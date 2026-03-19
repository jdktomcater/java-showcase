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
    public void review(CommitTaskState state) {
        String prompt = String.format("""
        你是业务风险审查专家，请基于提交信息和代码 diff 识别业务逻辑层面的变更风险。
        请严格使用中文，并按照以下 Markdown 结构输出：
        ## 结论
        风险等级：低风险 / 中风险 / 高风险
        
        ## 发现
        - 最多 3 条，聚焦业务流程、边界条件、兼容性、回滚影响
        
        ## 建议
        - 最多 3 条，可执行建议；如果没有明显风险，明确写“未发现阻断发布的业务风险”
        
        提交上下文：
        - 仓库：%s
        - 分支：%s
        - 提交说明：%s
        
        Diff:
        %s
        """, state.getRepository(), state.getBranch(), state.getMessage(), state.getDiff());
        String review = chatModel.call(prompt);
        log.info("业务完整审查完成，报告长度 {} 字", review.length());
        state.setBusinessReport(review);
    }

    @Override
    public Map<String, Object> apply(CommitTaskState commitTaskState) throws Exception {
        this.review(commitTaskState);
        return commitTaskState.toMap();
    }
}