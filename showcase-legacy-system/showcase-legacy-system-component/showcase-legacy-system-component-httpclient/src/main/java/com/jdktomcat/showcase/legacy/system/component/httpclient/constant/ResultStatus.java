package com.jdktomcat.showcase.legacy.system.component.httpclient.constant;

/**
 * 返回状态
 *
 * @author Tracy
 * @version 1.0.0
 * @Description
 * @date 2019年01月30日
 */
public enum ResultStatus {
    /**
     *
     */
    ERROR("0", "失败"),

    API_SYSTEM_ERROR("500", "系统错误"),

    API_THIRD_SERVICE_NOT_AVALIABLE("606", "系统三方服务未开启"),

    THIRD_PAY_CODE_ERROR("607", "三方渠道异常"),

    THIRD_DEPOSIT_ORDER_NOT_EXISTS("50006", "三方存款订单不存在"),

    FAIL_WITHDRAW("101", "提款失败"),

    THIRD_INTERNET_ERROR("50007", "通讯异常"),

    QUERY_ORDER_SUCCESS("0000", "订单已经成功，无需再次查询");

    private String code;

    private String message;

    ResultStatus(String code, String msg) {
        this.message = msg;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getDesc(String value) {
        ResultStatus[] businessModeEnums = values();
        for (ResultStatus businessModeEnum : businessModeEnums) {
            if (businessModeEnum.code.equals(value)) {
                return businessModeEnum.message;
            }
        }
        return "";
    }
}
