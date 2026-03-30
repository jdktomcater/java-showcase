package com.jdktomcat.showcase.ai.code.chunk.service;

import com.jdktomcat.showcase.ai.code.chunk.repository.Neo4jGraphRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DependencyIndexService {

    private static final Logger log = LoggerFactory.getLogger(DependencyIndexService.class);

    private final CodeRepositoryScanner scanner;
    private final JavaDependencyAnalyzer analyzer;
    private final Neo4jGraphRepository graphRepository;

    public Map<String, Object> fullIndex() throws IOException {
        List<Path> files = scanner.scan();
        int fileCount = 0;
        int nodeCount = 0;
        int relationCount = 0;
        int skippedFiles = 0;

        for (Path file : files) {
            if (!file.getFileName().toString().endsWith(".java")) {
                continue;
            }

            try {
                JavaDependencyAnalyzer.AnalysisResult result = analyzer.analyze(file);
                graphRepository.saveAll(result.nodes(), result.relations());
                fileCount++;
                nodeCount += result.nodes().size();
                relationCount += result.relations().size();
            } catch (Exception ex) {
                skippedFiles++;
                log.warn("Skip dependency analysis for {}", file, ex);
            }
        }

        return Map.of(
                "success", true,
                "files", fileCount,
                "nodes", nodeCount,
                "relations", relationCount,
                "skippedFiles", skippedFiles
        );
    }
}
