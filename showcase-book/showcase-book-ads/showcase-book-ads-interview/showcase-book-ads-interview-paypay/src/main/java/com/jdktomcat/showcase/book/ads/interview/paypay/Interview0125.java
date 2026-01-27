package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.*;

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
     * merge sorted transactions
     *
     * @param a first target transaction
     * @param b second target transaction
     * @return merged
     */
    static int[] mergeSortedTransactions(int[] a, int[] b) {
        if (a == null && b == null) {
            return new int[0];
        }
        if (a == null || a.length == 0) {
            return b;
        }
        if (b == null || b.length == 0) {
            return a;
        }
        int[] merged = new int[a.length + b.length];
        int indexA = 0, indexB = 0;
        for (int index = 0; index < merged.length; index++) {
            if (indexA < a.length && indexB < b.length) {
                if (a[indexA] <= b[indexB]) {
                    merged[index] = a[indexA++];
                } else {
                    merged[index] = b[indexB++];
                }
            } else if (indexA < a.length) {
                merged[index] = a[indexA++];
            } else if (indexB < b.length) {
                merged[index] = b[indexB++];
            }
        }
        return merged;
    }

    /**
     * max successful payments
     *
     * @param amounts amounts
     * @param limit   limit
     * @return max
     */
    static int maxSuccessfulPayments(int[] amounts, int limit) {
        if (amounts == null || amounts.length == 0) {
            return 0;
        }
        if (amounts.length == 1) {
            return amounts[0] > limit ? 0 : 1;
        }

        int start = 0, end = 1, amount = amounts[0], max = (amounts[0] > limit ? 0 : 1);
        while (start <= end && end < amounts.length) {
            if (amount + amounts[end] <= limit) {
                max = Math.max(max, end - start + 1);
                amount += amounts[end];
                end++;
            } else {
                amount -= amounts[start];
                start++;
            }
        }
        return max;
    }

    /**
     * validate payment graph
     *
     * @param n     edge number
     * @param edges edges
     * @return true:validate false invalidate
     */
    static boolean validatePaymentGraph(int n, int[][] edges) {
        boolean[][] rely = new boolean[n][n];
        for (int[] edge : edges) {
            rely[edge[0]][edge[1]] = true;
        }

        for (int i = 0; i < n; i++) {
            Set<Integer> relyNode = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if (rely[i][j]) {
                    relyNode.add(i);
                    boolean sub = isValidPaymentGraph(j, rely, relyNode);
                    if (sub) {
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * is valid payment graph
     *
     * @param t    target
     * @param rely rely map
     * @return true:rely false:not rely
     */
    private static boolean isValidPaymentGraph(int t, boolean[][] rely, Set<Integer> relyNode) {
        relyNode.add(t);
        for (int i = 0; i < rely[t].length; i++) {
            if (rely[t][i]) {
                boolean contained = relyNode.contains(i);
                if (contained) {
                    return false;
                }
                relyNode.add(i);
                boolean sub = isValidPaymentGraph(i, rely, relyNode);
                if (sub) {
                    relyNode.remove(i);
                }
                return sub;
            }
        }
        relyNode.remove(t);
        return true;
    }
}
