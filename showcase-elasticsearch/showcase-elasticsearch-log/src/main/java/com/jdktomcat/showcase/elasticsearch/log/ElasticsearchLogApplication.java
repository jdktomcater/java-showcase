package com.jdktomcat.showcase.elasticsearch.log;

import com.jdktomcat.showcase.elasticsearch.log.entry.ElkLog;
import com.jdktomcat.showcase.elasticsearch.log.service.EsFullScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@SpringBootApplication
public class ElasticsearchLogApplication implements CommandLineRunner {

    @Autowired
    private EsFullScanService esFullScanService;

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchLogApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        TreeMap<Integer, ElkLog> treeMap = new TreeMap<>();
        int batchSize = 100;
        String indexName = "fat-txzf-2026.01.02";
        esFullScanService.scrollAll(indexName, batchSize, elkLog -> {
            if (treeMap.size() < 100) {
                treeMap.put(elkLog.getMessage().length(), elkLog);
                return;
            }
            Map.Entry<Integer, ElkLog> last = treeMap.pollFirstEntry();
            if (last.getKey() < elkLog.getMessage().length()) {
                treeMap.put(elkLog.getMessage().length(), elkLog);
            } else {
                treeMap.put(last.getKey(), last.getValue());
            }
        });
        treeMap.forEach((length, elklog) -> {
            log.info("length:{}, log:{}", length, elklog);
        });
    }
}
