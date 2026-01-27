package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.*;

public class Interview0125Test {

    @Test
    public void isValidTransactionId() {
        assertTrue(Interview0125.isValidTransactionId("A1B2C3D4E5"));
        assertFalse(Interview0125.isValidTransactionId("1234567890"));
        assertFalse(Interview0125.isValidTransactionId("ABCDEFGHIJ"));
        assertFalse(Interview0125.isValidTransactionId("a1B2C3D4E5"));
        assertFalse(Interview0125.isValidTransactionId(null));
        assertFalse(Interview0125.isValidTransactionId(""));
        assertFalse(Interview0125.isValidTransactionId("12"));
        assertFalse(Interview0125.isValidTransactionId("A1B2C3D4E5A1B2C3D4E5A1B2C3D4E5"));
        assertFalse(Interview0125.isValidTransactionId("123456789012345678901"));
        assertFalse(Interview0125.isValidTransactionId("A1B2C"));
        assertFalse(Interview0125.isValidTransactionId("A1B2C3D4E5@"));
    }

    @Test
    public void mergeSortedTransactions() {
        int[] a1 = new int[]{1, 3, 5, 7};
        int[] b1 = new int[]{2, 4, 6, 8};
        int[] result1 = Interview0125.mergeSortedTransactions(a1, b1);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, result1);

        int[] a2 = null;
        int[] b2 = new int[]{2, 4, 6, 8};
        int[] result2 = Interview0125.mergeSortedTransactions(a2, b2);
        assertArrayEquals(new int[]{2, 4, 6, 8}, result2);

        int[] a3 = new int[]{2, 4, 6, 8};
        int[] b3 = null;
        int[] result3 = Interview0125.mergeSortedTransactions(a3, b3);
        assertArrayEquals(new int[]{2, 4, 6, 8}, result3);

        int[] a4 = new int[]{1};
        int[] b4 = new int[]{2, 4, 6, 8};
        int[] result4 = Interview0125.mergeSortedTransactions(a4, b4);
        assertArrayEquals(new int[]{1, 2, 4, 6, 8}, result4);

        int[] a5 = new int[]{10};
        int[] b5 = new int[]{2, 4, 6, 8};
        int[] result5 = Interview0125.mergeSortedTransactions(a5, b5);
        assertArrayEquals(new int[]{2, 4, 6, 8, 10}, result5);
    }

    @Test
    public void maxSuccessfulPayments() {
        int[] amounts1 = new int[]{2, 1, 3, 2, 1, 1, 1};
        int limit1 = 5;
        int max1 = Interview0125.maxSuccessfulPayments(amounts1, limit1);
        assertEquals(4, max1);

        int[] amounts2 = new int[]{2, 1, 3, 2, 5, 1, 1};
        int limit2 = 5;
        int max2 = Interview0125.maxSuccessfulPayments(amounts2, limit2);
        assertEquals(2, max2);

        int[] amounts3 = new int[]{5, 5, 5, 5, 5, 5, 5};
        int limit3 = 5;
        int max3 = Interview0125.maxSuccessfulPayments(amounts3, limit3);
        assertEquals(1, max3);

        int[] amounts4 = new int[]{6, 6, 6, 6, 6, 6, 6};
        int limit4 = 5;
        int max4 = Interview0125.maxSuccessfulPayments(amounts4, limit4);
        assertEquals(0, max4);

        int[] amounts5 = new int[]{6};
        int limit5 = 5;
        int max5 = Interview0125.maxSuccessfulPayments(amounts5, limit5);
        assertEquals(0, max5);

        int[] amounts6 = new int[]{5};
        int limit6 = 5;
        int max6 = Interview0125.maxSuccessfulPayments(amounts6, limit6);
        assertEquals(1, max6);

        int[] amounts7 = new int[]{4};
        int limit7 = 5;
        int max7 = Interview0125.maxSuccessfulPayments(amounts7, limit7);
        assertEquals(1, max7);

        int[] amounts8 = new int[]{};
        int limit8 = 5;
        int max8 = Interview0125.maxSuccessfulPayments(amounts8, limit8);
        assertEquals(0, max8);

        int[] amounts9 = null;
        int limit9 = 5;
        int max9 = Interview0125.maxSuccessfulPayments(amounts9, limit9);
        assertEquals(0, max9);

        int[] amounts10 = new int[]{2, 1, 3, 2, 1, 1, 1};
        int limit10 = 15;
        int max10 = Interview0125.maxSuccessfulPayments(amounts10, limit10);
        assertEquals(7, max10);
    }

    @Test
    public void testIsValidTransactionId() {
        int n1 = 3;
        int[][] edges1 = new int[][]{{0, 1}, {1, 2}, {2, 0}};
        boolean validate1 = Interview0125.validatePaymentGraph(n1, edges1);
        assertFalse(validate1);

        int n2 = 3;
        int[][] edges2 = new int[][]{{0, 1}, {1, 2}};
        boolean validate2 = Interview0125.validatePaymentGraph(n2, edges2);
        assertTrue(validate2);
    }
}