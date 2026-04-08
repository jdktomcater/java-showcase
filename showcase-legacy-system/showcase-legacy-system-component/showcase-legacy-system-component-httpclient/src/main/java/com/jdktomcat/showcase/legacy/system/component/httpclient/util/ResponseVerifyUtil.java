package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.OrderMapConstants;
import com.jdktomcat.showcase.legacy.system.component.httpclient.context.ThirdCallParams;
import com.jdktomcat.showcase.legacy.system.component.httpclient.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 三方请求响应报文校验工具类
 */
public class ResponseVerifyUtil {
    /**
     * 校验查单结果，结果为成功，则返回true，否则返回false
     * @param res
     * @return
     */
    public static int  verify(Map<String, Object> res, List<ThirdCallParams> callParams){
        int resResult=0;
        boolean  hasResCode=false;
        for(ThirdCallParams param:callParams){
            if(OrderMapConstants.REQUEST_RES_CODE.equals(param.getParamEnName())){
                hasResCode=true;
                String  paramsValue = ExpressionExtractValueUtil.execute(param.getParamEnName(),param.getParamValueExp(), res);
                String exp =","+paramsValue+",";
                if(StringUtils.isEmpty(param.getSuccessResValue())){
                    throw  new ServiceException("0","下单响应码未配置成功参数值,未发送dispatch通知");
                }
                String succesResValue=","+param.getSuccessResValue()+",";
                String failResValue=","+param.getFailResValue()+",";
                if (succesResValue.contains(exp)) {
                    //2.三方返回的状态码和配置的成功状态码一致,表示成功
                    resResult= 1;
                    break;
                } else if (StringUtils.isNotEmpty(param.getFailResValue()) && failResValue.contains(exp)) {
                    //2.三方返回的状态码和配置的成功状态码一致,表示失败
                    resResult= -1;
                    break;
                }
            }
        }
        if (!hasResCode) {
            throw  new ServiceException("0","下单响应码:res_code未配置");
        }
        return resResult;
    }
}
