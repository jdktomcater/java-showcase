package com.jdktomcat.showcase.ai.code.chunk.repository;

import com.jdktomcat.showcase.ai.code.chunk.domain.CodeGraphNode;
import com.jdktomcat.showcase.ai.code.chunk.domain.CodeGraphRelation;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
            )).list(record -> record.asMap()));
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
            )).list(record -> record.asMap()));
        }
    }
}
