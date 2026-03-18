package com.jdktomcat.showcase.ai.langgraph4j.service.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdktomcat.showcase.ai.langgraph4j.entry.GithubCompareResponse;
import com.jdktomcat.showcase.ai.langgraph4j.entry.GithubPushPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;

@Service
@RequiredArgsConstructor
public class GithubWebhookService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${github.webhook.secret}")
    private String webhookSecret;

    @Value("${github.api.token}")
    private String githubToken;

    public boolean verifySignature(String payload, String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }

        String expected = "sha256=" + hmacSha256(payload, webhookSecret);
        return expected.equals(signatureHeader);
    }

    public void handlePushEvent(String payload) {
        try {
            GithubPushPayload pushPayload = objectMapper.readValue(payload, GithubPushPayload.class);

            String fullName = pushPayload.getRepository().getFullName();
            String before = pushPayload.getBefore();
            String after = pushPayload.getAfter();

            GithubCompareResponse compareResponse = fetchCompare(fullName, before, after);

            System.out.println("Repository: " + fullName);
            System.out.println("Branch: " + pushPayload.getRef());
            System.out.println("Compare commits: " + compareResponse.getTotalCommits());

            if (compareResponse.getFiles() != null) {
                for (GithubCompareResponse.FileDiff file : compareResponse.getFiles()) {
                    System.out.println("==== file ====");
                    System.out.println("filename: " + file.getFilename());
                    System.out.println("status: " + file.getStatus());
                    System.out.println("additions: " + file.getAdditions());
                    System.out.println("deletions: " + file.getDeletions());
                    System.out.println("changes: " + file.getChanges());
                    System.out.println("patch:\n" + file.getPatch());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to handle push event", e);
        }
    }

    public GithubCompareResponse fetchCompare(String fullName, String before, String after) {
        String url = "https://api.github.com/repos/" + fullName + "/compare/" + before + "..." + after;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.add("X-GitHub-Api-Version", "2022-11-28");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GithubCompareResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                GithubCompareResponse.class
        );

        return response.getBody();
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
