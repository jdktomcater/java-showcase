package com.jdktomcat.showcase.rocketmq.transaction.consumer.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    /**
     * 格式化Date时间
     *
     * @param time       Date类型时间
     * @param timeFormat String类型格式
     * @return 格式化后的字符串
     */
    public static String parseDateToStr(Date time, String timeFormat) {
        DateFormat dateFormat = new SimpleDateFormat(timeFormat);
        return dateFormat.format(time);
    }
}
