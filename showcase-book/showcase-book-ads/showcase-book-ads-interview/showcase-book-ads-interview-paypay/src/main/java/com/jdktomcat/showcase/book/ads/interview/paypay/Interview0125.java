package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.Arrays;

/**
 * interview 0125
 */
public class Interview0125 {

    /**
     * 1.only number and upper char
     * 2.length 10-20
     * 3.at least one number
     * 4.at least one char
     *
     * @param txId tx id
     * @return true:valid false:invalid
     */
    static boolean isValidTransactionId(String txId) {
        if (txId == null) {
            return false;
        }
        if (txId.length() < 10 || txId.length() > 20) {
            return false;
        }
        boolean containNumber = false;
        boolean containChar = false;
        for (int i = 0; i < txId.length(); i++) {
            char ch = txId.charAt(i);
            if (ch >= '0' && ch <= '9') {
                containNumber = true;
            } else if (ch >= 'A' && ch <= 'Z') {
                containChar = true;
            } else {
                return false;
            }
        }
        return containNumber && containChar;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    static int[] mergeSortedTransactions(int[] a, int[] b){
        return  Arrays.copyOf(a,a.length + b.length);
    }

}
