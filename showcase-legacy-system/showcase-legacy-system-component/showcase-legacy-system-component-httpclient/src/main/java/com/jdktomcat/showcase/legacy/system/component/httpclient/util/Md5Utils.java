package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @version 1.0.0
 */
public class Md5Utils {
	
	public static String MD5(String key) {
        char[] hexDigits = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);

        for (byte value : b) {
            sb.append(HEX_DIGITS[(value & 240) >>> 4]);
            sb.append(HEX_DIGITS[value & 15]);
        }

        return sb.toString();
    }

    public static String to32Str(String sourceString) throws Exception {
        return toHexString(toByte(sourceString));
    }

    public static String to16Str(String sourceString) throws Exception {
        return to32Str(sourceString).substring(8, 24);
    }

    public static byte[] toByte(String sourceString) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(sourceString.getBytes(StandardCharsets.UTF_8));
        return digest.digest();
    }
}

