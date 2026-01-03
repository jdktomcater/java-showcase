package com.jdktomcat.showcase.elasticsearch.log;

import com.jdktomcat.showcase.elasticsearch.log.dao.ElkLogRepository;
import com.jdktomcat.showcase.elasticsearch.log.entry.ElkLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        Iterable<ElkLog> iterable = elkLogRepository.findByNameContaining("name");
        iterable.forEach(elkLog -> {
            log.info(String.valueOf(elkLog));
        });
    }
}
