package com.jdktomcat.showcase.jasypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AesUtil {
    private static final String IV = "typay!@#qwerasdf";
    private static final String password = "typay#generalfront";
    private static final String IV_TOKEN = "typay!@#xt.gcdry";
    private static final String PASSWORD_TOKEN = "typay#generalfront";
    private static final String wallpassword = "6b25f28603634c9fb86401a1f71dc056";



    /**
     * AES加密
     *
     * @param content 明文
     * @return 密文
     */
    public static String encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = password.getBytes();
            SecretKeySpec skySpec = new SecretKeySpec(raw, "AES");
            //使用CBC模式，需要一个向量iv，可增加加密算法的强度
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skySpec, iv);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return  byteToHex(encrypted);
        }catch (Exception e){
            e.printStackTrace();
            return  e.toString();
        }
    }

    /**
     * AES解密
     *
     * @param input
     * @param key
     * @return
     */
    public static String decrypt(String input) {
        try {
            byte[] raw = password.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] content = hexToByte(input);
            byte[] original = cipher.doFinal(content);
            return new String(original,"UTF8");
        } catch (Exception ex) {
            return null;
        }
    }

    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }

    /**
     * hex转byte数组
     * @param hex
     * @return
     */
    public static byte[] hexToByte(String hex) {
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = (byte) intVal;
        }
        return ret;
    }


    /**
     * AES加密
     *
     * @param str
     *            明文
     * @param key
     *            秘钥
     * @return
     */
    public static String encryptToken(String content) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = PASSWORD_TOKEN.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(IV_TOKEN.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return byteToHex(encrypted);
        } catch (Exception e) {
            return e.toString();
        }

    }

    /**
     * AES解密
     *
     * @param input
     * @param key
     * @return
     */
    public static String decryptToken(String input) {
        try {
            byte[] raw = PASSWORD_TOKEN.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV_TOKEN.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] content = hexToByte(input);
            byte[] original = cipher.doFinal(content);
            String originalString = new String(original, StandardCharsets.UTF_8);
            return originalString.trim();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * AES加密
     *
     * @param content 明文
     * @param aesKey aesKey
     * @return
     */
    public static String encryptWall(String content) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = wallpassword.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return byteToHex(encrypted);
        } catch (Exception e) {
            return e.toString();
        }

    }

    /**
     * AES解密
     *
     * @param input
     * @param aesKey
     * @return
     */
    public static String decryptWall(String input) {
        try {
            byte[] raw = wallpassword.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] content = hexToByte(input);
            byte[] original = cipher.doFinal(content);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(AesUtil.decrypt("dDuWrqY8mq4QSJyocFYmsIs6Zv0DszQTKyV/BJKqHcw="));
    }
}