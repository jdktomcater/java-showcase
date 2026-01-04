package com.jdktomcat.showcase.elasticsearch.log.util;

public class RegexUtil {

    /**
     * 提取日志锚点
     *
     * @param log 日志
     * @return 日志锚点
     */
    public static String getLogAnchor(String log) {
        String[] logContents = log.split("]\\[");
        return logContents[3];
    }
}
