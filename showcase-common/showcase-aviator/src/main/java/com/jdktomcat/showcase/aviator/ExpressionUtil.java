package com.jdktomcat.showcase.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ExpressionUtil {

    /**
     * 执行规则引擎,带缓存
     *
     * @param exprName 表达式名称
     * @param dataMap  数据
     * @return 结果
     */
    public static String executeWithCache(String exprName, Map<String, Object> dataMap) {
        Object objectValue = AviatorEvaluator.execute(exprName, dataMap, true);
        return objectValue == null ? "" : String.valueOf(objectValue);
    }

    /**
     * 执行规则引擎,不带缓存
     *
     * @param exprName 表达式名称
     * @param dataMap  数据
     * @return 结果
     */
    public static String executeWithoutCache(String exprName, Map<String, Object> dataMap) {
        Object objectValue = AviatorEvaluator.execute(exprName, dataMap);
        return objectValue == null ? "" : String.valueOf(objectValue);
    }

}
