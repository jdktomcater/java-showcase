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
import org.springframework.data.domain.Pageable;

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
        Pageable pageable = PageRequest.of(0, 100);
        Page<ElkLog> page = elkLogRepository.findAll(pageable);
        page.get().forEach(elkLog -> {
            log.info(String.valueOf(elkLog));
        });
    }
}
