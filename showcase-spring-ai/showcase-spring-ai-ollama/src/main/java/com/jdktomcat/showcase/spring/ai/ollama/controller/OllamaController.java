package com.jdktomcat.showcase.spring.ai.ollama.controller;

import com.jdktomcat.showcase.spring.ai.ollama.service.OllamaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ai")
public class OllamaController {

    private final OllamaService ollamaService;

    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/audit")
    public Mono<String> audit(@RequestBody String log) {
        String prompt = """
                你是支付系统审计专家。
                请分析以下日志，并给出：
                1. 风险等级
                2. 可能原因
                3. 是否可能资金不一致
                4. 建议

                日志：
                """ + log;
        return ollamaService.generate(prompt);
    }
}
