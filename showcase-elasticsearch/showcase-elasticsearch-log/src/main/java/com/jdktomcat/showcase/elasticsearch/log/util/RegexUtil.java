package com.jdktomcat.showcase.elasticsearch.log.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegexUtil {

    /**
     * 提取日志锚点
     *
     * @param content 日志
     * @return 日志锚点
     */
    public static String getLogAnchor(String content) {
        String[] logContents = content.split("]\\[");
        if(logContents.length<4){
            log.warn(content);
            return null;
        }
        return logContents[3];
    }
}
