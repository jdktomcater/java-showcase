package com.jdktomcat.showcase.ai.langgraph4j.service.github;

import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.CommitTaskState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubReviewPublisher {

    private static final int COMMIT_STATUS_DESCRIPTION_LIMIT = 140;
    private static final int CHECK_RUN_SUMMARY_LIMIT = 65535;

    private final RestTemplate restTemplate;

    @Value("${github.api.token:}")
    private String githubToken;

    @Value("${github.review.publish-check-run:false}")
    private boolean publishCheckRun;

    @Value("${github.review.publish-status:true}")
    private boolean publishStatus;

    @Value("${github.review.publish-pr-comment:true}")
    private boolean publishPrComment;

    @Value("${github.review.publish-pr-review:true}")
    private boolean publishPrReview;

    public void publishReviewResult(CommitTaskState state) {
        if (StringUtils.isBlank(githubToken)) {
            log.warn("GitHub token 未配置，跳过回写评审结果");
            return;
        }
        if (publishCheckRun) {
            safePublish("check run", () -> publishCheckRun(state));
        }
        if (publishStatus) {
            safePublish("commit status", () -> publishCommitStatus(state));
        }
        if (publishPrReview && state.getPullRequestNumber() != null) {
            safePublish("pull request review", () -> publishPullRequestReview(state));
            return;
        }
        if (publishPrComment && state.getPullRequestNumber() != null) {
            safePublish("pull request comment", () -> publishPullRequestComment(state));
        }
    }

    private void publishCheckRun(CommitTaskState state) {
        if (StringUtils.isAnyBlank(state.getRepository(), state.getSha())) {
            log.warn("仓库或 sha 为空，跳过 check run 回写 repository={} sha={}", state.getRepository(), state.getSha());
            return;
        }
        String url = "https://api.github.com/repos/" + state.getRepository() + "/check-runs";
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("title", "LangGraph4j AI 代码审查");
        output.put("summary", truncateForCheckRun(buildCheckRunSummary(state)));
        output.put("text", truncateForCheckRun(buildCheckRunDetails(state)));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "langgraph4j/code-review");
        body.put("head_sha", state.getSha());
        body.put("status", "completed");
        body.put("conclusion", state.isPassed() ? "success" : "failure");
        body.put("details_url", StringUtils.defaultIfBlank(state.getReviewUrl(), state.getCompareUrl()));
        body.put("external_id", buildExternalId(state));
        body.put("output", output);
        exchange(url, body);
        log.info("GitHub check run 已回写 repository={} sha={} decision={}",
                state.getRepository(), state.getSha(), state.getDecision());
    }

    private void publishCommitStatus(CommitTaskState state) {
        if (StringUtils.isAnyBlank(state.getRepository(), state.getSha())) {
            log.warn("仓库或 sha 为空，跳过提交状态回写 repository={} sha={}", state.getRepository(), state.getSha());
            return;
        }
        String url = "https://api.github.com/repos/" + state.getRepository() + "/statuses/" + state.getSha();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("state", state.isPassed() ? "success" : "failure");
        body.put("context", "langgraph4j/code-review");
        body.put("description", abbreviateDescription(state.getSummary()));
        body.put("target_url", StringUtils.defaultIfBlank(state.getReviewUrl(), state.getCompareUrl()));
        exchange(url, body);
        log.info("GitHub commit status 已回写 repository={} sha={} decision={}",
                state.getRepository(), state.getSha(), state.getDecision());
    }

    private void publishPullRequestReview(CommitTaskState state) {
        String url = "https://api.github.com/repos/" + state.getRepository() + "/pulls/" + state.getPullRequestNumber() + "/reviews";
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("body", buildReviewBody(state));
        body.put("event", state.isPassed() ? "COMMENT" : "REQUEST_CHANGES");
        exchange(url, body);
        log.info("GitHub PR review 已回写 repository={} pr={} decision={}",
                state.getRepository(), state.getPullRequestNumber(), state.getDecision());
    }

    private void publishPullRequestComment(CommitTaskState state) {
        String url = "https://api.github.com/repos/" + state.getRepository() + "/issues/" + state.getPullRequestNumber() + "/comments";
        Map<String, Object> body = Map.of("body", buildCommentBody(state));
        exchange(url, body);
        log.info("GitHub PR 评论已回写 repository={} pr={} decision={}",
                state.getRepository(), state.getPullRequestNumber(), state.getDecision());
    }

    private void exchange(String url, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }

    private String buildCommentBody(CommitTaskState state) {
        StringBuilder builder = new StringBuilder();
        builder.append("## AI 自动代码审查结果").append('\n').append("- 结论：").append(state.getDecision()).append('\n');
        if (StringUtils.isNotBlank(state.getSummary())) {
            builder.append("- 摘要：").append(state.getSummary()).append('\n');
        }
        builder.append("- 提交：`").append(state.getSha()).append("`").append('\n');
        if (StringUtils.isNotBlank(state.getFinalReport())) {
            builder.append('\n').append(state.getFinalReport()).append('\n');
        }
        if (StringUtils.isNotBlank(state.getReviewUrl())) {
            builder.append('\n').append("详情链接：").append(state.getReviewUrl());
        }
        return builder.toString();
    }

    private String buildReviewBody(CommitTaskState state) {
        StringBuilder builder = new StringBuilder();
        builder.append("## AI 自动代码审查").append('\n')
                .append("- 审查方式：LangGraph4j 多 Agent").append('\n')
                .append("- 结论：").append(state.getDecision()).append('\n');
        if (StringUtils.isNotBlank(state.getSummary())) {
            builder.append("- 摘要：").append(state.getSummary()).append('\n');
        }
        builder.append("- 提交：`").append(state.getSha()).append("`").append('\n');
        if (StringUtils.isNotBlank(state.getPullRequestTitle())) {
            builder.append("- PR：#").append(state.getPullRequestNumber()).append(" ").append(state.getPullRequestTitle()).append('\n');
        }
        builder.append('\n').append(buildSection("业务风险", state.getBusinessReport()))
                .append('\n').append(buildSection("代码规范", state.getConventionReport()))
                .append('\n').append(buildSection("性能风险", state.getPerformanceReport()))
                .append('\n').append(buildSection("安全风险", state.getSecurityReport()));
        if (StringUtils.isNotBlank(state.getFinalReport())) {
            builder.append('\n').append(state.getFinalReport()).append('\n');
        }
        return builder.toString();
    }

    private String buildCheckRunSummary(CommitTaskState state) {
        StringBuilder builder = new StringBuilder();
        builder.append("结论：").append(state.getDecision()).append('\n');
        if (StringUtils.isNotBlank(state.getSummary())) {
            builder.append("摘要：").append(state.getSummary()).append('\n');
        }
        builder.append("仓库：").append(state.getRepository()).append('\n')
                .append("分支：").append(state.getBranch()).append('\n')
                .append("提交：").append(state.getSha());
        return builder.toString();
    }

    private String buildCheckRunDetails(CommitTaskState state) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(state.getPullRequestTitle())) {
            builder.append("PR：#").append(state.getPullRequestNumber()).append(" ").append(state.getPullRequestTitle()).append("\n\n");
        }
        if (StringUtils.isNotBlank(state.getFinalReport())) {
            builder.append(state.getFinalReport()).append("\n\n");
        }
        builder.append(buildSection("业务风险", state.getBusinessReport())).append("\n\n")
                .append(buildSection("代码规范", state.getConventionReport())).append("\n\n")
                .append(buildSection("性能风险", state.getPerformanceReport())).append("\n\n")
                .append(buildSection("安全风险", state.getSecurityReport()));
        return builder.toString().trim();
    }

    private String buildSection(String title, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("### ").append(title).append('\n');
        if (StringUtils.isBlank(content)) {
            builder.append("暂无输出");
            return builder.toString();
        }
        builder.append(content.trim());
        return builder.toString();
    }

    private String buildExternalId(CommitTaskState state) {
        if (state.getPullRequestNumber() != null) {
            return "pr-" + state.getPullRequestNumber() + "-" + state.getSha();
        }
        return "commit-" + state.getSha();
    }

    private String truncateForCheckRun(String content) {
        if (content == null || content.length() <= CHECK_RUN_SUMMARY_LIMIT) {
            return content;
        }
        return content.substring(0, CHECK_RUN_SUMMARY_LIMIT - 3) + "...";
    }

    private void safePublish(String channel, Runnable publisher) {
        try {
            publisher.run();
        } catch (Exception ex) {
            log.warn("GitHub {} 回写失败，将继续执行其他回写渠道", channel, ex);
        }
    }

    private String abbreviateDescription(String summary) {
        String fallback = "AI 代码审查已完成";
        String resolved = StringUtils.defaultIfBlank(summary, fallback).replaceAll("\\s+", " ").trim();
        if (resolved.length() <= COMMIT_STATUS_DESCRIPTION_LIMIT) {
            return resolved;
        }
        return resolved.substring(0, COMMIT_STATUS_DESCRIPTION_LIMIT - 3) + "...";
    }
}
