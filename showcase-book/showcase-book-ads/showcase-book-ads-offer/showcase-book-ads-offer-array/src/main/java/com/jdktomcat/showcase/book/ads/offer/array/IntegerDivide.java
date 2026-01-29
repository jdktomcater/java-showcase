package com.jdktomcat.showcase.book.ads.offer.array;

/**
 * 题目：输入2个int型整数，它们进行除法计算并返回商，要求
 * 不得使用乘号'*'、除号'/'及求余符号'%'。当发生溢出时，返回最
 * 大的整数值。假设除数不为0。例如，输入15和2，输出15/2的结
 * 果，即7。
 */
public class IntegerDivide {

    /**
     * divide
     *
     * @param dividend target
     * @param divisor divide
     * @return result
     */
    public static int divide(int dividend, int divisor) {
        if (dividend == 0x80000000 && divisor == -1) {
            return Integer.MAX_VALUE;
        }
        return -1;
    }
}
