package com.jdktomcat.showcase.data.neo4j.domain;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Node("Person")
public class Person {

    @Id
    private String userId;

    private String name;

    private String city;

    private String bio;

    @Relationship(type = "FRIEND_WITH", direction = Relationship.Direction.OUTGOING)
    private Set<Friendship> friends = new HashSet<Friendship>();

    @Relationship(type = "PUBLISHED", direction = Relationship.Direction.OUTGOING)
    private Set<MomentPost> posts = new HashSet<MomentPost>();

    public Person() {
    }

    public Person(String userId, String name, String city, String bio) {
        this.userId = userId;
        this.name = name;
        this.city = city;
        this.bio = bio;
    }

    public void addFriend(Person friend, LocalDate since, int closeness) {
        this.friends.add(new Friendship(friend, since, closeness));
    }

    public void publish(MomentPost post) {
        this.posts.add(post);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Friendship> getFriends() {
        return friends;
    }

    public void setFriends(Set<Friendship> friends) {
        this.friends = friends;
    }

    public Set<MomentPost> getPosts() {
        return posts;
    }

    public void setPosts(Set<MomentPost> posts) {
        this.posts = posts;
    }
}
