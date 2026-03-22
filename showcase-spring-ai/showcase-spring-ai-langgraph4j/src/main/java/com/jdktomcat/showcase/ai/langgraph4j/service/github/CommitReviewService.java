package com.jdktomcat.showcase.ai.langgraph4j.service.github;

import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.CommitTaskState;
import com.jdktomcat.showcase.ai.langgraph4j.domain.entity.CompareResponse;
import com.jdktomcat.showcase.ai.langgraph4j.domain.entity.PushPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bsc.langgraph4j.CompiledGraph;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommitReviewService {

    @Qualifier("commitWorkflow")
    private final CompiledGraph<CommitTaskState> commitWorkflow;

    @Value("${github.review.max-diff-length:12000}")
    private int maxDiffLength;

    public CommitTaskState reviewPush(PushPayload pushPayload, CompareResponse compareResponse) {
        CommitTaskState initialState = buildInitialState(pushPayload, compareResponse);
        CommitTaskState finalState = commitWorkflow.invoke(initialState.toMap()).orElse(initialState);
        log.info("提交评审完成 repository={} sha={} decision={}",
                finalState.getRepository(), finalState.getSha(), finalState.getDecision());
        return finalState;
    }

    private CommitTaskState buildInitialState(PushPayload pushPayload, CompareResponse compareResponse) {
        CommitTaskState state = new CommitTaskState();
        PushPayload.Commit latestCommit = getLatestCommit(pushPayload.getCommits());
        List<CompareResponse.FileDiff> files = compareResponse == null ? List.of() : compareResponse.getFiles();

        state.setRepository(pushPayload.getRepository() == null ? "" : pushPayload.getRepository().getFullName());
        state.setBranch(extractBranch(pushPayload.getRef()));
        state.setSha(pushPayload.getAfter());
        state.setAuthor(latestCommit != null && latestCommit.getAuthor() != null ? latestCommit.getAuthor().getName() : "");
        state.setEmail(latestCommit != null && latestCommit.getAuthor() != null ? latestCommit.getAuthor().getEmail() : "");
        state.setMessage(buildCommitMessage(pushPayload.getCommits()));
        state.setCompareUrl(pushPayload.getCompare());
        state.setChangedFiles(files.size());
        state.setAdditions(sumAdditions(files));
        state.setDeletions(sumDeletions(files));
        state.setDiff(buildDiff(compareResponse));
        state.setNeedRetry(false);
        state.setPassed(false);
        state.setDecision("PENDING");
        return state;
    }

    private PushPayload.Commit getLatestCommit(List<PushPayload.Commit> commits) {
        if (commits == null || commits.isEmpty()) {
            return null;
        }
        return commits.get(commits.size() - 1);
    }

    private String buildCommitMessage(List<PushPayload.Commit> commits) {
        if (commits == null || commits.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (PushPayload.Commit commit : commits) {
            if (StringUtils.isBlank(commit.getMessage())) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append("\n---\n");
            }
            builder.append(commit.getMessage().trim());
        }
        return builder.toString();
    }

    private Integer sumAdditions(List<CompareResponse.FileDiff> files) {
        if (files == null) {
            return 0;
        }
        return files.stream()
                .map(CompareResponse.FileDiff::getAdditions)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }

    private Integer sumDeletions(List<CompareResponse.FileDiff> files) {
        if (files == null) {
            return 0;
        }
        return files.stream()
                .map(CompareResponse.FileDiff::getDeletions)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }

    private String buildDiff(CompareResponse compareResponse) {
        if (compareResponse == null || compareResponse.getFiles() == null || compareResponse.getFiles().isEmpty()) {
            return "No file diff available.";
        }

        StringBuilder builder = new StringBuilder();
        for (CompareResponse.FileDiff file : compareResponse.getFiles()) {
            builder.append("File: ").append(file.getFilename()).append('\n')
                    .append("Status: ").append(file.getStatus()).append('\n')
                    .append("Additions: ").append(Objects.toString(file.getAdditions(), "0")).append('\n')
                    .append("Deletions: ").append(Objects.toString(file.getDeletions(), "0")).append('\n');

            if (StringUtils.isNotBlank(file.getPatch())) {
                builder.append(file.getPatch()).append('\n');
            } else {
                builder.append("[patch omitted by GitHub compare API]").append('\n');
            }
            builder.append('\n');

            if (builder.length() >= maxDiffLength) {
                builder.append("[diff truncated to fit model context]");
                break;
            }
        }

        if (builder.length() > maxDiffLength) {
            return builder.substring(0, maxDiffLength);
        }
        return builder.toString();
    }

    private String extractBranch(String ref) {
        if (StringUtils.isBlank(ref)) {
            return "";
        }
        return ref.replace("refs/heads/", "");
    }
}
