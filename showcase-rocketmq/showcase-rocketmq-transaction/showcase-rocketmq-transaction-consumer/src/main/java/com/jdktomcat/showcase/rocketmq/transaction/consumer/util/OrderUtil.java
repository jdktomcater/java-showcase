package com.jdktomcat.showcase.rocketmq.transaction.consumer.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class OrderUtil {

    // 最近的时间戳
    private static long lastTimestamp=0;
    //机器id 2位
    private static final  long machineId = NetUtil.ipv4ToLong(NetUtil.getLocalIp());
    // 0，并发控制
    private static long sequence = 0L;
    // 序列号的最大值
    private static final int sequenceMax = 9999;


    /**
     * 生成订单号
     */
    public static synchronized String nextId(){
        Date now=new Date();
        String time= DateFormatUtils.format(now,"yyMMddHHmmssSSS");
        long timestamp = now.getTime();
        if (lastTimestamp == timestamp) {
            // 如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环);
            // 对新的timestamp，sequence从0开始
            sequence = sequence + 1 % sequenceMax;
            if (sequence == 0) {
                // 重新生成timestamp
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp= timestamp;
        return time + Long.toHexString(machineId) + leftPad(sequence);
    }

    /**
     * 补码
     *
     * @param i
     * @return
     */
    private static String leftPad(long i){
        String s = String.valueOf(i);
        StringBuilder sb=new StringBuilder();
        int c= 2 -s.length();
        c= Math.max(c, 0);
        for (int t=0;t<c;t++){
            sb.append("0");
        }
        return sb.append(s).toString();
    }

    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }



    /**
     * 生成订单Id
     * @param prefix 节点标识，如台湾节点：T
     * @param merchantCode 商户Code
     * @return 订单IDd
     */
    public static String getPltOrderID(String prefix,String merchantCode) {
        return prefix + merchantCode + nextId();
    }


    /**
     * 生成订单Id
     *
     * @param merchantCode 商户Code
     * @return 订单编码
     */
    public static String getPltOrderID(String prefix,String merchantCode,Integer type) {
        return prefix + merchantCode + type+ nextId();
    }

}
