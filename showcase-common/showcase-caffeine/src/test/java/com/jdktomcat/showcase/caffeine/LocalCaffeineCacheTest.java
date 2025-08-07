package com.jdktomcat.showcase.caffeine;

import com.alibaba.fastjson2.JSONObject;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class LocalCaffeineCacheTest extends TestCase {

    private final Map<String, String> data = new HashMap<>();

    public void setUp() throws Exception {
        super.setUp();

        data.put("""
                v=data.orderStatus; if(v==0){v='处理中';}
                elsif(v==1){v='成功';}
                elsif(v==2){v='失败';}
                else{v='订单状态不明确，请和商户确认';}
                v
                """, """
                {
                	"code": 200,
                	"msg": "SUCCESS",
                	"data": {
                		"orderNo": "3693692TCNY250618195301SVAVT",
                		"orderStatus": 0,
                		"orderAmount": 300.0,
                		"paidAmount": 0.0,
                		"playerName": "moren1",
                		"merchantNo": 369369,
                		"merchantOrderId": "HOaz888425061819490979648c600",
                		"payTypeId": 520,
                		"payTypeName": "银行卡转卡",
                		"createTime": "2025-06-18 19:53:01",
                		"completeTime": "2025-06-18 20:11:00",
                		"merchantFee": 0.0,
                		"merchantRate": 0.0,
                		"merchatFixedFee": 0.0,
                		"bankCode": "ALIPAY",
                		"bankNum": "13111223344",
                		"bankAddress": "北京",
                		"bankOwner": "胡歌",
                		"virtualRate": 0.0,
                		"virtualFiexdFee": 0.0,
                		"virtualCurrency": "",
                		"virtualProtocol": 0,
                		"tradeIP": "175.100.203.34",
                		"tradeDeviceType": 1,
                		"tradeDeviceNo": "",
                		"remark": "",
                		"virtualPayAmount": 0.0,
                		"virtualPaidAmount": 0.0,
                		"currencyCode": "CNY",
                		"operator": ""
                	},
                	"v": "处理中"
                }
                """);
        data.put("data.orderStatus", """
                {
                	"code": 200,
                	"msg": "SUCCESS",
                	"data": {
                		"orderNo": "3693692TCNY250618195301SVAVT",
                		"orderStatus": 0,
                		"orderAmount": 300.0,
                		"paidAmount": 0.0,
                		"playerName": "moren1",
                		"merchantNo": 369369,
                		"merchantOrderId": "HOaz888425061819490979648c600",
                		"payTypeId": 520,
                		"payTypeName": "银行卡转卡",
                		"createTime": "2025-06-18 19:53:01",
                		"completeTime": "2025-06-18 20:11:00",
                		"merchantFee": 0.0,
                		"merchantRate": 0.0,
                		"merchatFixedFee": 0.0,
                		"bankCode": "ALIPAY",
                		"bankNum": "13111223344",
                		"bankAddress": "北京",
                		"bankOwner": "胡歌",
                		"virtualRate": 0.0,
                		"virtualFiexdFee": 0.0,
                		"virtualCurrency": "",
                		"virtualProtocol": 0,
                		"tradeIP": "175.100.203.34",
                		"tradeDeviceType": 1,
                		"tradeDeviceNo": "",
                		"remark": "",
                		"virtualPayAmount": 0.0,
                		"virtualPaidAmount": 0.0,
                		"currencyCode": "CNY",
                		"operator": ""
                	}
                }
                """);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExecuteWithCache() {
        while (true) {
            data.forEach((key, value) -> {
                System.out.println(ExpressionUtil.executeWithCache(key, JSONObject.parseObject(value, Map.class)));
            });
        }
    }

    public void testExecuteWithoutCache() {
        while (true) {
            data.forEach((key, value) -> {
                System.out.println(ExpressionUtil.executeWithoutCache(key, JSONObject.parseObject(value, Map.class)));
            });
        }
    }
}