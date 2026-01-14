package com.jdktomcat.showcase.elasticsearch.log.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RegexUtil {

    private static final Pattern FILE_LINE = Pattern.compile("\\|\\s*([^\\s|]+\\.java)\\s*:\\s*(\\d+)");

    /**
     * 提取TX日志锚点
     *
     * @param content 日志
     * @return 日志锚点
     */
    public static String getTxLogAnchor(String content) {
        String[] logContents = content.split("]\\[");
        if (logContents.length < 4) {
            log.warn(content);
            return null;
        }
        return logContents[3];
    }

    /**
     * 提取WALLET日志锚点
     *
     * @param content 日志
     * @return 日志锚点
     */
    public static String getWalletLogAnchor(String content) {
        Matcher matcher = FILE_LINE.matcher(content);
        if (matcher.find()) {
            return matcher.group(1) + ":" + matcher.group(2);
        }
        return content;
    }
}
