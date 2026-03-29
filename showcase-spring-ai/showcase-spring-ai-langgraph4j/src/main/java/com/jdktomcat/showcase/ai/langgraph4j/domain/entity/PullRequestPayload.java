package com.jdktomcat.showcase.ai.langgraph4j.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestPayload {

    private String action;
    private Integer number;
    private Repository repository;

    @JsonProperty("pull_request")
    private PullRequest pullRequest;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {
        @JsonProperty("full_name")
        private String fullName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequest {
        private String title;
        private String body;

        @JsonProperty("html_url")
        private String htmlUrl;

        @JsonProperty("diff_url")
        private String diffUrl;

        private BranchRef head;
        private BranchRef base;
        private User user;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BranchRef {
        private String ref;
        private String sha;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String login;
    }
}
