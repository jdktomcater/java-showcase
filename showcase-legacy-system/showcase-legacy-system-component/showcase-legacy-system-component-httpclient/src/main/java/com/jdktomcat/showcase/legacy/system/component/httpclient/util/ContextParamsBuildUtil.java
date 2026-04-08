package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.alibaba.fastjson.JSONObject;
import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.CallbackParam;
import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.FastConstants;
import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.OrderMapConstants;
import com.jdktomcat.showcase.legacy.system.component.httpclient.context.*;
import com.jdktomcat.showcase.legacy.system.component.httpclient.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 上线文参数构建工具
 */
@Slf4j
public class ContextParamsBuildUtil {

    /**
     * 构建订单入参上下文
     *
     * @param context
     */
    public static void buildInParamsContext(FastContext context, List<ThirdCallParams> inParams) {
        TreeMap<String, Object> reqParams = new TreeMap<>();
        Map<String, Object> reqHeaderParams = new HashMap<>();
        context.setReqHeaderParam(reqHeaderParams);
        context.setReqParam(reqParams);
        LinkedHashMap<String, Object> signParams = new LinkedHashMap<>();
        try {
            Map mOrder = context.getOrderMap();
            mOrder.put("channelMerchantId", context.getChannelMerchantId());
            try{
                log.info("商户请求报文可用参数值respDto:{}", Base64.getEncoder().encodeToString(mOrder.toString().getBytes("utf-8")));
            } catch (Exception e) {
                log.info("Base64出错");
            }
            //List<ThirdCallParams> callParams=context.getCallInParams();
            for (ThirdCallParams param : inParams) {
                String paramsValue = ExpressionExtractValueUtil.execute(param.getParamEnName(), param.getParamValueExp(), mOrder);
                if (OrderMapConstants.REQUEST_METHOD.equals(param.getParamEnName())) {
                    context.setMethod(paramsValue);
                } else if (OrderMapConstants.DEPOSIT_PAGE_METHOD.equals(param.getParamEnName())) {
                    if (context instanceof DepositContext) {
                        ((DepositContext) context).setDepositPageMethod(paramsValue);
                    }
                } else if (OrderMapConstants.SIGN_CHAR_TYPE.equals(param.getParamEnName())) {
                    context.setSignCharType(paramsValue);
                } else if (OrderMapConstants.SIGN_KEY.equals(param.getParamValueExp())) {
                    context.setSignKey(param.getParamEnName());
                } else if (OrderMapConstants.ENC_KEY_NAME.equals(param.getParamValueExp())) {
                    context.setPrivateKeyName(param.getParamEnName());
                    mOrder.put(param.getParamEnName(), context.getPrivateKey());
                    if (StringUtils.isNotEmpty(param.getParamEnName())) {
                        if (param.getOrderNo() > 0) {
                            signParams.put(param.getParamEnName(), context.getPrivateKey());
                        }
                    }
                } else if (OrderMapConstants.SIGN_PARAM_KEY_SWITCH.equals(param.getParamEnName())) {
                    context.setSignParamKeySwitch(paramsValue);
                } else if (OrderMapConstants.SIGN_HANDLER_CLASS.equals(param.getParamEnName())) {
                    context.setSignHandlerClass(paramsValue);
                } else if (OrderMapConstants.NOTIFY_URL_TYPE.equals(param.getParamEnName())) {
                    if (context instanceof DepositContext) {
                        ((DepositContext) context).setNotifyUrlType(paramsValue);
                    }
                } else if (OrderMapConstants.WITHDRAW_NOTIFY_URL_TYPE.equals(param.getParamEnName())) {
                    if (context instanceof WithdrawContext) {
                        ((WithdrawContext) context).setNotifyUrlType(paramsValue);
                    }
                } else if (OrderMapConstants.NOTIFY_URL_KEY.equals(param.getParamValueExp())) {
                    if (context instanceof DepositContext) {
                        String notifyUrl = ContextParamsBuildUtil.getNotifyUrl((DepositContext) context);
                        log.info("回调:{}", context.getClass().getName());
                        mOrder.put(param.getParamEnName(), notifyUrl);
                        ((DepositContext) context).setNotifyUrlKey(param.getParamEnName());
                        if (param.getOrderNo() >= 0) {
                            reqParams.put(param.getParamEnName(), notifyUrl);
                        }
                        if (param.getOrderNo() > 0) {
                            signParams.put(param.getParamEnName(), notifyUrl);
                        }
                    } else if (context instanceof WithdrawContext) {
                        String notifyUrl = ContextParamsBuildUtil.getWithdrawNotifyUrl((WithdrawContext) context);
                        log.info("回调:{}", context.getClass().getName());
                        mOrder.put(param.getParamEnName(), notifyUrl);
                        ((WithdrawContext) context).setNotifyUrlKey(param.getParamEnName());
                        if (param.getOrderNo() >= 0) {
                            reqParams.put(param.getParamEnName(), notifyUrl);
                        }
                        if (param.getOrderNo() > 0) {
                            signParams.put(param.getParamEnName(), notifyUrl);
                        }
                    }
                } else if (OrderMapConstants.ENCODE_NOTIFY_URL_KEY.equals(param.getParamValueExp())) {
                    if (context instanceof DepositContext) {
                        String notifyUrl = ContextParamsBuildUtil.getNotifyUrl((DepositContext) context);
                        notifyUrl = URLEncoder.encode(notifyUrl, "UTF-8");
                        mOrder.put(param.getParamEnName(), notifyUrl);
                        ((DepositContext) context).setNotifyUrlKey(param.getParamEnName());
                        if (param.getOrderNo() >= 0) {
                            reqParams.put(param.getParamEnName(), notifyUrl);
                        }
                        if (param.getOrderNo() > 0) {
                            signParams.put(param.getParamEnName(), notifyUrl);
                        }
                    } else if (context instanceof WithdrawContext) {
                        String notifyUrl = ContextParamsBuildUtil.getWithdrawNotifyUrl((WithdrawContext) context);
                        notifyUrl = URLEncoder.encode(notifyUrl, "UTF-8");
                        log.info("回调:{}", context.getClass().getName());
                        mOrder.put(param.getParamEnName(), notifyUrl);
                        ((WithdrawContext) context).setNotifyUrlKey(param.getParamEnName());
                        if (param.getOrderNo() >= 0) {
                            reqParams.put(param.getParamEnName(), notifyUrl);
                        }
                        if (param.getOrderNo() > 0) {
                            signParams.put(param.getParamEnName(), notifyUrl);
                        }
                    }
                } else if (OrderMapConstants.NOTIFY_RECEIVE_SUCCESS.equals(param.getParamEnName())) {
                    if (context instanceof NotifyContext) {
                        ((NotifyContext) context).setNotifyReceiveSuccess(paramsValue);
                        log.info("设置回调响应的成功码:{}", ((NotifyContext) context).getNotifyReceiveSuccess());
                    } else {
                        log.info("设置回调响应的成功码异常,当前类型:{}", context.getClass().getName());
                    }
                } else if (param.getParamEnName().startsWith(FastConstants.PARAM_PREFIX_HEDAER)) {
                    if (StringUtils.isNotEmpty(paramsValue)) {
                        reqHeaderParams.put(param.getParamEnName(), paramsValue);
                    }
                } else if (param.getParamEnName().startsWith(OrderMapConstants.SIGN_RSA_ALGORITHMS)) {
                    if (StringUtils.isNotEmpty(paramsValue)) {
                        // 设置算法，如果有配置的话
                        context.setSignRsaAlgorithms(paramsValue);
                    }
                } else if (FastConstants.REQ_KEY_TYPE.equals(param.getParamEnName())) {
                    context.setReqKeyType(param.getParamValueExp());
                } else if (FastConstants.CALLBACK_TOKEN.equals(param.getParamEnName())) {
                    //如果配置了反查token
                    if (context.getWorkType() == 2) {
                        ThirdChannelDTO thirdChannelDTO = context.getThirdChannelDTO();
                        if (thirdChannelDTO.getCallStatus() == 1) {
                            String callToken = System.currentTimeMillis() + String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
                            if (param.getOrderNo() >= 0) {
                                reqParams.put(FastConstants.CALLBACK_TOKEN, callToken);
                            }
                            if (param.getOrderNo() > 0) {
                                signParams.put(FastConstants.CALLBACK_TOKEN, callToken);
                            }
                        }
                    }
                    log.info("orderId:{} 参数配置了配置了反查token 参与验签判断OrderNo值:{}",context.getOrderId(), param.getOrderNo());
                } else if (FastConstants.CALLBACK_URL.equals(param.getParamEnName())) {
                    if (context.getWorkType() == 2) {
                        ThirdChannelDTO thirdChannelDTO = context.getThirdChannelDTO();
                        if (thirdChannelDTO.getCallStatus() == 1) {
                            List<String> payUrls = context.getPayUrl();
                            if(payUrls != null && payUrls.size()>0) {
                                int max = payUrls.size() - 1, min = 0;
                                int range = (int) (Math.random() * (max - min) + min);
                                JSONObject callBackJson = getCallBackUrl(paramsValue);
                                //反查接口配置了值的会使用配置接口 配置值为空则使用默认接口
                                String callBackUrl =payUrls.get(range).concat(callBackJson.getString(CallbackParam.参数VALUE.getCode()));
                                log.info("orderId:{} 参数配置反查url:{}",context.getOrderId(),callBackUrl);
                                if (param.getOrderNo() >= 0) {
                                    reqParams.put(callBackJson.getString(CallbackParam.参数NAME.getCode()), callBackUrl);
                                    context.setCallBackParamKey(callBackJson.getString(CallbackParam.参数NAME.getCode()));
                                }
                                if (param.getOrderNo() > 0) {
                                    signParams.put(callBackJson.getString(CallbackParam.参数NAME.getCode()), callBackUrl);
                                }
                            }else {
                                log.info("orderId:{} 参数配置了配置了反查url 无法获取回调地址",context.getOrderId());
                            }
                        }
                    }
                    log.info("orderId:{} 参数配置了配置了反查url 参与验签判断OrderNo值:{} 请求参数反查url:{} 验签参数反查url:{}",context.getOrderId(), param.getOrderNo(),reqParams.get(FastConstants.CALLBACK_URL),signParams.get(FastConstants.CALLBACK_URL));
                } else if (FastConstants.CALLBACK_URL_ON_OFF_KEY.equals(param.getParamEnName()) && paramsValue.equals("false")) {
                    //外层反查地址开关 true或者没配置-开启  false关闭
                    log.info("orderId:{} 参数配置了外层反查地址开关false");
                    context.setCallbackUrlOnOff(false);
                }else if (FastConstants.network_type.equals(param.getParamEnName())) {
                    log.info("orderId:{} 参数配置了网络类型:{}",context.getOrderId(),param.getParamValueExp());
                    context.setNetworkType(param.getParamValueExp());
                }else {
                    if (StringUtils.isNotEmpty(paramsValue)) {
                        if (param.getOrderNo() >= 0) {
                            reqParams.put(param.getParamEnName(), paramsValue);
                        }
                        if (param.getOrderNo() > 0) {
                            signParams.put(param.getParamEnName(), paramsValue);
                        }
                    }
                }
            }
            //withdrawVerifyJoinSign,1配置json格式加入签名，2配置json格式不加入签名，3常规模式加入签名，不设withdrawVerifyJoinSign，或者其它值走常规模式不加入签名
            if(context.getCallbackUrlOnOff() && reqParams.containsKey("withdrawVerifyJoinSign") && "1".equals(reqParams.get("withdrawVerifyJoinSign"))){
                log.info("构建进入withdrawVerifyJoinSign值1模式，配置json格式加入签名,orderId:{}", context.getOrderId());
                setWithdrawReverseQueryUrl(context);
                rewritWithdrawVerify(context, signParams, reqParams);//重写json格式反查
                setSign(context, signParams);
            }else if(context.getCallbackUrlOnOff() && reqParams.containsKey("withdrawVerifyJoinSign") && "2".equals(reqParams.get("withdrawVerifyJoinSign"))){
                log.info("构建进入withdrawVerifyJoinSign值2模式，配置json格式不加入签名,orderId:{}", context.getOrderId());
                setSign(context, signParams);
                setWithdrawReverseQueryUrl(context);
                rewritWithdrawVerify(context, signParams, reqParams);//重写json格式反查
            }else if(context.getCallbackUrlOnOff() && reqParams.containsKey("withdrawVerifyJoinSign") && "3".equals(reqParams.get("withdrawVerifyJoinSign"))){
                log.info("构建进入withdrawVerifyJoinSign值3模式，常规模式加入签名,orderId:{}", context.getOrderId());
                setWithdrawReverseQueryUrl(context);
                signParams.put("callToken",reqParams.get("callToken"));
                signParams.put("withdrawQueryUrl",reqParams.get("withdrawQueryUrl"));
                reqParams.remove("withdrawVerifyJoinSign");
                setSign(context, signParams);
            }else {
                log.info("构建进入无withdrawVerifyJoinSign常规本来模式,orderId:{}", context.getOrderId());
                setSign(context, signParams);
                if(context.getCallbackUrlOnOff()) {
                    setWithdrawReverseQueryUrl(context);
                }
            }
            if (context.getSignKey().startsWith(FastConstants.PARAM_PREFIX_HEDAER) && reqParams.containsKey(context.getSignKey())){
                log.info("orderId:{},进入请求头传递签名数据逻辑", context.getOrderId());
                reqHeaderParams.put(context.getSignKey(), reqParams.get(context.getSignKey()));
                reqParams.remove(context.getSignKey());
            }
        } catch (Throwable ex) {
            log.error("orderId:{} 请求参数:{}", context.getOrderId(), reqParams);
            log.error("orderId:{} 构建请求报文异常", context.getOrderId(), ex);
            throw new ServiceException("构建请求报文异常", ex);
        }
    }

    /**
     * 重写反查：withdrawVerifyKey值是反查商户定义字段名，withdrawVerifyValue值是自定义参数json格式键值都是已有参数,withdrawVerifyJoinSign=1，json反查参数加入签名加入签名
     * @param context
     * @param signParams
     * @param reqParams
     */
    private static void rewritWithdrawVerify(FastContext context, LinkedHashMap<String, Object> signParams,TreeMap<String, Object> reqParams) {
        log.info("进入反查参数重写逻辑orderId:{}，可用参数请求参数reqParams:{}", context.getOrderId(),reqParams);
        try{
            String withdrawVerifyKey_Value;
            if (reqParams.containsKey("withdrawVerifyValue")){
                log.info("进入反查参数重写逻辑自定义参数orderId:{}", context.getOrderId());
                JSONObject withdrawVerifyValueToJson = JSONObject.parseObject(reqParams.get("withdrawVerifyValue").toString());
                AtomicReference<String> signStr = new AtomicReference<>("");
                withdrawVerifyValueToJson.forEach((k, v) -> {
                    signStr.set(signStr +"\""+ k +"\"" + ":" +"\""+ (reqParams.containsKey(v) && Objects.nonNull(reqParams.get(v)) ? reqParams.get(v) : v)  +"\",");
                });
                withdrawVerifyKey_Value = "{" + signStr.toString().substring(0, signStr.toString().length() - 1) + "}";

            }else {
                log.info("进入反查参数重写逻辑默认参数orderId:{}", context.getOrderId());
                withdrawVerifyKey_Value = "{" + "\"callToken\":\"" + reqParams.get("callToken") +"\"" + ",\"withdrawQueryUrl\":\"" + reqParams.get("withdrawQueryUrl") +  "\"}";
            }
            //withdrawVerifyJoinSign,值=1配置json格式加入签名，值=2配置json格式不加入签名，值=3常规模式加入签名，不设withdrawVerifyJoinSign，或者其它值走常规模式不加入签名
            if(reqParams.containsKey("withdrawVerifyJoinSign") && "1".equals(reqParams.get("withdrawVerifyJoinSign"))){
                signParams.put(reqParams.get("withdrawVerifyKey").toString(),withdrawVerifyKey_Value);
                reqParams.put(reqParams.get("withdrawVerifyKey").toString(),withdrawVerifyKey_Value);
                reqParams.remove("callToken");
                reqParams.remove("withdrawQueryUrl");
                reqParams.remove("withdrawVerifyKey");
                reqParams.remove("withdrawVerifyValue");
                reqParams.remove("withdrawVerifyJoinSign");
            }else if(reqParams.containsKey("withdrawVerifyJoinSign") && "2".equals(reqParams.get("withdrawVerifyJoinSign"))){
                reqParams.put(reqParams.get("withdrawVerifyKey").toString(),withdrawVerifyKey_Value);
                reqParams.remove("callToken");
                reqParams.remove("withdrawQueryUrl");
                reqParams.remove("withdrawVerifyKey");
                reqParams.remove("withdrawVerifyValue");
                reqParams.remove("withdrawVerifyJoinSign");
            }
        } catch (Throwable ex) {
            log.error("orderId:{} 请求参数:{}", context.getOrderId(), reqParams);
            log.error("orderId:{} 构建请求报文异常", context.getOrderId(), ex);
            throw new ServiceException("构建请求报文异常,反查参数重写失败！！", ex);
        }
    }

    /**
     * 验签报文
     *
     * @param context
     * @param signParams
     */
    private static void setSign(FastContext context, LinkedHashMap<String, Object> signParams) {
        ThirdChannelDTO thirdChannelDTO = context.getThirdChannelDTO();
        if (StringUtils.isNotEmpty(context.getSignHandlerClass()) && context.getSignHandlerClass().startsWith(FastConstants.SIGN_HANDLER_CLASS_PREFIX)) {
            log.info("进入外挂签名类:{}", context.getSignHandlerClass());
            OrderSignUtil.addCustomSign(context, signParams, thirdChannelDTO.getPrivateKey());
        } else {
            if (FastConstants.SIGN_PARAM_KEY_OFF.equals(context.getSignParamKeySwitch())) {
                // 添加无参数key的签名
                log.info("优先进入添加无参数key的签名:{}", FastConstants.SIGN_PARAM_KEY_OFF);
                OrderSignUtil.addNoSignKeyMD5Sign(context, signParams, context.getPrivateKey(), false);
            } else if (FastConstants.SIGN_PARAM_KEY_OFF_HK.equals(context.getSignParamKeySwitch())) {
                // 添加无参数key的签名,但带上密钥的key名称
                log.info("其次进入添加无参数key的签名,但带上密钥的key名称:{}", FastConstants.SIGN_PARAM_KEY_OFF_HK);
                OrderSignUtil.addNoSignKeyMD5Sign(context, signParams, context.getPrivateKey(), true);
            } else {
                // 添加有参数key签名
                log.info("默认进入添加有参数key签名:当{}!={},{}!={}", FastConstants.SIGN_PARAM_KEY_OFF_HK,context.getSignParamKeySwitch(),FastConstants.SIGN_PARAM_KEY_OFF,context.getSignParamKeySwitch());
                OrderSignUtil.addMD5Sign(context, signParams, context.getPrivateKey());
            }
        }

    }

    /**
     * 设置提款反查URL
     *
     * @param context
     */
    private static void setWithdrawReverseQueryUrl(FastContext context) {
        if (context.getWorkType() == 2) {
            ThirdChannelDTO thirdChannelDTO = context.getThirdChannelDTO();
            if (thirdChannelDTO.getCallStatus() == 1) {
                List<String> payUrls = context.getPayUrl();
                // 反查接口地址
                if (null != payUrls) {
                    int max = payUrls.size() - 1, min = 0;
                    int range = (int) (Math.random() * (max - min) + min);
                    TreeMap<String, Object> reqParam = context.getReqParam();
                    //设置反查token
                    String callToken = System.currentTimeMillis() + String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
                    if(StringUtils.isEmpty(reqParam.get("withdrawQueryUrl").toString()) && context.getCallBackParamKey().equals(CallbackParam.参数NAME.getValue())) {
                        reqParam.put("withdrawQueryUrl", payUrls.get(range).concat("/thirdwithdraw/withdraw/verify"));
                    }
                    if(StringUtils.isEmpty(reqParam.get("callToken").toString())) {
                        reqParam.put("callToken", callToken);
                    }
                    ((WithdrawContext) context).setToken(reqParam.get("callToken").toString());
                }
            }
        }
    }

    /**
     * @param context
     * @return
     */
    public static String getNotifyUrl(DepositContext context) {
        String notifyUrl;
        List<String> payUrls = context.getPayUrl();
        // 通知接口地址
        if (!CollectionUtils.isEmpty(payUrls)) {
            int max = payUrls.size() - 1, min = 0;
            int range = (int) (Math.random() * (max - min) + min);
            if (FastConstants.NOTIFY_URL_TYPE_PARAM.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/mparam/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_PARAM_MAP.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/mparam/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_PARAM_MAP_GET.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/mgparam/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_BODY.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/mbody/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_BODY_MAP.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/mbody/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_BODY_BYTE.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/bbody/notify/" + context.getOrderId());
            } else {
                throw new RuntimeException("该商户天下支付域名未配置");
            }
        } else {
            log.info("orderId:{} 该商户天下支付域名未配置", context.getOrderId());
            throw new RuntimeException("该商户天下支付域名未配置");
        }
        return notifyUrl;
    }

    /**
     * 将配置的反查内容转json
     * @param paramsValue
     * @return 默认返回原来反查参数名称 FastConstants.CALLBACK_URL
     */
    public static JSONObject getCallBackUrl(String paramsValue) {
        JSONObject callBackJson = new JSONObject();
        callBackJson.put(CallbackParam.参数NAME.getCode(),CallbackParam.参数NAME.getValue());
        callBackJson.put(CallbackParam.参数VALUE.getCode(),CallbackParam.参数VALUE.getValue());
        try{
            if(StringUtils.isNotEmpty(paramsValue)) {
                callBackJson = JSONObject.parseObject(paramsValue);
            }
        }catch(Exception e){
            log.info("配置的参数:{} 无法转json,返回默认反查参数:{}",paramsValue,callBackJson);
        }
        return callBackJson;
    }

    /**
     * @param context
     * @return
     */
    public static String getWithdrawNotifyUrl(WithdrawContext context) {
        log.info("代付回调查单-设置回调地址,订单ID:{}",context.getOrderId());
        String notifyUrl;
        List<String> payUrls = context.getPayUrl();
        log.info("代付回调查单-设置回调地址,订单ID:{},回调地址集合：{}，接参类型：{}",context.getOrderId(), payUrls, context.getNotifyUrlType());
        // 通知接口地址
        if (!CollectionUtils.isEmpty(payUrls)) {
            int max = payUrls.size() - 1, min = 0;
            int range = (int) (Math.random() * (max - min) + min);
            if (FastConstants.NOTIFY_URL_TYPE_PARAM.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/withdraw/mparam/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_PARAM_MAP.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/withdraw/mparam/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_PARAM_MAP_GET.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/withdraw/mgparam/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_BODY.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/withdraw/mbody/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_BODY_MAP.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/withdraw/mbody/notify/" + context.getOrderId());
            } else if (FastConstants.NOTIFY_URL_TYPE_BODY_BYTE.equals(context.getNotifyUrlType())) {
                notifyUrl = payUrls.get(range).concat("/third/fast/withdraw/bbody/notify/" + context.getOrderId());
            } else {
                throw new RuntimeException("该商户天下支付域名未配置");
            }
        } else {
            log.info("orderId:{} 该商户天下支付域名未配置", context.getOrderId());
            throw new RuntimeException("该商户天下支付域名未配置");
        }
        log.info("代付回调查单-设置回调地址,订单ID:{},回调地址：{}",context.getOrderId(), notifyUrl);
        return notifyUrl;
    }
}
