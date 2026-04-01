package com.jdktomcat.showcase.ai.code.chunk.repository;

import com.jdktomcat.showcase.ai.code.chunk.domain.CodeGraphNode;
import com.jdktomcat.showcase.ai.code.chunk.domain.CodeGraphRelation;
import com.jdktomcat.showcase.ai.code.chunk.dto.GraphViewEdge;
import com.jdktomcat.showcase.ai.code.chunk.dto.GraphViewNode;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.MapAccessor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class Neo4jGraphRepository {

    private final Driver driver;

    public Neo4jGraphRepository(Driver driver) {
        this.driver = driver;
    }

    public void saveAll(List<CodeGraphNode> nodes, List<CodeGraphRelation> relations) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                for (CodeGraphNode node : nodes) {
                    tx.run("""
                            MERGE (n:CodeNode {id: $id})
                            SET n.type = $type,
                                n.name = $name,
                                n.fqn = $fqn,
                                n.filePath = $filePath,
                                n.module = $module,
                                n.startLine = $startLine,
                                n.endLine = $endLine
                            """, Values.parameters(
                            "id", node.id(),
                            "type", node.type().name(),
                            "name", node.name(),
                            "fqn", node.fqn(),
                            "filePath", node.filePath(),
                            "module", node.module(),
                            "startLine", node.startLine(),
                            "endLine", node.endLine()
                    ));
                }

                for (CodeGraphRelation relation : relations) {
                    tx.run("""
                            MATCH (from:CodeNode {id: $fromId})
                            MATCH (to:CodeNode {id: $toId})
                            MERGE (from)-[r:RELATES {type: $type}]->(to)
                            """, Values.parameters(
                            "fromId", relation.fromId(),
                            "toId", relation.toId(),
                            "type", relation.type().name()
                    ));
                }

                return null;
            });
        }
    }

    public List<Map<String, Object>> findDependencies(String nodeId, int depth) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> tx.run("""
                    MATCH (start:CodeNode {id: $nodeId})
                    MATCH p = (start)-[:RELATES*1..10]->(target:CodeNode)
                    WHERE length(p) <= $depth
                    RETURN DISTINCT target.id AS id,
                                    target.type AS type,
                                    target.name AS name,
                                    target.fqn AS fqn,
                                    target.filePath AS filePath,
                                    target.module AS module,
                                    length(p) AS hops,
                                    [r IN relationships(p) | r.type] AS relationTypes
                    ORDER BY hops, fqn
                    """, Values.parameters(
                    "nodeId", nodeId,
                    "depth", depth
            )).list(MapAccessor::asMap));
        }
    }

    public List<Map<String, Object>> findImpact(String methodId, int depth) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> tx.run("""
                    MATCH (target:CodeNode {id: $methodId})
                    MATCH p = (caller:CodeNode)-[:RELATES*1..10]->(target)
                    WHERE caller.type = 'METHOD'
                      AND length(p) <= $depth
                      AND ALL(r IN relationships(p) WHERE r.type = 'CALLS')
                    OPTIONAL MATCH (owner:CodeNode)-[:RELATES {type: 'DECLARES'}]->(caller)
                    OPTIONAL MATCH (owner)-[:RELATES {type: 'ANNOTATED_BY'}]->(ann:CodeNode)
                    RETURN DISTINCT caller.id AS id,
                                    caller.name AS name,
                                    caller.fqn AS fqn,
                                    caller.filePath AS filePath,
                                    owner.fqn AS ownerType,
                                    collect(DISTINCT ann.name) AS ownerAnnotations,
                                    length(p) AS hops
                    ORDER BY hops, fqn
                    """, Values.parameters(
                    "methodId", methodId,
                    "depth", depth
            )).list(MapAccessor::asMap));
        }
    }

    public GraphData findDependencyGraph(String nodeId, int depth) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                Record record = tx.run("""
                    MATCH (start:CodeNode {id: $nodeId})
                    OPTIONAL MATCH p = (start)-[:RELATES*1..10]->(target:CodeNode)
                    WHERE length(p) <= $depth
                    WITH start, collect(DISTINCT p) AS paths
                    WITH start,
                         [node IN collect(DISTINCT start) + reduce(allNodes = [], path IN paths |
                             allNodes + CASE WHEN path IS NULL THEN [] ELSE nodes(path) END)
                          WHERE node IS NOT NULL] AS rawNodes,
                         reduce(allRels = [], path IN paths |
                             allRels + CASE WHEN path IS NULL THEN [] ELSE relationships(path) END) AS rawRels
                    UNWIND rawNodes AS node
                    WITH start, collect(DISTINCT node) AS graphNodes, rawRels
                    UNWIND CASE WHEN size(rawRels) = 0 THEN [NULL] ELSE rawRels END AS rel
                    WITH start, graphNodes, collect(DISTINCT rel) AS graphRels
                    RETURN [node IN graphNodes | {
                                id: node.id,
                                name: node.name,
                                type: node.type,
                                fqn: node.fqn,
                                filePath: node.filePath,
                                module: node.module,
                                startLine: node.startLine,
                                endLine: node.endLine
                            }] AS nodes,
                           [rel IN graphRels WHERE rel IS NOT NULL | {
                                id: elementId(rel),
                                fromId: startNode(rel).id,
                                toId: endNode(rel).id,
                                type: rel.type
                            }] AS relationships
                    """, Values.parameters(
                    "nodeId", nodeId,
                    "depth", depth
                )).single();
                return mapGraphData(
                        record.get("nodes").asList(MapAccessor::asMap),
                        record.get("relationships").asList(MapAccessor::asMap),
                        nodeId
                );
            });
        }
    }

    public GraphData findImpactGraph(String methodId, int depth) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                Record record = tx.run("""
                    MATCH (target:CodeNode {id: $methodId})
                    OPTIONAL MATCH p = (caller:CodeNode)-[:RELATES*1..10]->(target)
                    WHERE caller.type = 'METHOD'
                      AND length(p) <= $depth
                      AND ALL(r IN relationships(p) WHERE r.type = 'CALLS')
                    WITH target, collect(DISTINCT p) AS paths
                    WITH target,
                         [node IN collect(DISTINCT target) + reduce(allNodes = [], path IN paths |
                             allNodes + CASE WHEN path IS NULL THEN [] ELSE nodes(path) END)
                          WHERE node IS NOT NULL] AS rawNodes,
                         reduce(allRels = [], path IN paths |
                             allRels + CASE WHEN path IS NULL THEN [] ELSE relationships(path) END) AS rawRels
                    UNWIND rawNodes AS node
                    WITH target, collect(DISTINCT node) AS graphNodes, rawRels
                    UNWIND CASE WHEN size(rawRels) = 0 THEN [NULL] ELSE rawRels END AS rel
                    WITH target, graphNodes, collect(DISTINCT rel) AS graphRels
                    RETURN [node IN graphNodes | {
                                id: node.id,
                                name: node.name,
                                type: node.type,
                                fqn: node.fqn,
                                filePath: node.filePath,
                                module: node.module,
                                startLine: node.startLine,
                                endLine: node.endLine
                            }] AS nodes,
                           [rel IN graphRels WHERE rel IS NOT NULL | {
                                id: elementId(rel),
                                fromId: startNode(rel).id,
                                toId: endNode(rel).id,
                                type: rel.type
                            }] AS relationships
                    """, Values.parameters(
                    "methodId", methodId,
                    "depth", depth
                )).single();
                return mapGraphData(
                        record.get("nodes").asList(MapAccessor::asMap),
                        record.get("relationships").asList(MapAccessor::asMap),
                        methodId
                );
            });
        }
    }

    public GraphData findFullTypeDependencyGraph() {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                Record record = tx.run("""
                    OPTIONAL MATCH (source:CodeNode {type: 'TYPE'})
                    OPTIONAL MATCH (source)-[rel:RELATES]->(target:CodeNode {type: 'TYPE'})
                    WHERE rel IS NULL OR rel.type IN ['DEPENDS_ON', 'EXTENDS', 'IMPLEMENTS', 'INJECTS']
                    WITH [node IN collect(DISTINCT source) + collect(DISTINCT target)
                          WHERE node IS NOT NULL] AS rawNodes,
                         [relation IN collect(DISTINCT rel)
                          WHERE relation IS NOT NULL] AS graphRels
                    UNWIND CASE WHEN size(rawNodes) = 0 THEN [NULL] ELSE rawNodes END AS node
                    WITH collect(DISTINCT node) AS graphNodes, graphRels
                    RETURN [node IN graphNodes WHERE node IS NOT NULL | {
                                id: node.id,
                                name: node.name,
                                type: node.type,
                                fqn: node.fqn,
                                filePath: node.filePath,
                                module: node.module,
                                startLine: node.startLine,
                                endLine: node.endLine
                            }] AS nodes,
                           [rel IN graphRels | {
                                id: elementId(rel),
                                fromId: startNode(rel).id,
                                toId: endNode(rel).id,
                                type: rel.type
                            }] AS relationships
                    """).single();
                return mapGraphData(
                        record.get("nodes").asList(MapAccessor::asMap),
                        record.get("relationships").asList(MapAccessor::asMap),
                        null
                );
            });
        }
    }

    private GraphData mapGraphData(List<Map<String, Object>> nodeMaps, List<Map<String, Object>> relationMaps, String focusId) {
        List<GraphViewNode> nodes = new ArrayList<>();
        for (Map<String, Object> node : nodeMaps) {
            String id = asString(node.get("id"));
            nodes.add(new GraphViewNode(
                    id,
                    firstNonBlank(asString(node.get("name")), id),
                    asString(node.get("type")),
                    asString(node.get("fqn")),
                    asString(node.get("filePath")),
                    asString(node.get("module")),
                    asInteger(node.get("startLine")),
                    asInteger(node.get("endLine")),
                    Objects.equals(focusId, id)
            ));
        }

        List<GraphViewEdge> edges = new ArrayList<>();
        for (Map<String, Object> relation : relationMaps) {
            edges.add(new GraphViewEdge(
                    asString(relation.get("id")),
                    asString(relation.get("fromId")),
                    asString(relation.get("toId")),
                    asString(relation.get("type"))
            ));
        }

        return new GraphData(nodes, edges);
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Integer asInteger(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }

    private String firstNonBlank(String first, String fallback) {
        return first == null || first.isBlank() ? fallback : first;
    }

    public record GraphData(
            List<GraphViewNode> nodes,
            List<GraphViewEdge> edges
    ) {
    }
}
