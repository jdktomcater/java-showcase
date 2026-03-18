package com.jdktomcat.showcase.ai.langgraph4j.service.agent.commit;

import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.CommitTaskState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PerformanceAgent implements NodeAction<CommitTaskState> {

    private final ChatModel chatModel;

    public PerformanceAgent(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /** 根据 diff 生成规范审查报告 */
    public CommitTaskState review(CommitTaskState state) {
        String prompt = String.format("""
        你是性能优化顾问，请分析下面的代码改动，指出可能的性能瓶颈或不必要的计算，给出改进建议（使用 Markdown 表格展示）。
        Diff:
        %s
        """, state.getDiff());
        String review = chatModel.call(prompt);
        log.info("性能审查完成，报告长度 {} 字", review.length());
        return state.performanceReport(review);
    }

    @Override
    public Map<String, Object> apply(CommitTaskState commitState) {
        CommitTaskState result = review(commitState);
        return result.toMap();
    }
}