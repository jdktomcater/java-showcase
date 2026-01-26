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
}