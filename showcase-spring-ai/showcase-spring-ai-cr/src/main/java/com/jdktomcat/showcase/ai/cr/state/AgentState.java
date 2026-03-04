package com.jdktomcat.showcase.ai.cr.state;

import java.util.Map;

public class AgentState extends MapGraphState {
    public AgentState(Map<String, Object> initData) {
        super(initData);
    }
    // 可以定义特定的 Key，例如消息列表
    public static final String MESSAGES = "messages";
}
