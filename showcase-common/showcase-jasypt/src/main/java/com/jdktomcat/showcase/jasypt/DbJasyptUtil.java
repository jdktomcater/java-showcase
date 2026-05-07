package com.jdktomcat.showcase.jasypt;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class DbJasyptUtil {

    /**
     * Jasypt生成加密结果
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待加密值
     * @return 加密后字符串
     */
    public static String encryptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptOr = new PooledPBEStringEncryptor();
        encryptOr.setConfig(cryptOr(password));
        return encryptOr.encrypt(value);
    }

    /**
     * 解密
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待解密密文
     * @return 明文
     */
    public static String dencryptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptOr = new PooledPBEStringEncryptor();
        encryptOr.setConfig(cryptOr(password));
        return encryptOr.decrypt(value);
    }

    /**
     * 加密相关配置
     *
     * @param password salt
     * @return 配置
     */
    public static SimpleStringPBEConfig cryptOr(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
//        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
//        config.setProviderName(null);
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
//        config.setStringOutputType("base64");
        return config;
    }

    public static void main(String[] args) {
        String password= "WmXu4M1!9*oxAdWAM";
        String value = "vKyAJ7Ndl4McwojLp38IcAwIdIDq904o+74pS8dZI9Uiw6MGD5SmeHMfkxFXoszbva6HxFt31cQ=";
        System.out.println(dencryptPwd(password, value));

//        String encrypted = "vKyAJ7Ndl4McwojLp38IcAwIdIDq904o+74pS8dZI9Uiw6MGD5SmeHMfkxFXoszbva6HxFt31cQ=";
//        String password = "WmXu4M1!9*oxAdWAM";
//
//        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//        encryptor.setPassword(password);
//        encryptor.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
//        String plain = encryptor.decrypt(encrypted);
//        System.out.println(plain);
    }
}
