package com.jdktomcat.showcase.spring.ai.mcp.server.tool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 面向 MCP 暴露的 MySQL 查询工具集合。
 * <p>
 * 每个 {@code @Tool} 方法都会被 {@link org.springframework.ai.tool.method.MethodToolCallbackProvider}
 * 注册为一个 MCP Tool，名称即方法名，描述来自 {@code @Tool#description()}，
 * 入参描述来自 {@link ToolParam#description()}。这些信息会通过 MCP 协议的
 * {@code tools/list} 接口暴露给客户端，用于 LLM 工具选择或人工调用。
 * <p>
 * 安全约束：
 * <ul>
 *   <li>{@link #executeSelect(String, int)} 仅允许执行 SELECT/SHOW/DESC 语句；</li>
 *   <li>查询结果默认最多返回 100 行，避免一次性拉爆上下文窗口。</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MysqlQueryTools {

    private static final int DEFAULT_MAX_ROWS = 100;

    private final JdbcTemplate jdbcTemplate;

    @Tool(name = "listTables", description = "列出当前数据库中所有表的表名")
    public List<String> listTables() {
        log.info("[MCP-Tool] listTables invoked");
        // SHOW TABLES 在 MySQL 返回 1 列 (Tables_in_<db>)，但在 H2 MySQL 模式下返回
        // 2 列 (TABLE_NAME, TABLE_SCHEMA)。这里只取每行第一列，兼容两种实现。
        return jdbcTemplate.query("SHOW TABLES",
                (rs, rowNum) -> rs.getString(1));
    }

    @Tool(name = "describeTable",
            description = "查看指定表的结构（字段名、类型、是否可空、键、默认值等）")
    public List<Map<String, Object>> describeTable(
            @ToolParam(description = "要查看结构的表名，例如：t_user") String tableName) {
        log.info("[MCP-Tool] describeTable invoked, tableName={}", tableName);
        validateIdentifier(tableName);
        return jdbcTemplate.queryForList("DESC " + tableName);
    }

    @Tool(name = "executeSelect",
            description = "执行只读 SQL 查询（仅允许 SELECT / SHOW / DESC），返回结果集（最多 100 行）")
    public List<Map<String, Object>> executeSelect(
            @ToolParam(description = "完整 SQL 语句，必须以 SELECT/SHOW/DESC 开头")
            String sql,
            @ToolParam(required = false,
                    description = "最大返回行数，默认 100，最大 500")
            Integer maxRows) {
        log.info("[MCP-Tool] executeSelect invoked, sql={}", sql);
        String trimmed = requireReadOnly(sql);
        int limit = normalizeLimit(maxRows);
        return jdbcTemplate.query(trimmed, rs -> {
            List<Map<String, Object>> rows = new java.util.ArrayList<>();
            int colCount = rs.getMetaData().getColumnCount();
            while (rs.next() && rows.size() < limit) {
                Map<String, Object> row = new java.util.LinkedHashMap<>(colCount);
                for (int i = 1; i <= colCount; i++) {
                    row.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                }
                rows.add(row);
            }
            return rows;
        });
    }

    private String requireReadOnly(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        String trimmed = sql.trim();
        String upper = trimmed.toUpperCase();
        boolean readOnly = upper.startsWith("SELECT")
                || upper.startsWith("SHOW")
                || upper.startsWith("DESC")
                || upper.startsWith("DESCRIBE");
        if (!readOnly) {
            throw new IllegalArgumentException(
                    "出于安全考虑，仅允许 SELECT/SHOW/DESC 语句，收到：" + trimmed);
        }
        return trimmed;
    }

    private int normalizeLimit(Integer maxRows) {
        if (maxRows == null || maxRows <= 0) {
            return DEFAULT_MAX_ROWS;
        }
        return Math.min(maxRows, 500);
    }

    private void validateIdentifier(String identifier) {
        if (identifier == null || !identifier.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("非法的表名：" + identifier);
        }
    }
}
