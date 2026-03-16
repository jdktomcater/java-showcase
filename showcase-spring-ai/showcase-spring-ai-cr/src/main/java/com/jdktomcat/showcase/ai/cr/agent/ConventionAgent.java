package com.jdktomcat.showcase.ai.cr.agent;
import com.jdktomcat.showcase.ai.cr.state.CommitState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ConventionAgent implements NodeAction<CommitState> {

    private final ChatModel chatModel;

    public ConventionAgent(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /** 根据 diff 生成规范审查报告 */
    public CommitState review(CommitState state) {
        String prompt = String.format("""
                你是代码规范审查专家。请检查以下 diff，给出 **中文**（或你项目所在语言）的审查报告，包含：
                1. 是否遵循项目的 Java 编码规范（命名、缩进、异常处理等）；
                2. 是否符合 Checkstyle / SpotBugs 等工具的建议；
                3. 如有不符合，请在报告中用 **Markdown 列表** 给出改进建议。
                
                Diff:
                %s
                """, state.getDiff());

        String review = chatModel.call(prompt);
        log.info("规范审查完成，报告长度 {} 字", review.length());
        return state.conventionReport(review);
    }

    @Override
    public Map<String, Object> apply(CommitState commitState) {
        CommitState result = review(commitState);
        return result.toMap();
    }
}