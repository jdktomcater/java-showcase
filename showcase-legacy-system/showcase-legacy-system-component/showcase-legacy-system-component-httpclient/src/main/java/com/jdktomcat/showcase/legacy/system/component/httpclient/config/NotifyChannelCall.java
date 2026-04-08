package com.jdktomcat.showcase.legacy.system.component.httpclient.config;

import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.FastConstants;
import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.NetworkTypeEnum;
import com.jdktomcat.showcase.legacy.system.component.httpclient.context.NotifyContext;
import com.jdktomcat.showcase.legacy.system.component.httpclient.exception.RetrieableQueryOrderException;
import com.jdktomcat.showcase.legacy.system.component.httpclient.util.HttpFastUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Slf4j
@Service
public class NotifyChannelCall {
    @Resource
    private RestTemplate restHttpsTemplate;
    @Resource
    private NotifyResVerification resVerification;

    @Autowired
    private RestTemplate innerNetworkrestProxyeTemplat;
    /**
     * 3.向三方发起下单请求
     *
     * @return
     */
    public NotifyResVerification queryOrderByChannel(NotifyContext context) {
        ResponseEntity<String> res =null;
        try{
            log.info("orderId:{} 收到通知-开始查单,URL:{} 头部:{},三方商户编码:{},QueryUrl:{}",context.getOrderId(), context.getQueryUrl(),
                    context.getReqHeaderParam(),context.getMerchantCode(),context.getQueryUrl());

            if ("dcpay".equals(context.getMerchantCode()) || context.getQueryUrl().contains("/wallet-api/getDepositDetail")){
                log.info("orderId:{} 查单属于DC渠道停止配置参数自动化查单请求",context.getOrderId());
                throw new RetrieableQueryOrderException("查单属于DC渠道停止配置参数自动化查单请求");
            }

            //内外网标识 network_type 0内网 1外网 //默認走代理
            RestTemplate sentRestTemplate = restHttpsTemplate;
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(context.getNetworkType())) {
                if(context.getNetworkType().equals(NetworkTypeEnum.内网.getNetworkTypeId())) {
                    //不走代理
                    sentRestTemplate = innerNetworkrestProxyeTemplat;
                    log.info("orderId:{} 未走代理 网络类型:{}", context.getOrderId(),NetworkTypeEnum.内网.getNetworkTypeName());
                }else if (context.getNetworkType().equals(NetworkTypeEnum.外网.getNetworkTypeId())) {
                    //走代理
                    log.info("orderId:{} 走代理 网络类型:{}", context.getOrderId(),NetworkTypeEnum.外网.getNetworkTypeName());
                }
            }

            //向三方通道发起请求
            if (context.getQueryUrl().contains("/user-order/queryOrder")){
                log.info("orderId:{} 收到通知-进入全民模式-开始查单,URL:{} 头部:{}",context.getOrderId(),context.getQueryUrl(),context.getReqHeaderParam());
                if (FastConstants.METHOD_GET.equals(context.getMethod())) {
                    res = HttpFastUtils.get(context.getQueryUrl(), sentRestTemplate, context.getReqParam(), context.getReqHeaderParam());
                } else {
                    res = HttpFastUtils.post(context.getQueryUrl(), sentRestTemplate, context.getReqParam(), context.getReqHeaderParam());
                }
            }else {
                if (FastConstants.METHOD_GET.equals(context.getMethod())) {
                    res = HttpFastUtils.get(context.getQueryUrl(), sentRestTemplate, context.getReqParam(), context.getReqHeaderParam());
                } else {
                    res = HttpFastUtils.post(context.getQueryUrl(), sentRestTemplate, context.getReqParam(), context.getReqHeaderParam());
                }
            }
            context.setRes(res.getBody());
        }catch (Throwable ex){
            log.error("orderId:{} 提交商户查单异常，请求方式:{} 响应报文:{}",context.getOrderId(),context.getMethod(),res);
            log.error("orderId:{} 提交商户查单异常",context.getOrderId(),ex);
            throw new RetrieableQueryOrderException("提交商户查单异常:"+ex.getMessage());
        }
        return resVerification;
    }
}
