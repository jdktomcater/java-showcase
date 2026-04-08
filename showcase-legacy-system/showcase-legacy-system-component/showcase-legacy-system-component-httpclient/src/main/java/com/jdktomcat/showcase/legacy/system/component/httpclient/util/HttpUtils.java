package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.jdktomcat.showcase.legacy.system.component.httpclient.config.HttpProxy;
import com.jdktomcat.showcase.legacy.system.component.httpclient.config.RequestFactoryHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * HTTP请求工具类
 *
 * @author cofess
 * @description
 * @date 2020/11/27 15:23
 */
@Slf4j
@Component
public class HttpUtils {

    private static String proxyIp = "127.0.0.1";
    private static int proxyPort = 13080;
    private static boolean proxyActive = false;

    /**
     * 使用国内的代理服务器
     */
    private static String specialProxyIp = "127.0.0.1";
    private static int specialProxyPort = 13080;
    private static boolean specialProxyActive = false;

    private static RequestFactoryHelper requestFactoryHelper;

    public HttpUtils(RequestFactoryHelper requestFactoryHelper) {
        HttpUtils.requestFactoryHelper = requestFactoryHelper;
    }

    @Value("${third.proxy.ip:127.0.0.1}")
    public void setProxyIp(String ip) {
        if (null != ip) {
            proxyIp = ip;
        } else {
            proxyIp = ip;
        }
    }

    @Value("${third.proxy.port:13080}")
    public void setProxyPort(int port) {
        if (port > 0) {
            proxyPort = port;
        } else {
            proxyPort = port;
        }
    }

    @Value("${third.proxy.active:false}")
    public void setProxyActive(boolean active) {
        if (active) {
            proxyActive = active;
            log.info("代理设置--> 代理开关打开:{}", proxyActive);
        } else {
            proxyActive = active;
            log.info("代理设置--> 代理开关未打开:{}", proxyActive);
        }
    }

    @Value("${third.proxy.special.ip:127.0.0.1}")
    public void setSpecialProxyIp(String ip) {
        if (null != ip) {
            specialProxyIp = ip;
        } else {
            specialProxyIp = ip;
        }
    }

    @Value("${third.proxy.special.port:13081}")
    public void setSpecialProxyPort(int port) {
        if (port > 0) {
            specialProxyPort = port;
        } else {
            specialProxyPort = port;
        }
    }

    @Value("${third.proxy.special.active:false}")
    public void setSpecialProxyActive(boolean active) {
        if (active) {
            specialProxyActive = active;
            log.info("代理设置--> 国内代理服务器开关打开:{}", specialProxyActive);
        } else {
            specialProxyActive = active;
            log.info("代理设置--> 国内代理服务器开关未打开:{}", specialProxyActive);
        }
    }

    public static LinkedMap getProxyMsg() {
        return new LinkedMap() {{
            put("proxyIp", proxyIp);
            put("proxyPort", proxyPort);
        }};
    }

    public static void setProxy(RestTemplate restTemplate) {
        if (proxyActive) {
            restTemplate.setRequestFactory(clientHttpRequestFactory(proxyIp, proxyPort));
        } else {
            restTemplate.setRequestFactory(clientHttpRequestFactoryWithoutProxy());
        }
    }

    public static void setSpecialProxy(RestTemplate restTemplate) {
        if (specialProxyActive) {
            restTemplate.setRequestFactory(clientHttpRequestFactory(specialProxyIp, specialProxyPort));
        } else {
            restTemplate.setRequestFactory(clientHttpRequestFactoryWithoutProxy());
        }
    }

    public static ClientHttpRequestFactory clientHttpRequestFactory(String proxyIp, Integer proxyPort) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(requestFactoryHelper.getHttpClientBuilder()
                .setProxy(new HttpHost(proxyIp, proxyPort)).build());
        return clientHttpRequestFactory;
    }

    public static ClientHttpRequestFactory clientHttpRequestFactoryWithoutProxy() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        HttpClientBuilder httpClientBuilder = requestFactoryHelper.getHttpClientBuilder();
        clientHttpRequestFactory.setHttpClient(httpClientBuilder.build());
        return clientHttpRequestFactory;
    }

    /**
     * post请求方法，返回Body
     *
     * @param map 请求参数Map对象
     * @param url 请求路径
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> post(RestTemplate restTemplate, Map<String, Object> map, String url) throws Exception {
        // 设置默认设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        return post(restTemplate, headers, map, url);
    }

    /**
     * post请求方法，返回Body，可自定义请求头
     *
     * @param headers 可自定义请求头，要求：org.springframework.http.HttpHeaders
     * @param map     请求参数Map对象
     * @param url     请求路径
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> post(RestTemplate ignore, HttpHeaders headers, Map<String, Object> map, String url) throws Exception {
        if (CommonUtil.checkParamsIsBlank(map, url)) {
            return null;
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = null;
        if (headers == null) {
            requestEntity = new HttpEntity<>(MapParseUtil.parseMultiValueMap(map));
        } else {
            requestEntity = new HttpEntity<>(MapParseUtil.parseMultiValueMap(map), headers);
        }
//        setProxy(restTemplate);
        return TemplateHolderHelper.external().exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    /**
     * post机小乔用，根据端口内网ip选择代理ip
     * @param ignore
     * @param headers
     * @param map
     * @param url
     * @param switchSpecialProxyPortAndIp
     * @return
     * @throws Exception
     */
    public static ResponseEntity<String> post(RestTemplate ignore, HttpHeaders headers, Map<String, Object> map, String url,String switchSpecialProxyPortAndIp) throws Exception {
        String ip = null;
        String port = null;
        try {
            String[] arr = switchSpecialProxyPortAndIp.split("\\|");
            ip = arr[0];
            port = arr[1];
        } catch (Exception e) {
            log.error("分割ip和端口报错");
        }
        if (CommonUtil.checkParamsIsBlank(map, url,ip,port)) {
            log.info("map,ip,端口或者url为空");
            return null;
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = null;
        if (headers == null) {
            requestEntity = new HttpEntity<>(MapParseUtil.parseMultiValueMap(map));
        } else {
            requestEntity = new HttpEntity<>(MapParseUtil.parseMultiValueMap(map), headers);
        }
        //根据端口设置代理服务
        RestTemplate restTemplate = TemplateHolderHelper.custom(ip, Integer.valueOf(port));
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    /**
     * post请求方法，返回Body
     *
     * @param map 请求参数Map对象
     * @param url 请求路径
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> postBySpecial(RestTemplate restTemplate, Map<String, Object> map, String url) throws Exception {
        // 设置默认设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        return postBySpecial(restTemplate, headers, map, url);
    }

    /**
     * post请求方法，返回Body，可自定义请求头，使用国内的代理服务器访问
     *
     * @param headers 可自定义请求头，要求：org.springframework.http.HttpHeaders
     * @param map     请求参数Map对象
     * @param url     请求路径
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> postBySpecial(RestTemplate ignore, HttpHeaders headers, Map<String, Object> map, String url) throws Exception {
        if (CommonUtil.checkParamsIsBlank(map, url)) {
            return null;
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = null;
        if (headers == null) {
            requestEntity = new HttpEntity<>(MapParseUtil.parseMultiValueMap(map));
        } else {
            requestEntity = new HttpEntity<>(MapParseUtil.parseMultiValueMap(map), headers);
        }
        RestTemplate restTemplate = TemplateHolderHelper.external();
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    /**
     * post请求方法，返回Body，可自定义请求头，使用国内的代理服务器访问
     *
     * @param restTemplate 请求类
     * @param paramsJson   请求参数
     * @param url          请求路径
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> postForJsonBySpecial(RestTemplate restTemplate, JSONObject paramsJson, String url) throws Exception {
        return Rwh.specialPostJson(url).body(paramsJson.toString()).exec();
    }

    /**
     * post请求方法，返回Body
     *
     * @param map 请求参数Map对象
     * @param url 请求路径
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> get(RestTemplate restTemplate, Map<String, Object> map, String url) throws Exception {
        // 设置默认设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        return get(restTemplate, headers, map, url);
    }

    /**
     * Ge请求方法，返回Body，可自定义请求头
     *
     * @param headers 可自定义请求头，要求：org.springframework.http.HttpHeaders
     * @param map     请求参数Map对象
     * @param url     请求路径
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> get(RestTemplate ignore, HttpHeaders headers, Map<String, Object> map, String url) throws Exception {
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//        if (map != null && !map.isEmpty()) {
//            String paramStr = Joiner.on("&").withKeyValueSeparator("=").join(map);
//            url = url.concat("?");
//            url = url.concat(paramStr);
//            log.info("请求地址：{}", url);
//        }
//        RestTemplate restTemplate = TemplateHolderHelper.external();
//        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        return Rwh.get(url).headers(headers).uriVars(map).exec();
    }

    /**
     * post方法请求：针对请求头必须为【application/json】的请求方式
     *
     * @param param 把参数JSON后的参数，例如：{"a":"v1","b":"v2"}
     * @param url   请求地址
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> postForJson(RestTemplate restTemplate, String param, String url) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        return postForJson(restTemplate, headers, param, url);
    }

    /**
     * post方法请求：针对请求头必须为【application/json】的请求方式
     *
     * @param paramsJson JSONObject对象
     * @param url        请求地址
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> postForJson(RestTemplate restTemplate, JSONObject paramsJson, String url) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        return postForJson(restTemplate, headers, paramsJson.toString(), url);
    }

    /**
     * post方法请求：针对请求头必须为【application/json】的请求方式
     *
     * @param headers    自定义请求头
     * @param paramsJson JSONObject对象
     * @param url        请求地址
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> postForJson(RestTemplate restTemplate, HttpHeaders headers, JSONObject paramsJson, String url) throws Exception {
        return postForJson(restTemplate, headers, paramsJson.toString(), url);
    }

    /**
     * post方法请求：针对请求头必须为【application/json】的请求方式
     *
     * @param headers 自定义请求头
     * @param param   把参数JSON后的参数，例如：{"a":"v1","b":"v2"}
     * @param url     请求地址
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author cofess
     */
    public static ResponseEntity<String> postForJson(RestTemplate restTemplate, HttpHeaders headers, String param, String url) throws Exception {
        if (CommonUtil.checkParamsIsBlank(param, url)) {
            return null;
        }
        return Rwh.postJson(url).headers(headers).body(param).exec();
    }

    /**
     * 原生post方法请求
     *
     * @param paramMap 参数
     * @param url      请求地址
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author application
     */
    public static String httpSendPost(String url, Map<String, Object> paramMap) throws Exception {
        String paramJson = "";
        if (paramMap != null && paramMap.size() > 0) {
            paramJson = Joiner.on("&").withKeyValueSeparator("=").join(paramMap);
        }
        return httpSendPost(url, paramJson, null);
    }

    /**
     * 原生post方法请求
     *
     * @param json 参数
     * @param url  请求地址
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author application
     */
    public static String httpSendPostForJson(String url, JSONObject json) throws Exception {
        String paramJson = "";
        if (json != null) {
            paramJson = String.valueOf(json);
        }
        return httpSendPost(url, paramJson, null);
    }

    /**
     * 原生post方法请求
     *
     * @param paramMap 参数
     * @param url      请求地址
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author application
     */
    public static String httpSendPostAndHeads(String url, Map<String, Object> paramMap, Map<String, String> headers) throws Exception {
        String paramJson = "";
        if (paramMap != null && paramMap.size() > 0) {
            paramJson = Joiner.on("&").withKeyValueSeparator("=").join(paramMap);
        }
        return httpSendPost(url, paramJson, headers);
    }

    /**
     * 原生GET方法请求
     *
     * @param paramMap 参数
     * @param url      请求地址
     * @return 获取到的内容
     * @throws Exception 在请求的过程中有可能会发生某些不可控异常
     * @author application
     */
    public static String httpSendGetAndHeads(String url, Map<String, Object> paramMap, Map<String, String> headers) throws Exception {
        String paramJson = "";
        if (paramMap != null && paramMap.size() > 0) {
            paramJson = Joiner.on("&").withKeyValueSeparator("=").join(paramMap);
            url = url.concat("?").concat(paramJson);
        }

        return httpSendPost(url, paramJson, headers);
    }

    /**
     * 发送请求报文，获得返回报文
     *
     * @param url
     * @param param
     * @return
     */
    public static String httpSendPost(String url, String param, Map<String, String> headers) throws Exception {
        return apacheHttpClient(url, param, headers, proxyActive, proxyPort, proxyIp);
    }

    /**
     * 发送请求报文，获得返回报文
     *
     * @param url
     * @param
     * @return
     */
    public static String httpSendGet(String url, Map<String, String> headers) throws Exception {
        return apacheHttpClient(url, null, headers, proxyActive, proxyPort, proxyIp);
    }

    /**
     * 发送请求报文，获得返回报文
     *
     * @param url
     * @param param
     * @return
     */
    public static String apachePostBySpecial(String url, String param, Map<String, String> headers) throws Exception {
        return apacheHttpClient(url, param, headers, specialProxyActive, specialProxyPort, specialProxyIp);
    }

    /**
     * 三服通用国内代理--控制端口
     * @param url
     * @param param
     * @param headers
     * @param switchSpecialProxyPort 选择代理端口
     * @return
     * @throws Exception
     */
    public static String apachePostBySpecialProxyPort(String url, String param, Map<String, String> headers, String switchSpecialProxyPort) throws Exception {
        return apacheHttpClient(url, param, headers, specialProxyActive, Integer.valueOf(switchSpecialProxyPort), specialProxyIp);
    }

    /**
     * 发送请求报文，获得返回报文
     *
     * @param url         地址
     * @param param       参数字符串，如果为null则使用get请求，有值为post请求
     * @param headers     请求头
     * @param proxyActive 代理开关 true false
     * @param proxyPort   代理端口
     * @param proxyIp     代理IP
     * @return 请求结果
     * @throws UnsupportedEncodingException
     */
    public static String apacheHttpClient(String url, String param, Map<String, String> headers, boolean proxyActive, int proxyPort, String proxyIp) throws UnsupportedEncodingException {
        RequestWrapper requestWrapper;
        // 是否为post请求
        if (StringUtils.isNotBlank(param)) {
            log.info("进入POST请求模式");
            requestWrapper = Rwh.post(url).body(param);
        } else {
            log.info("进入GET请求模式");
            requestWrapper = Rwh.get(url);
        }
        if (headers != null) {
            headers.forEach(requestWrapper::header);
        }
        return requestWrapper.proxy(HttpProxy.of(proxyIp, proxyPort, proxyActive))
                .exec()
                .getBody();
    }

    public static String apachePostBySpecialProxyPort_map(String url, Map<String, String> param, Map<String, String> headers, String switchSpecialProxyPort) throws Exception {
        return apacheHttpClient_map(url, param, headers, specialProxyActive, Integer.valueOf(switchSpecialProxyPort), specialProxyIp);
    }
    public static String apacheHttpClient_map(String url, Map<String, String> param, Map<String, String> headers, boolean proxyActive, int proxyPort, String proxyIp) throws UnsupportedEncodingException {
        RequestWrapper requestWrapper;
        // 是否为post请求
        if (StringUtils.isNotBlank(param.toString())) {
            log.info("进入POST请求模式");
            requestWrapper = Rwh.post(url).mBody(param);
        } else {
            log.info("进入GET请求模式");
            requestWrapper = Rwh.get(url);
        }
        if (headers != null) {
            headers.forEach(requestWrapper::header);
        }
        return requestWrapper.proxy(HttpProxy.of(proxyIp, proxyPort, proxyActive))
                .exec()
                .getBody();
    }
}
