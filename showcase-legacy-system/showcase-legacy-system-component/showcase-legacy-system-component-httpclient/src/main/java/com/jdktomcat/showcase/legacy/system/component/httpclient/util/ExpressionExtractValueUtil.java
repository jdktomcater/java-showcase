package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.OrderMapConstants;
import com.jdktomcat.showcase.legacy.system.component.httpclient.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExpressionExtractValueUtil {
    /**
     * 通过表达式提取值
     * @param paramKey 参数名称
     * @param expName 表达式
     * @param data  内容集合
     * @return
     */
    public static String execute(String paramKey,String expName, Map<String,Object> data){
        String paramValue="";
        try{
            if(PARAM_KEYS.contains(paramKey)){
                paramValue=expName==null?"":expName;
                paramValue=paramValue.replace("'","");
            }else if(PARAM_EXP_KEYS.contains(expName)){
                paramValue=paramKey==null?"":paramKey;
                paramValue=paramValue.replace("'","");
            }
        }catch (Exception ex){
            log.error("内容提取失败,表达式:{}  内容集:{}  异常:{}",expName,data,ex.getMessage());
            throw  new ServiceException("内容提取失败:"+expName);
        }
        return paramValue;
    }

    private final static List<String> PARAM_KEYS=new ArrayList<>();
    static {
        PARAM_KEYS.add(OrderMapConstants.ENC_KEY_NAME);
        PARAM_KEYS.add(OrderMapConstants.REQUEST_METHOD);
        PARAM_KEYS.add(OrderMapConstants.SIGN_CHAR_TYPE);
        PARAM_KEYS.add(OrderMapConstants.SIGN_KEY);
        PARAM_KEYS.add(OrderMapConstants.SIGN_HANDLER_CLASS);
        PARAM_KEYS.add(OrderMapConstants.PARAM_PREFIX_HEDAER_CONTENT_TYPE);
        PARAM_KEYS.add(OrderMapConstants.NOTIFY_URL_KEY);
        PARAM_KEYS.add(OrderMapConstants.NOTIFY_URL_TYPE);
        PARAM_KEYS.add(OrderMapConstants.NOTIFY_RECEIVE_SUCCESS);
        PARAM_KEYS.add(OrderMapConstants.RESPONSE_HANDLER_CLASS);
        PARAM_KEYS.add(OrderMapConstants.SIGN_HANDLER_CLASS);
        PARAM_KEYS.add(OrderMapConstants.WITHDRAW_NOTIFY_URL_TYPE);

        // PARAM_KEYS.add(OrderMapConstants.RES_DEPOSIT_URL);
        //PARAM_KEYS.add(OrderMapConstants.CHANNEL_ORDER_ID);
        // PARAM_KEYS.add(OrderMapConstants.CHANNEL_BALANCE);
        // PARAM_KEYS.add(OrderMapConstants.PAID_AMOUNT);
        //PARAM_KEYS.add(OrderMapConstants.ORDER_ID);
        //PARAM_KEYS.add(OrderMapConstants.REQUEST_RES_MSG);
       // PARAM_KEYS.add(OrderMapConstants.THIRD_ORDER_STATUS);
    }

    private final static List<String> PARAM_EXP_KEYS=new ArrayList<>();
    static {
        PARAM_EXP_KEYS.add(OrderMapConstants.ENC_KEY_NAME);
        PARAM_EXP_KEYS.add(OrderMapConstants.SIGN_KEY);
        PARAM_EXP_KEYS.add(OrderMapConstants.NOTIFY_URL_KEY);
    }
}
