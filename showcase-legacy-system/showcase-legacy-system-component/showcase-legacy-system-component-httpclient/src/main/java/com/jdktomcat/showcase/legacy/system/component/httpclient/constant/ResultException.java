package com.jdktomcat.showcase.legacy.system.component.httpclient.constant;


import org.apache.commons.lang3.StringUtils;

/**
 *  根据异常信息返回描述
 *
 * @author aplication
 * @version 1.0.0
 * @Description
 * @date 2021年02月19日
 */
public enum ResultException {
    /**
     * 常见异常类型枚举
     */
    E_JSON_FAST_EXC("JSONException", "三方商户下单返回格式不正确,解析错误"),

    E_NULLPOINT_EXC("NullPointerException","请求超时，或者响应为空"),

    E_SOCKET_EXC("SocketException","网络连接异常,请重试"),

    E_HTTPHOST_EXC("HttpHostConnectException","网络拒绝连接异常"),

    E_CAST_EXC("ClassCastException","类型转换异常"),

    E_SQL_EXC("SQLException","操作数据库异常"),

    E_INDEXOUt_EXC("IndexOutOfBoundsException","数组下标越界异常"),

    E_NUMBER_FORMAT_EXC("NumberFormatException","字符串转换为数字异常"),

    E_NOSUCH_EXC("NoSuchMethodException","方法不存在异常"),

    E_INSTAN_EXC("InstantiationException","实例化异常"),

    E_FILENOT_EXC("FileNotFoundException","文件未找到异常"),

    E_SERVICE_EXC("ServiceException","系统捕获异常，请联系技术确认"),

    E_ARITHMETIC_EXC("ArithmeticException","数学运算异常");



    private String code;

    private String message;

    ResultException(String code, String msg) {
        this.message = msg;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getExcMsg(Exception excValue) {
        ResultException[] businessModeEnums = values();
        for (ResultException businessModeEnum : businessModeEnums) {
            String code = businessModeEnum.code.toUpperCase();
            String value = excValue.toString().toUpperCase();
            // 捕获系统内定义异常
            final String serviceCode = "ServiceException".toUpperCase();
            if ( serviceCode.equalsIgnoreCase(value) || value.contains(serviceCode) ) {
                String message = excValue.getMessage();
                return StringUtils.isNotEmpty(message)?message:businessModeEnum.message;
            }
            if ( code.equalsIgnoreCase(value) || value.contains(code) ) {
                return businessModeEnum.message;
            }
        }
        return "三方通道下单异常,请联系技术确认!";
    }

}
