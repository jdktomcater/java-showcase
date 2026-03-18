package com.jdktomcat.showcase.ai.langgraph4j.domain.dto;

import com.jdktomcat.showcase.ai.langgraph4j.utils.JSONUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bsc.langgraph4j.state.AgentState;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommitTaskState extends AgentState {
    private String repository;
    private String branch;
    private String sha;
    private String author;
    private String email;
    private String message;
    private String diff;
    private String compareUrl;
    private Integer changedFiles;
    private Integer additions;
    private Integer deletions;
    // 下面的字段会在各 Agent 中填充
    private String conventionReport;
    private String securityReport;
    private String performanceReport;
    private String businessReport;
    private boolean needRetry;
    private boolean passed;
    private String decision;
    private String finalReport;
    private String telegramMessage;

    public CommitTaskState() {
        super(new HashMap<>());
    }

    public CommitTaskState(Map<String, Object> data) {
        super(data);
        CommitTaskState oldState = JSONUtils.parseObject(JSONUtils.toJSONString(data), CommitTaskState.class);
        if (oldState == null) {
            return;
        }
        this.repository = oldState.getRepository();
        this.branch = oldState.getBranch();
        this.sha = oldState.getSha();
        this.author = oldState.getAuthor();
        this.email = oldState.getEmail();
        this.message = oldState.getMessage();
        this.diff = oldState.getDiff();
        this.compareUrl = oldState.getCompareUrl();
        this.changedFiles = oldState.getChangedFiles();
        this.additions = oldState.getAdditions();
        this.deletions = oldState.getDeletions();
        this.conventionReport = oldState.getConventionReport();
        this.securityReport = oldState.getSecurityReport();
        this.performanceReport = oldState.getPerformanceReport();
        this.businessReport = oldState.getBusinessReport();
        this.needRetry = oldState.isNeedRetry();
        this.passed = oldState.isPassed();
        this.decision = oldState.getDecision();
        this.finalReport = oldState.getFinalReport();
        this.telegramMessage = oldState.getTelegramMessage();
    }

    public Map<String, Object> toMap() {
        return JSONUtils.convertToMap(this);
    }

    // 状态更新器（不可变对象，每次更新返回新实例）
    public CommitTaskState conventionReport(String conventionReport) {
        this.setConventionReport(conventionReport);
        return this;
    }

    public CommitTaskState securityReport(String securityReport) {
        this.setSecurityReport(securityReport);
        return this;
    }

    public CommitTaskState performanceReport(String performanceReport) {
        this.setPerformanceReport(performanceReport);
        return this;
    }

    public CommitTaskState businessReport(String businessReport) {
        this.setBusinessReport(businessReport);
        return this;
    }

    public CommitTaskState finalReport(String finalReport) {
        this.setFinalReport(finalReport);
        return this;
    }

    public CommitTaskState withDecision(String decision) {
        this.setDecision(decision);
        return this;
    }

    public CommitTaskState withPassed(boolean passed) {
        this.setPassed(passed);
        return this;
    }

    public CommitTaskState telegramMessage(String telegramMessage) {
        this.setTelegramMessage(telegramMessage);
        return this;
    }

    public CommitTaskState withNeedRetry(boolean needRetry) {
        this.setNeedRetry(needRetry);
        return this;
    }
}