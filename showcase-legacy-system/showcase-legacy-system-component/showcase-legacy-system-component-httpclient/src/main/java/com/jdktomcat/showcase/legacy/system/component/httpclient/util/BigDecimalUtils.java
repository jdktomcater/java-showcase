package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import java.math.BigDecimal;

/**
 * 金额转换工具类
 *
 * @author cofess
 * @description
 * @date 2020/10/20 14:59
 */
public class BigDecimalUtils {

    /**
     * 将金额单位转换为分
     */
    private static final BigDecimal HUNDRED_CENTS = new BigDecimal(100);

    /**
     * 将金额元转换为分，不留小数点
     *
     * @param amount 金额：必须为字符串，避免丢失精度
     * @return 转换后的BigDecimal类
     */
    public static int convertToCent(String amount) {
        BigDecimal amountBigDecimal = newBigDecimal(amount);
        return amountBigDecimal.multiply(HUNDRED_CENTS).intValue();
    }

    /**
     * 将金额分转换为元，留小数点
     *
     * @param amount 金额：必须为字符串，避免丢失精度
     * @return 转换后的BigDecimal类
     */
    public static BigDecimal restoreAmount(String amount) {
        BigDecimal amountBigDecimal = newBigDecimal(amount);
        return amountBigDecimal.divide(HUNDRED_CENTS);
    }

    /**
     * 根据金额新建一个BigDecimal对象
     *
     * @param amount
     * @return BigDecimal对象
     */
    public static BigDecimal newBigDecimal(String amount) {
        return new BigDecimal(amount);
    }

    /**
     * 两个参数相乘
     *
     * @param amount       金额
     * @param multiplicand 被乘数
     * @return BigDecimal对象
     */
    public static BigDecimal bothMultiply(String amount, String multiplicand) {
        BigDecimal bigDecimal = newBigDecimal(amount);
        BigDecimal multiplicandBigDecimal = newBigDecimal(multiplicand);
        return bigDecimal.multiply(multiplicandBigDecimal);
    }

    /**
     * 两个参数相除
     *
     * @param amount   金额
     * @param dividend 被除数
     * @return BigDecimal对象
     */
    public static BigDecimal bothDivide(String amount, String dividend) {
        BigDecimal bigDecimal = newBigDecimal(amount);
        BigDecimal multiplicandBigDecimal = newBigDecimal(dividend);
        return bigDecimal.divide(multiplicandBigDecimal);
    }
}