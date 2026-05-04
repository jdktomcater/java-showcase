package com.jdktomcat.showcase.data.neo4j.config;

import com.jdktomcat.showcase.data.neo4j.service.FriendCircleService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "showcase.neo4j", name = "initialize-sample-data", havingValue = "true")
public class SampleDataInitializer implements ApplicationRunner {

    private final FriendCircleService friendCircleService;

    public SampleDataInitializer(FriendCircleService friendCircleService) {
        this.friendCircleService = friendCircleService;
    }

    @Override
    public void run(ApplicationArguments args) {
        friendCircleService.resetSampleGraph();
    }
}
