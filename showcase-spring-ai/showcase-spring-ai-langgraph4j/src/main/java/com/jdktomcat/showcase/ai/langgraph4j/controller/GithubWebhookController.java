package com.jdktomcat.showcase.ai.langgraph4j.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/github")
public class GithubWebhookController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestHeader(value = "X-GitHub-Event", required = false) String event, @RequestBody String payload) throws Exception {
        if (!"push".equals(event)) {
            return ResponseEntity.ok("ignored");
        }
        log.info("GithubWebhookController receiveWebhook,payload:{}", payload);
        JsonNode root = objectMapper.readTree(payload);
        String ref = root.path("ref").asText();
        String repo = root.path("repository").path("full_name").asText();
        for (JsonNode commit : root.path("commits")) {
            String id = commit.path("id").asText();
            String message = commit.path("message").asText();
            String author = commit.path("author").path("name").asText();
            log.info("repo={}, ref={} , commit={}, author={} message={}", repo, ref, id, author, message);
        }
        return ResponseEntity.ok("success");
    }
}
