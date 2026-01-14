package com.jdktomcat.showcase.elasticsearch.log;

import com.jdktomcat.showcase.elasticsearch.log.service.EsFullScanService;
import com.jdktomcat.showcase.elasticsearch.log.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@SpringBootApplication
public class ElasticsearchLogApplication implements CommandLineRunner {

    @Autowired
    private EsFullScanService esFullScanService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
        int batchSize = 100;
        String[] indexNames = {
                "fat-wallet-2026.01.11"
        };
        String[] appNames = {
//                "TyPayJV2Dispatch"
//                "TyPayJV2JobProvider",
//                "TyPayJV2ThirdchannelDispatch",
//                "TyPayJV2Api",
//                "TyPayJV2FinancialFront",
//                "typayjv2mqorderfinishconsumer",
//                "TyPayJV2ThirdchannelUser"
//                "plugin-kms-service",
//                "TyPayJV2ThirdchannelThird",
//                "typayjv2mqalarmconsumer",
//                "TyPayJV2GeneralFront",
//                "TyPayJV2MQConsumer",
//                "TyPayJV2BankServer",
//                "TyPayJV2BusinessFront",
//                "TyPayJV2MerchantServer",
//                "automachinejv2logadmin",
//                "TyPayJV2MatchupPayService"
//                "MatchupPayHub",
//                "MatchupAdmin",
//                "MatchupPayApi"
//                "wallet-b2b-manager",
//                "wallet-pay-server",
//                "wallet-job-server",
//                "wallet-hub-manager",
//                "wallet-thirdchannel-third",
//                "wallet-api-server",
//                "wallet-admin-gateway",
//                "wallet-thirdchannel-dispatch",
//                "wallet-notice-server",
//                "wallet-api-gateway"
        };
        for (String appName : appNames) {
            String capacityKey = "es:scan:elklog:capacity:" + appName;
            for (String indexName : indexNames) {
                esFullScanService.scrollAll(indexName, appName, batchSize, elkLog -> {
                    String message = elkLog.getMessage();
                    String logAnchor = RegexUtil.getWalletLogAnchor(message);
                    // 统计容量
                    stringRedisTemplate.opsForZSet().incrementScore(capacityKey, logAnchor, message.length());
                });
            }
        }
    }
}
