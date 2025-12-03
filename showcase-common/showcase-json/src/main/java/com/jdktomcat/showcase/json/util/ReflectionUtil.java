package com.jdktomcat.showcase.json.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;


/**
 * 反射相关工具类
 */
@Slf4j
public class ReflectionUtil {

    /**
     * 数组分割符
     */
    private static final String ARRAY_SEPARATOR = ";";


    /**
     * 动态访问对象属性内容
     *
     * @param obj       对象
     * @param fieldName 属性
     * @return 属性值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || StringUtils.isBlank(fieldName)) {
            log.error("getFieldValue参数中存在空，请检查！");
            return null;
        }
        // 判断是否为基础类型
        if ((obj instanceof String) || (obj instanceof Long) || (obj instanceof Integer) || (obj instanceof Double)) {
            log.info("getFieldValue参数为基础类型！");
            return obj;
        }
        // 判断是否为批量
        if(obj instanceof String[] keys){
            log.info("getFieldValue参数为批量数组类型！");
            return String.join(ARRAY_SEPARATOR, keys);
        }
        // 判断是否为列表
        if (obj instanceof List<?> updateDataList) {
            // 转换为 List 类型
            String[] keys = new  String[updateDataList.size()];
            for (int i = 0; i < updateDataList.size(); i++) {
                Object value = getFieldValue(updateDataList.get(i), fieldName);
                keys[i] = (value == null ? null : value.toString());
            }
            return String.join(ARRAY_SEPARATOR, keys);
        }
        // 查找类属性
        Class<?> clazz = obj.getClass();
        // 循环父类
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                // 往父类找
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                log.error("对象非法访问属性异常！", e);
                return null;
            }
        }
        log.warn("对象：{}属性：{}不存在！", obj.getClass().getName(), fieldName);
        return null;
    }
}
