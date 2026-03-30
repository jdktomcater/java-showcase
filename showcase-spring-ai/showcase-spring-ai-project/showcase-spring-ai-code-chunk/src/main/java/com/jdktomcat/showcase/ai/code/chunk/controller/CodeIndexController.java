package com.jdktomcat.showcase.ai.code.chunk.controller;

import com.jdktomcat.showcase.ai.code.chunk.dto.CodeSearchHit;
import com.jdktomcat.showcase.ai.code.chunk.dto.CodeSearchRequest;
import com.jdktomcat.showcase.ai.code.chunk.service.CodeIndexService;
import com.jdktomcat.showcase.ai.code.chunk.service.CodeSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CodeIndexController {

    private final CodeIndexService codeIndexService;

    private final CodeSearchService codeSearchService;

    @PostMapping("/api/code/index")
    public Map<String, Object> index() throws IOException {
        int count = codeIndexService.fullIndex();
        return Map.of(
                "success", true,
                "indexedChunks", count
        );
    }

    @PostMapping("/api/code/search")
    public Map<String, Object> search(@Valid @RequestBody CodeSearchRequest request) {
        List<CodeSearchHit> hits = codeSearchService.search(request.query(), request.resolvedTopK());
        return Map.of(
                "success", true,
                "count", hits.size(),
                "hits", hits
        );
    }
}
