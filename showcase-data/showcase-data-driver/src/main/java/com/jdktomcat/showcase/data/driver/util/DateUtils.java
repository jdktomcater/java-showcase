package com.jdktomcat.showcase.data.driver.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author js-oswald
 */
@Slf4j
public class DateUtils {

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当天从0开始的时间
     *
     * @return 时间
     */
    public static Date getNowDateFromZero() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getNowDate());
        calendar.add(Calendar.DAY_OF_MONTH, -15);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
