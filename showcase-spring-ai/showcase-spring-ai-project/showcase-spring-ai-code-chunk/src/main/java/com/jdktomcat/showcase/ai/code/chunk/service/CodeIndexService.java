package com.jdktomcat.showcase.ai.code.chunk.service;

import com.jdktomcat.showcase.ai.code.chunk.domain.CodeChunk;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CodeIndexService {

    private final CodeRepositoryScanner scanner;
    private final JavaCodeChunker javaCodeChunker;
    private final VectorStore vectorStore;

    public int fullIndex() throws IOException {
        List<Path> files = scanner.scan();
        List<Document> documents = new ArrayList<>();
        for (Path file : files) {
            String fileName = file.getFileName().toString();
            if (!fileName.endsWith(".java")) {
                continue;
            }
            List<CodeChunk> chunks = javaCodeChunker.chunk(file);
            for (CodeChunk chunk : chunks) {
                documents.add(toDocument(chunk));
            }
        }

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }
        return documents.size();
    }

    private Document toDocument(CodeChunk chunk) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("chunkId", chunk.id());
        metadata.put("repo", chunk.repo());
        metadata.put("path", chunk.path());
        metadata.put("language", chunk.language());
        metadata.put("packageName", empty(chunk.packageName()));
        metadata.put("className", empty(chunk.className()));
        metadata.put("methodName", empty(chunk.methodName()));
        metadata.put("symbolType", chunk.symbolType());
        metadata.put("startLine", chunk.startLine());
        metadata.put("endLine", chunk.endLine());
        metadata.put("chunkHash", chunk.chunkHash());
        return new Document(
                chunk.toEmbeddingText(),
                metadata
        );
    }

    private String empty(String value) {
        return value == null ? "" : value;
    }
}
