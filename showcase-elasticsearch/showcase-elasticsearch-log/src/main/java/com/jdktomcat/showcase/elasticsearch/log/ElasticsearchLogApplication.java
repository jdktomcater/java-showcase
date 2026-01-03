package com.jdktomcat.showcase.elasticsearch.log;

import com.jdktomcat.showcase.elasticsearch.log.dao.ElkLogRepository;
import com.jdktomcat.showcase.elasticsearch.log.entry.ElkLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@SpringBootApplication
public class ElasticsearchLogApplication implements CommandLineRunner {

    @Autowired
    private ElkLogRepository elkLogRepository;

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
        long count = elkLogRepository.count();
        log.info("总数：{}", count);
        TreeMap<Integer, ElkLog> treeMap = new TreeMap<>();
        Page<ElkLog> page;
        int pageNum = 0;
        while (count >= 0) {
            log.info("count:{} page:{}", count, pageNum);
            page = elkLogRepository.findAll(PageRequest.of(pageNum++, 100));
            count -= page.getSize();
            page.forEach(elkLog -> {
                if (treeMap.size() < 100) {
                    treeMap.put(elkLog.getMessage().length(), elkLog);
                }
                Map.Entry<Integer, ElkLog> last = treeMap.pollLastEntry();
                if (last.getKey() < elkLog.getMessage().length()) {
                    treeMap.put(elkLog.getMessage().length(), elkLog);
                } else {
                    treeMap.put(last.getKey(), last.getValue());
                }
            });
        }
        treeMap.forEach((length, elklog) -> {
            log.info("length:{}, log:{}", length, elklog);
        });
    }
}
