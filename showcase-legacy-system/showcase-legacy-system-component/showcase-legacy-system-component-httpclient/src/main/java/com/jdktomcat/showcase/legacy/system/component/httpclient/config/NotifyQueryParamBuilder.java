package com.jdktomcat.showcase.legacy.system.component.httpclient.config;

import com.jdktomcat.showcase.legacy.system.component.httpclient.context.NotifyContext;
import com.jdktomcat.showcase.legacy.system.component.httpclient.exception.ServiceException;
import com.jdktomcat.showcase.legacy.system.component.httpclient.util.ContextParamsBuildUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class NotifyQueryParamBuilder {
    @Resource
    private NotifyChannelCall channelCall;
    /**
     * 2.组装收到商户通知后，查单的请求报文
     * @return
     */
    public NotifyChannelCall buildQueryParams(NotifyContext context){
        if(StringUtils.isEmpty(context.getQueryUrl())){
            log.error("orderId:{} 查单地址未配置,三方商户编码:{}",context.getOrderId(),context.getMerchantCode());
            throw  new ServiceException("查单地址未配置:"+context.getMerchantCode());
        }
        //构建订单入参上下文
        ContextParamsBuildUtil.buildInParamsContext(context,context.getQueryInParams());
        return channelCall;
    }
}
