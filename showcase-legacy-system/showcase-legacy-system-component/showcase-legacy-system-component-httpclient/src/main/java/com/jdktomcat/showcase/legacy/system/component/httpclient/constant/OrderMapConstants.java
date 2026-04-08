package com.jdktomcat.showcase.legacy.system.component.httpclient.constant;

/**
 * 订单映射三方参数常量
 */
public class OrderMapConstants {
    /**
     * 参数签名字符类型: up/lower
     */
    public static final String SIGN_CHAR_TYPE = "sign_char_type";
    /**
     * 参数签名KEY
     */
    public static final String SIGN_KEY = "sign_key";

    /**
     * 签名参数key是否参与签名：1参与签名，0不参与签名，默认为1参与签名
     */
    public static final String SIGN_PARAM_KEY_SWITCH = "sign_param_key_switch";
    /**
     * 三方商户下单密钥KEY名称变量
     */
    public static final String ENC_KEY_NAME = "enc_key_name";
    /**
     * 请求响应码
     */
    public static final String REQUEST_RES_CODE = "res_code";
    /**
     * 请求响应结果
     */
    public static final String REQUEST_RES_MSG = "res_msg";
    /**
     * 三方商户下单后充值地址
     */
    public static final String RES_DEPOSIT_URL = "deposit_url";

    /**
     * 请求方式
     */
    public static final String REQUEST_METHOD = "req_method";
    /**
     * 充值页面请求方式
     */
    public static final String DEPOSIT_PAGE_METHOD = "deposit_page_method";
    /**
     * 参数为请求头信息
     */
    public static final String PARAM_PREFIX_HEDAER_CONTENT_TYPE = "header.contentType";
    /**
     * 请求时间戳（精确到秒）
     */
    public static final String REQUEST_TIMESTAMP = "req_timestamp";
    /**
     * 当前时间
     */
    public static final String CUR_TIME = "cur_time";
    /**
     * 渠道余额
     */
    public static final String CHANNEL_BALANCE = "channel_balance";


    /**
     * 订单实际支付金额
     */
    public static final String PAID_AMOUNT = "paidAmount";

    /**
     * 订单实际支付虚拟币金额
     */
    public static final String VIRTUAL_PAID_AMOUNT = "virtualPaidAmount";

    /**
     * 订单实际支付金额-默认下单金额 true:开启 其他:关闭
     */
    public static final String PAID_AMOUNT_DEFAULT = "paidAmountDefault";

    /**
     * 订单ID
     */
    public static final String ORDER_ID = "orderId";
    /**
     * 三方商户订单ID
     */
    public static final String CHANNEL_ORDER_ID = "channelOrderId";
    /**
     * 订单ID
     */
    public static final String THIRD_ORDER_STATUS = "channelOrderStatus";

    /**
     * 我方通知URl的key
     */
    public static final String NOTIFY_URL_KEY = "notify_url_key";

    /**
     * 我方通知URl的key
     */
    public static final String ENCODE_NOTIFY_URL_KEY = "encode_notify_url_key";

    /**
     * 我方通知URl格式：body/param
     */
    public static final String NOTIFY_URL_TYPE = "notify_url_type";

    /**
     * 接收商户通知成功标识符
     */
    public static final String NOTIFY_RECEIVE_SUCCESS = "notify_res_success";

    /**
     * 验签的sign的处理类  key
     */
    public static final String SIGN_HANDLER_CLASS = "sign_handler_class";

    /**
     * 下单响应报文处理类  key
     */
    public static final String RESPONSE_HANDLER_CLASS = "response_handler_class";
    /**
     * 验签的sign的算法:"SHA256WithRSA" / "SHA1WithRSA"
     */
    public static final String SIGN_RSA_ALGORITHMS = "sign_rsa_algorithms";
    /**
     * 真实付款人姓名
     */
    public static final String REAL_PAY_NAME = "real_pay_name";

    /**
     反查配置url名称
     */
    public static final String  CALLBACK_URL ="withdrawQueryUrl";

    /**
     * 我方代付回调通知URl格式：body/param
     * 代付渠道配置化对接时配置了该参数则走回调查单逻辑
     */
    public static final String WITHDRAW_NOTIFY_URL_TYPE = "withdraw_notify_url_type";
}
