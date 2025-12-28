package com.jdktomcat.showcase.mybatis.plus;

import com.jdktomcat.showcase.mybatis.plus.entry.ChannelAccountRecord;
import com.jdktomcat.showcase.mybatis.plus.mapper.ChannelAccountRecordMapper;
import com.jdktomcat.showcase.mybatis.plus.mapper.DepositOrderLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

/**
 * 启动类
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.jdktomcat.showcase.mybatis.plus.mapper")
public class MybatisPlusApplication implements CommandLineRunner {

    @Resource
    private DepositOrderLogMapper depositOrderLogMapper;

    @Resource
    private ChannelAccountRecordMapper channelAccountRecordMapper;

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
        ChannelAccountRecord channelAccountRecord = new ChannelAccountRecord();
        channelAccountRecord.setOrderIndex(2005199540986187777L);
        channelAccountRecord.setPayChannelID(10L);
        channelAccountRecord.setOrderID("12345678");
        channelAccountRecord.setCreateTime(new Date());
        int insert = channelAccountRecordMapper.insert(channelAccountRecord);
        log.info("insert channelAccountRecord:{}", insert);
    }
}