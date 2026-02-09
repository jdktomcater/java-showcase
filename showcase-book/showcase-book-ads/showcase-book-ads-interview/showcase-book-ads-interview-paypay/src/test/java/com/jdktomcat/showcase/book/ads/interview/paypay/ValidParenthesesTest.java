package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidParenthesesTest {

    @Test
    public void validParentheses() {
        String str1 = "()[]{}";
        assertTrue(ValidParentheses.validParentheses(str1));

        String str2 = "(";
        assertFalse(ValidParentheses.validParentheses(str2));

        String str3 = "";
        assertTrue(ValidParentheses.validParentheses(str3));

        String str4 = "((((()))";
        assertFalse(ValidParentheses.validParentheses(str4));

        String str5 = "({)";
        assertFalse(ValidParentheses.validParentheses(str5));

        String str6 = "({[]})";
        assertTrue(ValidParentheses.validParentheses(str6));
    }

    @Test
    public void validParenthesesNew() {
        String str1 = "()[]{}";
        assertTrue(ValidParentheses.validParenthesesNew(str1));

        String str2 = "(";
        assertFalse(ValidParentheses.validParenthesesNew(str2));

        String str3 = "";
        assertTrue(ValidParentheses.validParenthesesNew(str3));

        String str4 = "((((()))";
        assertFalse(ValidParentheses.validParenthesesNew(str4));

        String str5 = "({)";
        assertFalse(ValidParentheses.validParenthesesNew(str5));

        String str6 = "({[]})";
        assertTrue(ValidParentheses.validParenthesesNew(str6));
    }
}