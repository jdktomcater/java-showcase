package com.jdktomcat.showcase.spring.ai.mcp.llm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 大模型 + MCP 问答入口
 * <p>
 * 接受自然语言问题，由 LLM 自主选择 MCP 工具完成数据查询，再用自然语言作答。
 * <p>
 * 示例：
 * <pre>
 *   curl 'http://localhost:8092/ai/ask?question=数据库里有哪些表？'
 *   curl -X POST 'http://localhost:8092/ai/ask' \
 *        -H 'Content-Type: text/plain' \
 *        --data '统计一下每个城市的用户数，按用户数倒序'
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AskController {

    private final ChatClient chatClient;

    @GetMapping("/ask")
    public Map<String, String> askGet(@RequestParam("question") String question) {
        return doAsk(question);
    }

    @PostMapping("/ask")
    public Map<String, String> askPost(@org.springframework.web.bind.annotation.RequestBody String question) {
        return doAsk(question);
    }

    private Map<String, String> doAsk(String question) {
        log.info("收到用户提问: {}", question);
        String answer = chatClient
                .prompt()
                .user(question)
                .call()
                .content();
        log.info("LLM 回答: {}", answer);
        return Map.of(
                "question", question,
                "answer", answer
        );
    }
}
