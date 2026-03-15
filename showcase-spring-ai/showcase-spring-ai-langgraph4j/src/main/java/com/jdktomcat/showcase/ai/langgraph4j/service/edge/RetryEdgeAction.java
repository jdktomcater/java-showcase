package com.jdktomcat.showcase.ai.langgraph4j.service.edge;

import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.AnalysisTaskState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.EdgeAction;
import org.springframework.stereotype.Component;

/**
 * @author zhoujun09@kuaishou.com
 * Created on 2025-11-27
 */
@Slf4j
@Component
public class RetryEdgeAction implements EdgeAction<AnalysisTaskState> {

    @Override
    public String apply(AnalysisTaskState analysisTaskState) throws Exception {
        return analysisTaskState.isNeedRetry() ? "retry" : "end";
    }
}
