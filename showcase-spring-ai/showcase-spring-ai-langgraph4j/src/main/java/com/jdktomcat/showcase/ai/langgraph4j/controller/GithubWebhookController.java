package com.jdktomcat.showcase.ai.langgraph4j.controller;

import com.jdktomcat.showcase.ai.langgraph4j.service.github.GithubWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GithubWebhookController {

    private final GithubWebhookService githubWebhookService;

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestHeader(value = "X-GitHub-Event", required = false) String event,
                                                 @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
                                                 @RequestBody String payload) {
        if (!"push".equals(event)) {
            return ResponseEntity.ok("ignored");
        }
        boolean valid = githubWebhookService.verifySignature(payload, signature);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid signature");
        }
        githubWebhookService.handlePushEvent(payload);
        return ResponseEntity.ok("success");
    }
}
