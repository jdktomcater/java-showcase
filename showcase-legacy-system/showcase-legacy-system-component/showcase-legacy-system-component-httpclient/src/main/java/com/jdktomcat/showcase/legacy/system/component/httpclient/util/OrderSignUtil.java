package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.FastConstants;
import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.OrderMapConstants;
import com.jdktomcat.showcase.legacy.system.component.httpclient.context.FastContext;
import com.jdktomcat.showcase.legacy.system.component.httpclient.context.ThirdCallParams;
import com.jdktomcat.showcase.legacy.system.component.httpclient.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * 订单sign工具类
 */
@Slf4j
public class OrderSignUtil {
    /**
     * 添加sign值
     * @param context 請求上下文
     * @param signParams 已排序的验签参数
     * @param privateKey   私钥
     */
    public static void addNoSignKeyMD5Sign(FastContext context, LinkedHashMap<String, Object> signParams, String privateKey, boolean hasKeyName) {
        TreeMap<String, Object> reqParams = context.getReqParam();
        StringBuilder beforeSign =new StringBuilder("");
        Collection cParams=signParams.values();
        for(Object objParams:cParams){
            beforeSign.append(objParams.toString());
        }
        if(hasKeyName){
            beforeSign= beforeSign.append("&").append(context.getPrivateKeyName()).append("=").append(privateKey);
        }else if(StringUtils.isEmpty(context.getPrivateKeyName())){
            beforeSign= beforeSign.append(privateKey);
        }
        String afterSign= Md5Utils.MD5(beforeSign.toString());
        //大小写转换
        if(FastConstants.CHAR_UP.equals(context.getSignCharType())){
            afterSign=afterSign.toUpperCase();
        }else if(FastConstants.CHAR_LOWER.equals(context.getSignCharType())){
            afterSign=afterSign.toLowerCase();
        }
        if(StringUtils.isNotEmpty(context.getSignKey())){
            reqParams.put(context.getSignKey(),afterSign);
        }else{
            reqParams.put("sign",afterSign);
        }
    }
    /**
     * 添加sign值
     * @param context 請求上下文
     * @param signParams 已排序的验签参数
     * @param privateKey   私钥
     */
    public static void addMD5Sign(FastContext context,LinkedHashMap<String, Object> signParams, String privateKey) {
        TreeMap<String, Object> reqParams = context.getReqParam();
        String beforeSign = Joiner.on("&").withKeyValueSeparator("=").join(signParams);
        if(StringUtils.isEmpty(context.getPrivateKeyName())){
            beforeSign= beforeSign.concat(privateKey);

        }else{
            // beforeSign= beforeSign.concat("&").concat(context.getPrivateKeyName()).concat("=").concat(privateKey);
        }
        String afterSign= Md5Utils.MD5(beforeSign);
        //大小写转换
        if(FastConstants.CHAR_UP.equals(context.getSignCharType())){
            afterSign=afterSign.toUpperCase();
        }else if(FastConstants.CHAR_LOWER.equals(context.getSignCharType())){
            afterSign=afterSign.toLowerCase();
        }
        if(StringUtils.isNotEmpty(context.getSignKey())){
            reqParams.put(context.getSignKey(),afterSign);
        }else{
            reqParams.put("sign",afterSign);
        }
    }

    /**
     * 添加sign值
     * @param context 請求上线文
     * @param signParams   已排序的验签参数
     * @param signParams   报文验签的加密私钥
     */
    public static void addCustomSign(FastContext context, LinkedHashMap<String, Object> signParams,String privateKey) {
        try {
            String signParamsJsonStr = JSONObject.toJSONString(signParams);
            if (signParamsJsonStr.contains(privateKey)){
                log.info("验签实现类:{} 已排序的验签参数:{}",context.getSignHandlerClass(), signParamsJsonStr.replaceAll(privateKey, "***和谐密钥***"));
            } else {
                log.info("验签实现类:{} 已排序的验签参数:{}",context.getSignHandlerClass(), signParamsJsonStr);
            }
        } catch (Exception e) {
            log.info("生成验签报文异常,渠道商户号:{}",context.getChannelMerchantId(),e);
            throw new ServiceException("生成验签报文异常");
        }
    }


    /**
     * 解密响应报文
     * @param context
     * @param response
     * @return
     */
    public static String decryptResponse(FastContext context, List<ThirdCallParams> params, String response){
        String strResponse=response;
        try {
            for(ThirdCallParams param:params) {
                if (OrderMapConstants.RESPONSE_HANDLER_CLASS.equals(param.getParamEnName())) {
                    context.setResonseHandlerClass(param.getParamValueExp());
                    log.info("响应报文解密实现类:{} ",context.getResonseHandlerClass());
                    break;
                }
            }
        } catch (Exception e) {
            log.error("解密响应报文异常,渠道商户号:{}",context.getChannelMerchantId(),e.getMessage());
            throw new ServiceException("生成验签报文异常");
        }
        return strResponse;
    }
}
