package com.jdktomcat.showcase.ai.langgraph4j.entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GithubCompareResponse {

    @JsonProperty("total_commits")
    private Integer totalCommits;

    private List<FileDiff> files;

    @Data
    public static class FileDiff {
        private String filename;
        private String status;
        private Integer additions;
        private Integer deletions;
        private Integer changes;
        private String patch;
    }
}
