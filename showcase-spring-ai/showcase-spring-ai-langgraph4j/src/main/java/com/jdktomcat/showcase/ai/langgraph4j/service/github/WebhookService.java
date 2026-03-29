package com.jdktomcat.showcase.ai.langgraph4j.service.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.CommitTaskState;
import com.jdktomcat.showcase.ai.langgraph4j.domain.entity.CompareResponse;
import com.jdktomcat.showcase.ai.langgraph4j.domain.entity.PullRequestPayload;
import com.jdktomcat.showcase.ai.langgraph4j.domain.entity.PushPayload;
import com.jdktomcat.showcase.ai.langgraph4j.service.telegram.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private static final Set<String> REVIEWABLE_PULL_REQUEST_ACTIONS = Set.of("opened", "reopened", "synchronize", "ready_for_review");

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final CommitReviewService commitReviewService;
    private final GitHubReviewPublisher gitHubReviewPublisher;
    private final TelegramNotificationService telegramNotificationService;

    @Value("${github.webhook.secret:}")
    private String webhookSecret;

    @Value("${github.api.token:}")
    private String githubToken;

    public boolean verifySignature(String payload, String signatureHeader) {
        if (StringUtils.isBlank(webhookSecret)) {
            log.warn("GitHub webhook secret 未配置，当前跳过签名校验");
            return true;
        }
        if (signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }
        String expected = "sha256=" + hmacSha256(payload, webhookSecret);
        return expected.equals(signatureHeader);
    }

    @Async("webhookTaskExecutor")
    public void handleWebhookEvent(String event, String payload) {
        try {
            if ("push".equals(event)) {
                handlePushEvent(payload);
                return;
            }
            if ("pull_request".equals(event)) {
                handlePullRequestEvent(payload);
                return;
            }
            log.info("忽略未支持的 GitHub 事件 event={}", event);
        } catch (Exception e) {
            log.error("GitHub webhook 处理失败 event={}", event, e);
        }
    }

    private void handlePushEvent(String payload) throws Exception {
        PushPayload pushPayload = objectMapper.readValue(payload, PushPayload.class);
        String fullName = pushPayload.getRepository().getFullName();
        String before = pushPayload.getBefore();
        String after = pushPayload.getAfter();
        CompareResponse compareResponse = fetchCompare(fullName, before, after);
        CommitTaskState finalState = commitReviewService.reviewPush(pushPayload, compareResponse);
        publishReviewResult(finalState);
        log.info("GitHub push 处理完成 repository={} branch={} decision={}", fullName, pushPayload.getRef(), finalState.getDecision());
    }

    private void handlePullRequestEvent(String payload) throws Exception {
        PullRequestPayload pullRequestPayload = objectMapper.readValue(payload, PullRequestPayload.class);
        if (!REVIEWABLE_PULL_REQUEST_ACTIONS.contains(pullRequestPayload.getAction())) {
            log.info("忽略 PR 事件 action={} pr={}", pullRequestPayload.getAction(), pullRequestPayload.getNumber());
            return;
        }
        String fullName = pullRequestPayload.getRepository().getFullName();
        Integer number = pullRequestPayload.getNumber();
        List<CompareResponse.FileDiff> files = fetchPullRequestFiles(fullName, number);
        CommitTaskState finalState = commitReviewService.reviewPullRequest(pullRequestPayload, files);
        publishReviewResult(finalState);
        log.info("GitHub PR 处理完成 repository={} pr={} decision={}",
                fullName, number, finalState.getDecision());
    }

    private void publishReviewResult(CommitTaskState finalState) {
        gitHubReviewPublisher.publishReviewResult(finalState);
        telegramNotificationService.sendCommitReview(finalState);
    }

    public CompareResponse fetchCompare(String fullName, String before, String after) {
        String url = "https://api.github.com/repos/" + fullName + "/compare/" + before + "..." + after;
        ResponseEntity<CompareResponse> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(githubHeaders()), CompareResponse.class);
        return response.getBody();
    }

    public List<CompareResponse.FileDiff> fetchPullRequestFiles(String fullName, Integer number) {
        List<CompareResponse.FileDiff> files = new ArrayList<>();
        for (int page = 1; page <= 10; page++) {
            String url = "https://api.github.com/repos/" + fullName + "/pulls/" + number + "/files?per_page=100&page=" + page;
            ResponseEntity<CompareResponse.FileDiff[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(githubHeaders()),
                    CompareResponse.FileDiff[].class
            );
            CompareResponse.FileDiff[] currentPage = response.getBody();
            if (currentPage == null || currentPage.length == 0) {
                break;
            }
            files.addAll(List.of(currentPage));
            if (currentPage.length < 100) {
                break;
            }
        }
        return files;
    }

    private HttpHeaders githubHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        return headers;
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC calculation failed", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
