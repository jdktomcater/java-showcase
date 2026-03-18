package com.jdktomcat.showcase.ai.langgraph4j.entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GithubPushPayload {

    private String ref;
    private String before;
    private String after;
    private Repository repository;
    private List<Commit> commits;

    @Data
    public static class Repository {
        @JsonProperty("full_name")
        private String fullName;
    }

    @Data
    public static class Commit {
        private String id;
        private String message;
        private Author author;
        private List<String> added;
        private List<String> modified;
        private List<String> removed;
    }

    @Data
    public static class Author {
        private String name;
        private String email;
    }
}
