package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tracy
 */
public class MapParseUtil {
    private static final String EQUAL_SPLIT = "=";
    private static final String DATA_SPLIT = "&";


    /**
     * String里面的键值对："key=value&key=value"  转为Map
     */
    public static Map<String, Object> keyValueParseToMap(String data) {
        Map<String, Object> map = new LinkedHashMap<>();
        String[] strArray = data.split(DATA_SPLIT);
        for (String str : strArray) {
            String[] eqaulStr = str.split(EQUAL_SPLIT);
            if (eqaulStr.length < 2) {
                map.put(eqaulStr[0], "");
            } else {
                map.put(eqaulStr[0], eqaulStr[1]);
            }

        }
        return map;
    }

    /**
     * 转换:map转为MultiValueMap
     */
    public static MultiValueMap<String, String> parseMultiValueMap(Map<String, Object> map) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        map.forEach((k, v) -> {
            if(Objects.nonNull(v)){
                multiValueMap.add(k, v.toString());
            }else{
                multiValueMap.add(k, "");
            }
        });
        return multiValueMap;
    }


    /**
     * 转换:map转为MultiValueMap
     */
    public static MultiValueMap<String, String> parseMultiValueStringMap(Map<String, String> map) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        map.forEach((k, v) -> {
            if(Objects.nonNull(v)){
                multiValueMap.add(k, v.toString());
            }else{
                multiValueMap.add(k, "");
            }
        });
        return multiValueMap;
    }

    /**
     * map 转化为&连接 from悟空
     * @param mapParam
     * @return
     */
    public static String mapToParam(Map<String, Object> mapParam) {
        StringBuilder params = new StringBuilder();
        Set<Map.Entry<String, Object>> entrySet = mapParam.entrySet();
        for (Map.Entry<String, Object> p : entrySet) {
            params.append(p.getKey());
            params.append("=");
            params.append(""+p.getValue());
            params.append("&");
        }
        return params.substring(0, params.length() - 1);
    }
}
