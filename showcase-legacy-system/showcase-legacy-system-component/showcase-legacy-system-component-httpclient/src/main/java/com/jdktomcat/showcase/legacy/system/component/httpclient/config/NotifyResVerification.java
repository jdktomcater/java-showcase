package com.jdktomcat.showcase.legacy.system.component.httpclient.config;

import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.OrderMapConstants;
import com.jdktomcat.showcase.legacy.system.component.httpclient.context.NotifyContext;
import com.jdktomcat.showcase.legacy.system.component.httpclient.context.NotifyParamVO;
import com.jdktomcat.showcase.legacy.system.component.httpclient.context.QueryResult;
import com.jdktomcat.showcase.legacy.system.component.httpclient.context.ThirdCallParams;
import com.jdktomcat.showcase.legacy.system.component.httpclient.exception.RetrieableQueryOrderException;
import com.jdktomcat.showcase.legacy.system.component.httpclient.util.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Data
public class NotifyResVerification {

    /**
     * 4.校验下单响应报文，验证成功
     *
     * @return
     */
    public void verifyResponse(NotifyContext context) {
        try {
            List<ThirdCallParams> param = context.getQueryOutParams();
            String strResponse = OrderSignUtil.decryptResponse(context, param, context.getRes());
            Map<String, Object> mRes = JsonUtils.json2map(strResponse);
            int resResult = ResponseVerifyUtil.verify(mRes, param);
            if (resResult != 0) {
                log.info("orderId:{} 三方通知报文校验成功", context.getOrderId());
                NotifyParamVO notifyVO = context.getNotifyParamVO();
                notifyVO.setResultStatus(resResult);
                context.setResponse(mRes);
            } else {
                log.info("orderId:{} 三方通知报文校验未通过:{}", context.getOrderId(), DesensitizationUtil.jsonDesensit(strResponse));
            }
            mapResponse(context, mRes, param);
        } catch (Throwable ex) {
            log.error("orderId:{} 订单校验异常,不发送dispatch通知:{}", context.getOrderId(), ex.getMessage());
            throw new RetrieableQueryOrderException(context.getNotifyParamVO().getData().getMsg());
        }
    }

    /**
     * 校验查单结果，结果为成功，则返回true，否则返回false
     *
     * @param res
     * @return
     */
    private void mapResponse(NotifyContext context, Map<String, Object> res, List<ThirdCallParams> callParams) {
        NotifyParamVO notifyVO = context.getNotifyParamVO();
        QueryResult queryResult = new QueryResult();
        if (notifyVO.getResultStatus() == 1) {
            queryResult.setMsg("订单状态:成功");
        } else if (notifyVO.getResultStatus() == -1) {
            queryResult.setMsg("订单状态:失败");
        } else {
            queryResult.setMsg("订单状态:未明");
        }
        // 强制要求必须要配置实际金额参数,否则不接受通知处理
        boolean isHasPaidAmount = false;
        for (ThirdCallParams param : callParams) {
            String paramsValue = ExpressionExtractValueUtil.execute(param.getParamEnName(), param.getParamValueExp(), res);
            if (StringUtils.isNotEmpty(paramsValue)) {
                if (OrderMapConstants.ORDER_ID.equals(param.getParamEnName())) {
                    notifyVO.setOrderId(paramsValue);
                } else if (OrderMapConstants.PAID_AMOUNT.equals(param.getParamEnName())) {
                    notifyVO.setPaidAmount(paramsValue);
                    isHasPaidAmount = true;
                } else if (OrderMapConstants.VIRTUAL_PAID_AMOUNT.equals(param.getParamEnName())) {
                    if (StringUtils.isNotEmpty(paramsValue)) {
                        notifyVO.setVirtualPaidAmount(paramsValue);
                        isHasPaidAmount = true;
                    }
                } else if (OrderMapConstants.PAID_AMOUNT_DEFAULT.equals(param.getParamEnName()) && "true".equals(paramsValue)) {
                    //设置响应默认值,配置后默认为下单金额
                    notifyVO.setPaidAmount(context.getNotifyParamVO().getAmount());
                    isHasPaidAmount = true;
                    log.info("配置了查单响应默认值paidAmountDefault:{},实际金额为下单金额:{}",paramsValue,context.getNotifyParamVO().getAmount());
                } else if (OrderMapConstants.CHANNEL_ORDER_ID.equals(param.getParamEnName())) {
                    notifyVO.setChannelOrderId(paramsValue);
                } else if (OrderMapConstants.REQUEST_RES_MSG.equals(param.getParamEnName())) {
                    queryResult.setMsg(paramsValue);
                } else if (OrderMapConstants.THIRD_ORDER_STATUS.equals(param.getParamEnName())) {
                    queryResult.setStatus(paramsValue);
                } else if (OrderMapConstants.REAL_PAY_NAME.equals(param.getParamEnName())) {
                    // 设置真实付款人姓名
                    notifyVO.setRealPayName(paramsValue);
                }
            }
        }
        log.info("orderId:{} 校验查单结果，LinkType值：{}", context.getOrderId(),context.getLinkType());
        //用虚拟币金额上分
        if ("1".equals(String.valueOf(context.getLinkType()))) {
            log.info("orderId:{} 校验查单结果2，LinkType值：{}", context.getOrderId(),context.getLinkType());
            for (ThirdCallParams param : callParams) {
                String paramsValue = ExpressionExtractValueUtil.execute(param.getParamEnName(), param.getParamValueExp(), res);
                if (StringUtils.isNotEmpty(paramsValue)) {
                    if ("useNewOtherPaidAmount".equals(param.getParamEnName())) {
                        notifyVO.setPaidAmount(paramsValue);
                        isHasPaidAmount = true;
                        log.info("orderId:{} 校验查单结果，进入虚拟币金额上分LinkType：{}，设置mq值notifyVO：{}", context.getOrderId(),context.getLinkType(),notifyVO.toString());
                    }
                }
            }
        }

        if (context.getWorkType() == 1 && !isHasPaidAmount) {
            log.error("未配置实际下单金额参数,无法处理查单结果,当前查单结果:" + res);
            throw new RuntimeException("未配置实际下单金额参数,无法处理查单结果");
        }
        String paidAmount = StringUtils.isNotEmpty(notifyVO.getVirtualPaidAmount()) ? notifyVO.getVirtualPaidAmount(): notifyVO.getPaidAmount();
        queryResult.setMsg(queryResult.getMsg() + ",实际金额:" + paidAmount);
        notifyVO.setData(queryResult);
        log.info("orderId:{} 校验查单结果，设置mq值：{}", context.getOrderId(),notifyVO.toString());
    }
}
