package com.jdktomcat.showcase.limiter.redis.token;

import com.jdktomcat.showcase.limiter.redis.token.component.RedisComponent;
import com.jdktomcat.showcase.limiter.redis.token.dto.MerchantPayChannelDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class ShowcaseRedisApplication implements CommandLineRunner {

    @Autowired
    private RedisComponent redisComponent;


    public static void main(String[] args) {
        SpringApplication.run(ShowcaseRedisApplication.class, args);
    }

    @Override
    public void run(String... args) {
//        List<MerchantPayChannelDto> dataList = new ArrayList<>();
//        for (int i = 0; i < 300; i++) {
//            dataList.add(generateMerchantPayChannelDto());
//        }
//        Object target = JSONObject.toJSON(dataList);
//        String key = "d_list102234";
//        String field = "8_4_5_0_";
//        for (int i = 0; i < 100; i++) {
//            long start = System.currentTimeMillis();
//            redisComponent.hset(key, field + i, target, 24 * 3600);
//            log.info("cost:{}ms", System.currentTimeMillis() - start);
//        }
        for (int i = 0; i < 100; i++) {
            try {
                int minSplitAmt = (Integer) redisComponent.get("CONFIG_splitLastMinAmt");
                log.info("{} minSplitAmt is {}", i, minSplitAmt);
                TimeUnit.MILLISECONDS.sleep(30);
            } catch (Exception e) {
                log.error("redis Exception: {}", e.getMessage(), e);
            }
        }


    }

    public static MerchantPayChannelDto generateMerchantPayChannelDto() {
        MerchantPayChannelDto merchantPayChannelDto = new MerchantPayChannelDto();
        merchantPayChannelDto.setMerchantId(1000);
        merchantPayChannelDto.setPayType(20);
        merchantPayChannelDto.setPayChannelName("测试支付渠道");
        merchantPayChannelDto.setPayChannelID(1000);
        merchantPayChannelDto.setMaxAmountPerTrans(new BigDecimal(100));
        merchantPayChannelDto.setMinAmountPerTrans(new BigDecimal(100));
        merchantPayChannelDto.setPcFixedAmount("2300");
        merchantPayChannelDto.setWapFixedAmount("2300");
        merchantPayChannelDto.setAppFixedAmount("2300");
        merchantPayChannelDto.setIosFixedAmount("2300");
        merchantPayChannelDto.setAndroidFixedAmount("2300");
        merchantPayChannelDto.setProtocol("121232314");
        merchantPayChannelDto.setCurrency("USDT");
        merchantPayChannelDto.setIsFastPay(1);
        return merchantPayChannelDto;
    }
}

