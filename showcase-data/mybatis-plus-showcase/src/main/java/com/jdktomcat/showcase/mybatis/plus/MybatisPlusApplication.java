package com.jdktomcat.showcase.mybatis.plus;

import com.jdktomcat.showcase.mybatis.plus.entry.DepositOrderLog;
import com.jdktomcat.showcase.mybatis.plus.entry.MatchupRule;
import com.jdktomcat.showcase.mybatis.plus.mapper.DepositOrderLogMapper;
import com.jdktomcat.showcase.mybatis.plus.mapper.MatchupRuleMapper;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.jdktomcat.showcase.mybatis.plus.mapper")
public class MybatisPlusApplication implements CommandLineRunner {

    @Resource
    private DepositOrderLogMapper depositOrderLogMapper;

    public static void main(String[] args) {

        SpringApplication.run(MybatisPlusApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        DepositOrderLog depositOrderLog = new DepositOrderLog();
        depositOrderLog.setPlatformOrderId("Test000000000001");
        depositOrderLog.setOrderId("Test000000000002");
        depositOrderLog.setActionTime(new Date());
        depositOrderLog.setResume("Test000000000003");
        depositOrderLog.setScore(0L);
        depositOrderLog.setMatchResult(0);
        depositOrderLog.setPoolType(1);
        int insert = depositOrderLogMapper.insert(depositOrderLog);
        System.out.println(insert);
    }
}