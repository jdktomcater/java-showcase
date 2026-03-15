package com.jdktomcat.showcase.ai.cr.state;

import com.jdktomcat.showcase.ai.cr.util.JSONUtils;
import lombok.Data;
import org.bsc.langgraph4j.state.AgentState;

import java.util.HashMap;
import java.util.Map;

@Data
public class CommitState extends AgentState {
    private String sha;
    private String author;
    private String email;
    private String message;
    private String diff;
    // 下面的字段会在各 Agent 中填充
    private String conventionReport;
    private String securityReport;
    private String performanceReport;
    private String businessReport;
    private boolean needRetry;
    private String finalReport;

    public CommitState() {
        super(new HashMap<>());
    }

    public CommitState(Map<String, Object> data) {
        super(data);
        CommitState oldState = JSONUtils.parseObject(JSONUtils.toJSONString(data), CommitState.class);
        if (oldState == null) {
            return;
        }
        this.sha = oldState.getSha();
        this.author = oldState.getAuthor();
        this.email = oldState.getEmail();
        this.message = oldState.getMessage();
        this.diff = oldState.getDiff();
        this.conventionReport = oldState.getConventionReport();
        this.securityReport = oldState.getSecurityReport();
        this.performanceReport = oldState.getPerformanceReport();
        this.businessReport = oldState.getBusinessReport();
        this.needRetry = oldState.isNeedRetry();
        this.finalReport = oldState.getFinalReport();
    }

    public Map<String, Object> toMap() {
        return JSONUtils.convertToMap(this);
    }

    // 状态更新器（不可变对象，每次更新返回新实例）
    public CommitState conventionReport(String conventionReport) {
        this.setConventionReport(conventionReport);
        return this;
    }

    public CommitState securityReport(String securityReport) {
        this.setSecurityReport(securityReport);
        return this;
    }

    public CommitState performanceReport(String performanceReport) {
        this.setPerformanceReport(performanceReport);
        return this;
    }

    public CommitState businessReport(String businessReport) {
        this.setBusinessReport(businessReport);
        return this;
    }

    public CommitState finalReport(String finalReport) {
        this.setFinalReport(finalReport);
        return this;
    }

    public CommitState withNeedRetry(boolean needRetry) {
        this.setNeedRetry(needRetry);
        return this;
    }
}