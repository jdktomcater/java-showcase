package com.jdktomcat.showcase.ai.langgraph4j.service.config;

import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.AnalysisTaskState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.state.AgentStateFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class AnalysisTaskStateFactory implements AgentStateFactory<AnalysisTaskState> {
    @Override
    public AnalysisTaskState apply(Map<String, Object> stringObjectMap) {
        return new AnalysisTaskState(stringObjectMap);
    }
}
