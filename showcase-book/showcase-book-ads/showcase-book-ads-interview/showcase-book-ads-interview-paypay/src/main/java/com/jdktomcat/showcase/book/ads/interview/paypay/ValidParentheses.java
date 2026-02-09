package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.*;

/**
 * Given a string s containing only the characters:
 * '(', ')', '{', '}', '[', ']'
 * determine whether the input string is valid.
 * A string is considered valid if:
 * Every opening bracket has a corresponding closing bracket of the same type.
 * Brackets are closed in the correct order.
 * Every closing bracket matches a previously opened bracket.
 */
public class ValidParentheses {

    /**
     * valid the str is valid parentheses
     *
     * @param str target
     * @return true: valid false:invalid
     */
    public static boolean validParentheses(String str) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
            }
            if (ch == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            }
            if (ch == ']') {
                if (stack.isEmpty() || stack.pop() != '[') {
                    return false;
                }
            }
            if (ch == '}') {
                if (stack.isEmpty() || stack.pop() != '{') {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    /**
     * valid the str is valid parentheses
     *
     * @param str target
     * @return true: valid false:invalid
     */
    public static boolean validParenthesesNew(String str) {
        Deque<Character> stack = new ArrayDeque<>();
        Map<Character, Character> map = Map.of(')', '(', '}', '{', ']', '[');
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (map.containsValue(ch)) {
                stack.push(ch);
            } else if (map.containsKey(ch)) {
                if (stack.isEmpty() || stack.pop() != map.get(ch)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
}
