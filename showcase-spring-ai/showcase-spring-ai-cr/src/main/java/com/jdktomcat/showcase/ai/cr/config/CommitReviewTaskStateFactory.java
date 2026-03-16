package com.jdktomcat.showcase.ai.cr.config;

import com.jdktomcat.showcase.ai.cr.state.CommitState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.state.AgentStateFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CommitReviewTaskStateFactory implements AgentStateFactory<CommitState> {
    @Override
    public CommitState apply(Map<String, Object> stringObjectMap) {
        return new CommitState(stringObjectMap);
    }
}
