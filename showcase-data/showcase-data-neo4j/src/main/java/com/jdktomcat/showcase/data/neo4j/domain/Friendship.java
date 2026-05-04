package com.jdktomcat.showcase.data.neo4j.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDate;

@RelationshipProperties
public class Friendship {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private Person friend;

    private LocalDate since;

    private int closeness;

    public Friendship() {
    }

    public Friendship(Person friend, LocalDate since, int closeness) {
        this.friend = friend;
        this.since = since;
        this.closeness = closeness;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getFriend() {
        return friend;
    }

    public void setFriend(Person friend) {
        this.friend = friend;
    }

    public LocalDate getSince() {
        return since;
    }

    public void setSince(LocalDate since) {
        this.since = since;
    }

    public int getCloseness() {
        return closeness;
    }

    public void setCloseness(int closeness) {
        this.closeness = closeness;
    }
}
