package com.jdktomcat.showcase.data.neo4j.domain;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;

@Node("MomentPost")
public class MomentPost {

    @Id
    private String postId;

    private String content;

    private String topic;

    private String city;

    private LocalDateTime createdAt;

    public MomentPost() {
    }

    public MomentPost(String postId, String content, String topic, String city, LocalDateTime createdAt) {
        this.postId = postId;
        this.content = content;
        this.topic = topic;
        this.city = city;
        this.createdAt = createdAt;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
