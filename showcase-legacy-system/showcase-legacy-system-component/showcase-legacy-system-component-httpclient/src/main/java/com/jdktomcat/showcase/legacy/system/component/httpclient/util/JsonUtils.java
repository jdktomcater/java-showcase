package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class JsonUtils {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        //忽略反序列化中的未知列
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象转换成json字符串。
     * <p>Title: pojoToJson</p>
     * <p>Description: </p>
     *
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("JsonUtils objectToJson occur exception!"+e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (Exception e) {
            log.error("JsonUtils jsonToPojo occur exception!"+e.getMessage(), e);
        }
        return null;
    }


    /**
     * 多对象转json字符串
     *
     * @param map
     * @return
     */
    public static String objectsToJson(Map<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        map.forEach((s, o) -> {
            String str = objectToJson(o);
            sb.append("\"").append(s).append("\":").append(str).append(",");
        });
        return sb.substring(0, sb.length() - 1) + "}";
    }

    /**
     * 字符串转换为 Map<String, Object>
     *
     * @param jsonString
     * @return
     * @throws Exception
     */
    public static <T> Map<String, Object> json2map(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.readValue(jsonString, Map.class);
        } catch (Exception ex) {
            throw new RuntimeException("类型转化异常", ex);
        }
    }
}
