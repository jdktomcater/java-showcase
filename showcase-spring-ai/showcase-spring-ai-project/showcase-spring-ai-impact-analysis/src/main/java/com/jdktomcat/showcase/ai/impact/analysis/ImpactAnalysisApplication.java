package com.jdktomcat.showcase.ai.impact.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main application class for Impact Chain Dependency Analysis.
 * <p>
 * This module provides comprehensive code impact chain analysis for multi-layer data flows:
 * - HTTP interfaces (@RestController, @RequestMapping, etc.)
 * - RPC interfaces (@DubboService, @GrpcService, etc.)
 * - MQ consumers (@RabbitListener, @KafkaListener, etc.)
 * - Scheduled tasks (@Scheduled)
 * - Event-driven components (@EventListener)
 * <p>
 * Features:
 * - Automatic entry point detection via AST parsing and pattern matching
 * - Impact chain building and visualization
 * - Neo4j-based graph storage and querying
 * - REST API for integration with other tools
 */
@SpringBootApplication
@EnableConfigurationProperties
public class ImpactAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImpactAnalysisApplication.class, args);
    }
}
