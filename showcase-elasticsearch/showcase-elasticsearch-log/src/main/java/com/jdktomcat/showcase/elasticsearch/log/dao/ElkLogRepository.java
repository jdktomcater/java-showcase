package com.jdktomcat.showcase.elasticsearch.log.dao;

import com.jdktomcat.showcase.elasticsearch.log.entry.ElkLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElkLogRepository extends ElasticsearchRepository<ElkLog, String> {
}
