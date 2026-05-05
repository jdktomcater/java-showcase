package com.jdktomcat.showcase.data.neo4j.service;

import com.jdktomcat.showcase.data.neo4j.dto.MomentFeedItem;
import com.jdktomcat.showcase.data.neo4j.dto.PersonSummary;
import org.neo4j.driver.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FriendCircleService {

    private final Neo4jClient neo4jClient;

    public FriendCircleService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Transactional
    public void resetSampleGraph() {
        neo4jClient.query("MATCH (n) WHERE n:Person OR n:MomentPost DETACH DELETE n").run();

        List<Map<String, Object>> people = Arrays.asList(
                person("alice", "Alice", "Shanghai", "Product manager"),
                person("bob", "Bob", "Hangzhou", "Backend engineer"),
                person("cathy", "Cathy", "Shanghai", "Designer"),
                person("david", "David", "Suzhou", "Data engineer"),
                person("emma", "Emma", "Nanjing", "Photographer"),
                person("frank", "Frank", "Beijing", "Architect"));
        neo4jClient.query("UNWIND $people AS person "
                        + "CREATE (:Person {userId: person.userId, name: person.name, city: person.city, bio: person.bio})")
                .bind(people).to("people")
                .run();

        List<Map<String, Object>> friendships = Arrays.asList(
                friendship("alice", "bob", LocalDate.of(2021, 3, 12), 90),
                friendship("alice", "cathy", LocalDate.of(2020, 7, 3), 82),
                friendship("alice", "frank", LocalDate.of(2023, 1, 18), 70),
                friendship("bob", "david", LocalDate.of(2022, 5, 21), 78),
                friendship("bob", "emma", LocalDate.of(2021, 11, 8), 74),
                friendship("cathy", "david", LocalDate.of(2019, 9, 30), 88),
                friendship("frank", "emma", LocalDate.of(2023, 6, 6), 65));
        neo4jClient.query("UNWIND $friendships AS friendship "
                        + "MATCH (from:Person {userId: friendship.fromUserId}) "
                        + "MATCH (to:Person {userId: friendship.toUserId}) "
                        + "CREATE (from)-[:FRIEND_WITH {since: friendship.since, closeness: friendship.closeness}]->(to)")
                .bind(friendships).to("friendships")
                .run();

        List<Map<String, Object>> posts = Arrays.asList(
                post("bob", "post-bob-1", "Refactored a slow graph query today.", "work", "Hangzhou",
                        LocalDateTime.of(2026, 5, 3, 21, 15)),
                post("cathy", "post-cathy-1", "New visual draft for the friend-circle feed.", "design", "Shanghai",
                        LocalDateTime.of(2026, 5, 4, 9, 30)),
                post("david", "post-david-1", "Neo4j path queries are very handy for recommendations.", "graph", "Suzhou",
                        LocalDateTime.of(2026, 5, 2, 20, 5)),
                post("emma", "post-emma-1", "Sunset near the river after work.", "life", "Nanjing",
                        LocalDateTime.of(2026, 5, 1, 18, 40)),
                post("frank", "post-frank-1", "Sketched a service topology for a new project.", "architecture", "Beijing",
                        LocalDateTime.of(2026, 5, 4, 8, 10)));
        neo4jClient.query("UNWIND $posts AS post "
                        + "MATCH (author:Person {userId: post.authorUserId}) "
                        + "CREATE (author)-[:PUBLISHED]->(:MomentPost {postId: post.postId, content: post.content, "
                        + "topic: post.topic, city: post.city, createdAt: post.createdAt})")
                .bind(posts).to("posts")
                .run();
    }

    @Transactional(readOnly = true)
    public List<PersonSummary> listPeople() {
        return fetchPersonSummaries("MATCH (person:Person) "
                + "RETURN person.userId AS userId, person.name AS name, person.city AS city, person.bio AS bio "
                + "ORDER BY person.name", Collections.<String, Object>emptyMap());
    }

    @Transactional(readOnly = true)
    public List<PersonSummary> findDirectFriends(String userId) {
        Map<String, Object> parameters = singletonParameter("userId", userId);
        return fetchPersonSummaries("MATCH (:Person {userId: $userId})-[:FRIEND_WITH]-(friend:Person) "
                + "RETURN DISTINCT friend.userId AS userId, friend.name AS name, friend.city AS city, friend.bio AS bio "
                + "ORDER BY name", parameters);
    }

    @Transactional(readOnly = true)
    public List<PersonSummary> findFriendSuggestions(String userId) {
        Map<String, Object> parameters = singletonParameter("userId", userId);
        return fetchPersonSummaries("MATCH (person:Person {userId: $userId})-[:FRIEND_WITH]-()-[:FRIEND_WITH]-(candidate:Person) "
                + "WHERE candidate <> person AND NOT (person)-[:FRIEND_WITH]-(candidate) "
                + "RETURN DISTINCT candidate.userId AS userId, candidate.name AS name, candidate.city AS city, candidate.bio AS bio "
                + "ORDER BY name", parameters);
    }

    @Transactional(readOnly = true)
    public List<PersonSummary> findMutualFriends(String firstUserId, String secondUserId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("firstUserId", firstUserId);
        parameters.put("secondUserId", secondUserId);
        return fetchPersonSummaries("MATCH (first:Person {userId: $firstUserId})-[:FRIEND_WITH]-(mutual:Person)-[:FRIEND_WITH]-(second:Person {userId: $secondUserId}) "
                + "WHERE first <> second "
                + "RETURN DISTINCT mutual.userId AS userId, mutual.name AS name, mutual.city AS city, mutual.bio AS bio "
                + "ORDER BY name", parameters);
    }

    @Transactional(readOnly = true)
    public List<MomentFeedItem> findFriendTimeline(String userId) {
        String cypher = "MATCH (:Person {userId: $userId})-[:FRIEND_WITH]-(friend:Person)-[:PUBLISHED]->(post:MomentPost) "
                + "RETURN friend.userId AS authorUserId, friend.name AS authorName, post.postId AS postId, "
                + "post.content AS content, post.topic AS topic, post.city AS city, post.createdAt AS createdAt "
                + "ORDER BY post.createdAt DESC";

        return new ArrayList<MomentFeedItem>(neo4jClient.query(cypher)
                .bind(userId).to("userId")
                .fetchAs(MomentFeedItem.class)
                .mappedBy((typeSystem, record) -> new MomentFeedItem(
                        record.get("authorUserId").asString(),
                        record.get("authorName").asString(),
                        record.get("postId").asString(),
                        record.get("content").asString(),
                        record.get("topic").asString(),
                        record.get("city").asString(),
                        toLocalDateTime(record.get("createdAt"))))
                .all());
    }

    private List<PersonSummary> fetchPersonSummaries(String cypher, Map<String, Object> parameters) {
        return new ArrayList<PersonSummary>(neo4jClient.query(cypher)
                .bindAll(parameters)
                .fetchAs(PersonSummary.class)
                .mappedBy((typeSystem, record) -> new PersonSummary(
                        record.get("userId").asString(),
                        record.get("name").asString(),
                        record.get("city").asString(),
                        record.get("bio").asString()))
                .all());
    }

    private Map<String, Object> person(String userId, String name, String city, String bio) {
        Map<String, Object> person = new HashMap<String, Object>();
        person.put("userId", userId);
        person.put("name", name);
        person.put("city", city);
        person.put("bio", bio);
        return person;
    }

    private Map<String, Object> friendship(String fromUserId, String toUserId, LocalDate since, int closeness) {
        Map<String, Object> friendship = new HashMap<String, Object>();
        friendship.put("fromUserId", fromUserId);
        friendship.put("toUserId", toUserId);
        friendship.put("since", since);
        friendship.put("closeness", closeness);
        return friendship;
    }

    private Map<String, Object> post(String authorUserId, String postId, String content, String topic, String city,
                                     LocalDateTime createdAt) {
        Map<String, Object> post = new HashMap<String, Object>();
        post.put("authorUserId", authorUserId);
        post.put("postId", postId);
        post.put("content", content);
        post.put("topic", topic);
        post.put("city", city);
        post.put("createdAt", createdAt);
        return post;
    }

    private Map<String, Object> singletonParameter(String name, Object value) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(name, value);
        return parameters;
    }

    private LocalDateTime toLocalDateTime(Value value) {
        return value.asLocalDateTime();
    }
}
