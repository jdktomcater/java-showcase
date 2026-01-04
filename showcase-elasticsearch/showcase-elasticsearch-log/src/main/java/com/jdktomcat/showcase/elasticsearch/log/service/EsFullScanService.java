package com.jdktomcat.showcase.elasticsearch.log.service;

import com.jdktomcat.showcase.elasticsearch.log.entry.ElkLog;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

@Slf4j
@Service
public class EsFullScanService {

    private final ElasticsearchOperations ops;

    /**
     * 构造注入
     *
     * @param ops ES相关配置
     */
    public EsFullScanService(ElasticsearchOperations ops) {
        this.ops = ops;
    }

    /**
     * 全量查询（可按 app_name 过滤）
     *
     * @param indexName 索引名称
     * @param appName   应用名称（为空则不过滤）
     * @param batchSize 批量大小
     * @param handler   处理器
     */
    public void scrollAll(String indexName, String appName, int batchSize, Consumer<ElkLog> handler) {
        IndexCoordinates index = IndexCoordinates.of(indexName);

        var qb = QueryBuilders.boolQuery();
        if (StringUtils.hasText(appName)) {
            // 注意：如果 app_name 在 ES mapping 中是 text 类型，请改成 "app_name.keyword"
            qb.filter(QueryBuilders.termQuery("app_name.keyword", appName));
        } else {
            qb.must(QueryBuilders.matchAllQuery());
        }

        Query query = new NativeSearchQueryBuilder()
                // scroll 最推荐
                .withQuery(qb).withSorts(SortBuilders.fieldSort("_doc").order(SortOrder.ASC))
                .withPageable(PageRequest.of(0, batchSize))
                // 强烈建议：只取需要字段（按需修改/也可以删掉这一行取全量 _source）
                .withSourceFilter(new FetchSourceFilter(new String[]{"log_time","app_name", "message"}, new String[]{}))
                .build();
        // Spring Boot 2.7.x / Spring Data Elasticsearch 4.4.x 推荐使用 searchForStream：内部使用 scroll，并在 close() 时自动清理 scroll 上下文

        try (SearchHitsIterator<ElkLog> it = ops.searchForStream(query, ElkLog.class, index)) {
            log.info("scan total:{}", it.getTotalHits());
            long count = 0L;
            while (it.hasNext()) {
                if (count != 0 && count % 10_000 == 0) {
                    log.info("scan count:{}", count);
                }
                count++;
                SearchHit<ElkLog> hit = it.next();
                handler.accept(hit.getContent());
            }
        }
    }
}