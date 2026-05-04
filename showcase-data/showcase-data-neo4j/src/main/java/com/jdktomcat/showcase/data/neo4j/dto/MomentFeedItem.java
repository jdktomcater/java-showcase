package com.jdktomcat.showcase.data.neo4j.dto;

import java.time.LocalDateTime;

public class MomentFeedItem {

    private final String authorUserId;

    private final String authorName;

    private final String postId;

    private final String content;

    private final String topic;

    private final String city;

    private final LocalDateTime createdAt;

    public MomentFeedItem(String authorUserId, String authorName, String postId, String content,
                          String topic, String city, LocalDateTime createdAt) {
        this.authorUserId = authorUserId;
        this.authorName = authorName;
        this.postId = postId;
        this.content = content;
        this.topic = topic;
        this.city = city;
        this.createdAt = createdAt;
    }

    public String getAuthorUserId() {
        return authorUserId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public String getTopic() {
        return topic;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
