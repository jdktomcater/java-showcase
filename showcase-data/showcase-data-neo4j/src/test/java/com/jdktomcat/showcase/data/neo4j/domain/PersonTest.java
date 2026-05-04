package com.jdktomcat.showcase.data.neo4j.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonTest {

    @Test
    void shouldBuildFriendAndPostRelationships() {
        Person alice = new Person("alice", "Alice", "Shanghai", "Product manager");
        Person bob = new Person("bob", "Bob", "Hangzhou", "Backend engineer");
        MomentPost post = new MomentPost("post-1", "Hello graph", "graph", "Hangzhou",
                LocalDateTime.of(2026, 5, 4, 10, 0));

        alice.addFriend(bob, LocalDate.of(2024, 1, 1), 95);
        bob.publish(post);

        assertEquals(1, alice.getFriends().size());
        Friendship friendship = alice.getFriends().iterator().next();
        assertEquals("bob", friendship.getFriend().getUserId());
        assertEquals(LocalDate.of(2024, 1, 1), friendship.getSince());
        assertEquals(95, friendship.getCloseness());
        assertEquals(1, bob.getPosts().size());
        assertEquals("post-1", bob.getPosts().iterator().next().getPostId());
    }
}
