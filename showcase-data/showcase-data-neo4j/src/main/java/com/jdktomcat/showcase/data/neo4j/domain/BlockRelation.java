package com.jdktomcat.showcase.data.neo4j.domain;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
public class BlockRelation {

    @RelationshipId
    private Long id;

    @TargetNode
    private Person blocked;

    private LocalDateTime blockedAt;

    public BlockRelation() {
    }

    public BlockRelation(Person blocked, LocalDateTime blockedAt) {
        this.blocked = blocked;
        this.blockedAt = blockedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getBlocked() {
        return blocked;
    }

    public void setBlocked(Person blocked) {
        this.blocked = blocked;
    }

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }
}