package com.jdktomcat.showcase.ai.langgraph4j.service.agent.commit;

import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.CommitTaskState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class BusinessAgent implements NodeAction<CommitTaskState> {

    private final ChatModel chatModel;

    public BusinessAgent(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /** 根据 diff 生成规范审查报告 */
    public CommitTaskState review(CommitTaskState state) {
        String prompt = String.format("""
        你是业务领域专家，请判断本次改动是否满足以下业务需求（从用户故事或需求文档中抽取要点），若缺失关键业务流程，请在报告中标记。
        业务需求示例：
        %s   // 这里可以从 commit message 里抽取或单独传入
        Diff:
        %s
        """, state.getMessage(), state.getDiff());
        String review = chatModel.call(prompt);
        log.info("业务完整审查完成，报告长度 {} 字", review.length());
        return state.businessReport(review);
    }

    @Override
    public Map<String, Object> apply(CommitTaskState commitTaskState) throws Exception {
        CommitTaskState result = this.review(commitTaskState);
        return result.toMap();
    }
}