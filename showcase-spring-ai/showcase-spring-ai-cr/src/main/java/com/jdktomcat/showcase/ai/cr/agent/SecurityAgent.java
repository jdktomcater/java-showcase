package com.jdktomcat.showcase.ai.cr.agent;

import com.jdktomcat.showcase.ai.cr.state.CommitState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SecurityAgent implements NodeAction<CommitState> {

    private final ChatModel chatModel;

    public SecurityAgent(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 根据 diff 生成规范审查报告
     */
    public CommitState review(CommitState state) {
        String prompt = String.format("""
                你是安全审计专家，依据 OWASP Top‑10 检查以下代码改动，返回 **中文** 的安全风险列表：
                - 可能的 SQL 注入
                - XSS、CSRF
                - 权限提升风险
                - 资源泄露（如未关闭流）
                
                Diff:
                %s
                """, state.getDiff());

        String review = chatModel.call(prompt);
        log.info("安全审查完成，报告长度 {} 字", review.length());
        return state.securityReport(review);
    }

    @Override
    public Map<String, Object> apply(CommitState commitState) {
        CommitState result = review(commitState);
        return result.toMap();
    }
}