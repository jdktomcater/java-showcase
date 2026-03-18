package com.jdktomcat.showcase.ai.langgraph4j.service.config;

import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.AnalysisTaskState;
import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.CommitTaskState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.state.AgentStateFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CommitTaskStateFactory implements AgentStateFactory<CommitTaskState> {
    @Override
    public CommitTaskState apply(Map<String, Object> stringObjectMap) {
        return new CommitTaskState(stringObjectMap);
    }
}
