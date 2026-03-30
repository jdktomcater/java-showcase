package com.jdktomcat.showcase.ai.code.chunk.controller;

import com.jdktomcat.showcase.ai.code.chunk.service.CodeIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CodeIndexController {

    private final CodeIndexService codeIndexService;

    @PostMapping("/api/code/index")
    public Map<String, Object> index() throws IOException {
        int count = codeIndexService.fullIndex();
        return Map.of(
                "success", true,
                "indexedChunks", count
        );
    }
}
