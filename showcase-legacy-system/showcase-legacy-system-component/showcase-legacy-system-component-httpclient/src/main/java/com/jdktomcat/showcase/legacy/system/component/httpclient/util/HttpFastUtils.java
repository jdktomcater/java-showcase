package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.OrderMapConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
public class HttpFastUtils {
    /**
     * post请求方法，返回Body，可自定义请求头
     *
     * @param map 请求参数Map对象
     * @param url 请求路径
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> post(String url, RestTemplate restTemplate, Map<String, Object> map, Map<String, Object> headerMap) {
        if (CommonUtil.checkParamsIsBlank(map, url)) {
            return null;
        }
        url = constructUrl(url, map);
        changeType(map);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity requestEntity;
        if (CollectionUtils.isEmpty(headerMap)) {
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            dataProcessing(map);
            String param = JsonUtils.objectsToJson(map);
            requestEntity = new HttpEntity<>(param, headers);
        } else {
            String contentType = "";
            for (String key : headerMap.keySet()) {
                if (OrderMapConstants.PARAM_PREFIX_HEDAER_CONTENT_TYPE.equals(key)) {
                    contentType = (String) headerMap.get(key);
                    headers.add("Content-Type", contentType);
                } else {
                    headers.add(key.substring(7), (String) headerMap.get(key));
                }
            }
            if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                requestEntity = new HttpEntity(MapParseUtil.parseMultiValueMap(map), headers);
                if(contentType.startsWith("wk:")) {
                    //wk_开头的配置请求头按&处理
                    headers.setContentType(MediaType.valueOf(contentType.replace("wk:","")));
                    requestEntity = new HttpEntity(MapParseUtil.mapToParam(map), headers);
                }
                log.info("form表单类型请求:{}", url);
            } else if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                dataProcessing(map);
                String param = JsonUtils.objectsToJson(map);
                log.info("json类型请求:" + contentType);
                if(contentType.startsWith("jx:")) {
                    contentType = contentType.replace("jx:", "");
                    headers.setContentType(MediaType.valueOf(contentType));
                    String extParam = JSONUtil.toJsonPrettyStr(map.get("extParam"));
                    log.info("请求参数extParam03: {}", extParam);
                    map.put("extParam",extParam);
                    param = JSONUtil.toJsonPrettyStr(map);
                }
                requestEntity = new HttpEntity<>(param, headers);
            } else if(contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)){
                if (CommonUtil.checkParamsIsBlank(map, url)) {
                    return null;
                }
                if (headers == null) {
                    requestEntity = new HttpEntity<>(MapParseUtil.parseMultiValueMap(map));
                } else {
                    requestEntity = new HttpEntity<>(MapParseUtil.parseMultiValueMap(map), headers);
                }
            } else {
                log.error("不支持的报文请求Content-Type:" + contentType);
                throw new RuntimeException("不支持的报文请求Content-Type:" + contentType);
            }
        }
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return response;
    }

    /**
     * post请求方法，返回Body，可自定义请求头
     *
     * @param map 请求参数Map对象
     * @param url 请求路径
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> get(String url, RestTemplate restTemplate, Map<String, Object> map, Map<String, Object> headerMap) {
        if (CommonUtil.checkParamsIsBlank(map, url)) {
            return null;
        }
        url = constructUrl(url, map);
        String paramStr = Joiner.on("&").withKeyValueSeparator("=").join(map);
        url = url.concat("?");
        url = url.concat(paramStr);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity requestEntity;
        if (CollectionUtils.isEmpty(headerMap)) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        } else {
            for (String key : headerMap.keySet()) {
                if (OrderMapConstants.PARAM_PREFIX_HEDAER_CONTENT_TYPE.equals(key)) {
                    String contentType = (String) headerMap.get(key);
                    headers.add("Content-Type", contentType);
                } else {
                    headers.add(key.substring(7), (String) headerMap.get(key));
                }
            }
        }
        requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        return response;
    }

    /**
     * application/json格式的时候，值如果是json格式，则进行json对象转换处理
     *
     * @param map
     */
    private static void dataProcessing(Map<String, Object> map) {
        map.entrySet().forEach(v -> {
            String value = v.getValue().toString().trim();
            // 检验是否符合基本的json格式
            if (value.indexOf("{") == 0 && value.lastIndexOf("}") > 0) {
                try {
                    // 进行json对象转换
                    v.setValue(JSONObject.parseObject(value));
                } catch (Exception e) {
                    log.error("键：{}，值：{}，数据处理转换异常，信息：{}", v.getKey(), v.getValue(), e.getMessage());
                }
            } else if (value.indexOf("[") == 0 && value.lastIndexOf("]") > 0) {
                try {
                    // 进行json对象转换
                    v.setValue(JSONObject.parseArray(value));
                } catch (Exception e) {
                    log.error("键：{}，值：{}，数据处理转换异常，信息：{}", v.getKey(), v.getValue(), e.getMessage());
                }
            }
        });
    }

    private static void changeType(Map<String, Object> map){
        if (map.containsKey("changeToInt")){
            String keysStr = map.get("changeToInt").toString();
            String[] keys = keysStr.split(";");
            for (int i=0;i<keys.length;i++){
                String value = map.get(keys[i]).toString();
                map.replace(keys[i], BigDecimalUtils.newBigDecimal(value));
            }
            map.remove("changeToInt");
        }
    }
    private static String constructUrl(String url, Map<String, Object> map){
        if (url.contains("{") && url.contains("}")){
            log.info("进入请求地址构建方法");
            url = UriComponentsBuilder.fromHttpUrl(url)
                    .uriVariables(map)
                    .toUriString();
        }
        log.info("进入请求地址构建后url:"+url);
        return url;
    }
}
