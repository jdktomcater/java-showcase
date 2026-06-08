package com.jdktomcater.showcase.job.provider.commands;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShowcaseCommands {

    @XxlJob(value = "showcaseJob")
    public void showcaseJob() {
        XxlJobHelper.getJobParam();
    }
}
