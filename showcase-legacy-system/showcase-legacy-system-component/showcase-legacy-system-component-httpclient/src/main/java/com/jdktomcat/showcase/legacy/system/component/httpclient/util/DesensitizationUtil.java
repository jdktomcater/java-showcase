package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 日志脱敏工具类
 */
@Slf4j
public class DesensitizationUtil {

    private static final int MASK_LENGTH = 6;
    /**
     * json字符串格式脱敏
     * @param value 待脱敏内容
     */
    public static String jsonDesensit(String value) {
        try{
            JSONObject jsonObject = JSONObject.parseObject(value);
            return jsonDesensit(jsonObject).toJSONString();
        }catch (Exception e){
            return value;
        }
    }

    /**
     * map格式脱敏
     * @param value 待脱敏内容
     */
    public static String mapDesensit(Map value) {
        String before = JSONObject.toJSONString(value);
        try{
            JSONObject jsonObject = JSONObject.parseObject(before);
            return jsonDesensit(jsonObject).toJSONString();
        }catch (Exception e){
            return before;
        }
    }

    private static JSONObject jsonDesensit(JSONObject before) {
        try{
            for (String key:before.keySet()){
                if (before.get(key) == null){
                    continue;
                }
                String lowerCaseKey = key.toLowerCase();
                if (lowerCaseKey.contains("url") || lowerCaseKey.contains("amount") || lowerCaseKey.contains("money")){
                    continue;
                }
                if (before.get(key) instanceof String){
                    String value = before.getString(key);
                    if (StringUtils.isBlank(value)){
                        continue;
                    }
                    if (value.startsWith("{") && value.endsWith("}")){
                        before.replace(key, jsonDesensit(JSONObject.parseObject(value)).toJSONString());
                    }else {
                        before.replace(key, fillMask(value,3,3));
                    }
                } else if (before.get(key) instanceof JSONObject){
                    before.replace(key, jsonDesensit((JSONObject) before.get(key)));
                } else {
                    String value = before.get(key).toString();
                    if (StringUtils.isBlank(value)){
                        continue;
                    }
                    if (value.startsWith("[") && value.endsWith("]")){
                        before.replace(key, fillMask(value,4,4));
                    } else{
                        continue;
                    }
                }
            }
        }catch (Exception e){
            log.info("日志脱敏异常，待脱敏日志：{}，异常信息：", before.toJSONString(), e);
        }
        return before;
    }

    private static String fillMask(String value, int leftLength, int rightLength) {
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            int valueLength = value.length();
            try {
                int keepLength = leftLength + rightLength;
                String valueNew;
                switch(valueLength) {
                    case 1:
                        valueNew = value;
                        break;
                    case 2:
                        valueNew = value.substring(0,1)+"*";
                        break;
                    case 3:
                        valueNew = value.substring(0,1)+"*"+value.substring(2,3);
                        break;
                    case 4:
                        valueNew = value.substring(0,1)+"*"+value.substring(2,4);
                        break;
                    case 5:
                        valueNew = value.substring(0,1)+"*"+value.substring(3,5);
                        break;
                    default:
                        StringBuilder stringBuilder;
                        int stringBuilderLength;
                        if (valueLength - keepLength > 1) {
                            stringBuilderLength = keepLength + MASK_LENGTH;
                            stringBuilder = new StringBuilder(stringBuilderLength);
                            stringBuilder.append(value, 0, leftLength);
                            stringBuilder.append("******");
                            stringBuilder.append(value, valueLength - rightLength, valueLength);
                        } else {
                            stringBuilderLength = MASK_LENGTH + 2;
                            stringBuilder = new StringBuilder(stringBuilderLength);
                            stringBuilder.append(value.charAt(0));
                            stringBuilder.append("******");
                            stringBuilder.append(value.charAt(valueLength - 1));
                        }
                        valueNew = stringBuilder.toString();
                }

                return valueNew;
            } catch (Exception var8) {
              log.info("脱敏异常：", var8);
              return value;
            }
        }
    }

}
