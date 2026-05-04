package com.jdktomcat.showcase.data.neo4j.service;

import com.jdktomcat.showcase.data.neo4j.domain.MomentPost;
import com.jdktomcat.showcase.data.neo4j.domain.Person;
import com.jdktomcat.showcase.data.neo4j.dto.MomentFeedItem;
import com.jdktomcat.showcase.data.neo4j.dto.PersonSummary;
import com.jdktomcat.showcase.data.neo4j.repository.PersonRepository;
import org.neo4j.driver.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class FriendCircleService {

    private final PersonRepository personRepository;

    private final Neo4jClient neo4jClient;

    public FriendCircleService(PersonRepository personRepository, Neo4jClient neo4jClient) {
        this.personRepository = personRepository;
        this.neo4jClient = neo4jClient;
    }

    @Transactional
    public void resetSampleGraph() {
        neo4jClient.query("MATCH (n) WHERE n:Person OR n:MomentPost DETACH DELETE n").run();

        Person alice = new Person("alice", "Alice", "Shanghai", "Product manager");
        Person bob = new Person("bob", "Bob", "Hangzhou", "Backend engineer");
        Person cathy = new Person("cathy", "Cathy", "Shanghai", "Designer");
        Person david = new Person("david", "David", "Suzhou", "Data engineer");
        Person emma = new Person("emma", "Emma", "Nanjing", "Photographer");
        Person frank = new Person("frank", "Frank", "Beijing", "Architect");

        alice.addFriend(bob, LocalDate.of(2021, 3, 12), 90);
        alice.addFriend(cathy, LocalDate.of(2020, 7, 3), 82);
        alice.addFriend(frank, LocalDate.of(2023, 1, 18), 70);
        bob.addFriend(david, LocalDate.of(2022, 5, 21), 78);
        bob.addFriend(emma, LocalDate.of(2021, 11, 8), 74);
        cathy.addFriend(david, LocalDate.of(2019, 9, 30), 88);
        frank.addFriend(emma, LocalDate.of(2023, 6, 6), 65);

        bob.publish(new MomentPost("post-bob-1", "Refactored a slow graph query today.", "work", "Hangzhou",
                LocalDateTime.of(2026, 5, 3, 21, 15)));
        cathy.publish(new MomentPost("post-cathy-1", "New visual draft for the friend-circle feed.", "design", "Shanghai",
                LocalDateTime.of(2026, 5, 4, 9, 30)));
        david.publish(new MomentPost("post-david-1", "Neo4j path queries are very handy for recommendations.", "graph", "Suzhou",
                LocalDateTime.of(2026, 5, 2, 20, 5)));
        emma.publish(new MomentPost("post-emma-1", "Sunset near the river after work.", "life", "Nanjing",
                LocalDateTime.of(2026, 5, 1, 18, 40)));
        frank.publish(new MomentPost("post-frank-1", "Sketched a service topology for a new project.", "architecture", "Beijing",
                LocalDateTime.of(2026, 5, 4, 8, 10)));

        personRepository.saveAll(Arrays.asList(alice, bob, cathy, david, emma, frank));
    }

    @Transactional(readOnly = true)
    public List<PersonSummary> listPeople() {
        return toSummaries(personRepository.findAllByOrderByNameAsc());
    }

    @Transactional(readOnly = true)
    public List<PersonSummary> findDirectFriends(String userId) {
        return toSummaries(personRepository.findDirectFriends(userId));
    }

    @Transactional(readOnly = true)
    public List<PersonSummary> findFriendSuggestions(String userId) {
        return toSummaries(personRepository.findSecondDegreeFriends(userId));
    }

    @Transactional(readOnly = true)
    public List<PersonSummary> findMutualFriends(String firstUserId, String secondUserId) {
        return toSummaries(personRepository.findMutualFriends(firstUserId, secondUserId));
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

    private List<PersonSummary> toSummaries(Collection<Person> people) {
        List<PersonSummary> summaries = new ArrayList<PersonSummary>();
        for (Person person : people) {
            summaries.add(PersonSummary.from(person));
        }
        return summaries;
    }

    private LocalDateTime toLocalDateTime(Value value) {
        return value.asLocalDateTime();
    }
}
