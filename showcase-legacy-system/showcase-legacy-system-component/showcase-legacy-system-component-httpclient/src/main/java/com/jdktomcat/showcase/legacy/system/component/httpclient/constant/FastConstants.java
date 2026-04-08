package com.jdktomcat.showcase.legacy.system.component.httpclient.constant;

public class FastConstants {

    /**
     * 三方商户支付地址请求方式：post
     */
    public static final String  METHOD_POST="post";
    /**
     * 三方商户支付地址请求方式：get
     */
    public static final String  METHOD_GET="get";

    /**
     * 字符大写 up
     */
    public static final String  CHAR_UP="up";
    /**
     * 字符小写 lower
     */
    public static final String  CHAR_LOWER="lower";

    /**
     * 我方通知URl格式：param_map
     */
    public static final String NOTIFY_URL_TYPE_PARAM ="param";
    /**
     * 我方通知URl格式：param_map
     */
    public static final String NOTIFY_URL_TYPE_PARAM_MAP ="param_map";
    /**
     * 我方通知URl格式：param_map_get
     */
    public static final String NOTIFY_URL_TYPE_PARAM_MAP_GET ="param_map_get";
    /**
     * 我方通知URl格式：body_map
     */
    public static final String NOTIFY_URL_TYPE_BODY ="body";
    /**
     * 我方通知URl格式：body_map
     */
    public static final String NOTIFY_URL_TYPE_BODY_MAP ="body_map";
    /**
     * 我方通知URl格式：body_byte
     */
    public static final String NOTIFY_URL_TYPE_BODY_BYTE ="body_byte";
    /**
     * 参数为请求头信息
     */
    public static final String   PARAM_PREFIX_HEDAER="header.";

     /**
      * 调度请求三方的订单ID  key
      */
     public static final String DISPATCH_ORDER_ID_KEY ="orderid";

    /**
     *  签名的所有参数key都不参与签名
     */
    public static final String SIGN_PARAM_KEY_OFF ="0";

    /**
     *  签名的所有参数key都不参与签名，但是密钥key需要参与签名
     */
    public static final String SIGN_PARAM_KEY_OFF_HK ="2";

    /**
     * 验签的sign的处理包名
     */
    public static final String SIGN_HANDLER_CLASS_PREFIX ="com.pay.typay.third.fast.sign.imp.";
    /**
     * 验签的sign的算法:"SHA256WithRSA" / "SHA1WithRSA"
     */
    public static final String SIGN_RSA_ALGORITHMS ="SHA256WithRSA";

    /**
     * 支付的通知url的默认:key
     */
    public static final String  NOTIFY_URL_KEY="notify_url";

    //提款反查key前缀
    public static  final String RKEY = "WITHDRAW_ORDER_";

    /**
     * 请求加密KEY类型 1公钥 2私钥
     */
    public static final String  REQ_KEY_TYPE ="request_key_type";

    /**
     * 反查配置token名称
     */
    public static final String  CALLBACK_TOKEN ="callToken";

    /**
     反查配置url名称
     */
    public static final String  CALLBACK_URL ="withdrawQueryUrl";

    /**
     反查配置key名称
     */
    public static final String  CALLBACK_KEY ="callback_key";

    /**
     反查配置key值
     */
    public static final String  CALLBACK_VALUE ="callback_value";
    /**
     反查参数发送开启关闭 true开启 false关闭
     */
    public static final String  CALLBACK_URL_ON_OFF_KEY ="callback_url_on_off";

    /**
     * 内外网标识 network_type 0内网 1外网
     */
    public static final String  network_type="network_type";
}