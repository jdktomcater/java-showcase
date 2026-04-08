package com.jdktomcat.showcase.legacy.system.component.httpclient.context;

import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.CallbackParam;
import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.FastConstants;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 上下文基类
 */
@Data
public class FastContext {
    /**
     * 工作模式
     */
    private int  workType;
    /**
     * 三方商户编码
     */
    private String merchantCode;
    /**
     * 渠道ID
     */
    private int payChannelId;
    /**
     * 三方商户渠道号
     */
    private String channelMerchantId;
    /**
     * 三方订单id
     */
    private String orderId;
    /**
     * 订单状态
     */
    private  int orderStatus;
    /**
     * 订单支付方式
     */
    private int payType;
    /**
     * 三方订单数据对象
     */
    private Map<String,Object> orderMap;
    /**
     * 三方通道配置数据
     */
    private ThirdChannelDTO thirdChannelDTO;
    /**
     * 下单:入参配置
     */
    private List<ThirdCallParams> callInParams;
    /**
     * 下单:出参配置
     */
    private List<ThirdCallParams> callOutParams;



    /**
     * 签名参数key是否参与签名：1参与签名，0不参与签名， 2：同0，但密钥key参与签名   默认为1参与签名
     */
    private String  signParamKeySwitch="1";
    /**
     * 验签字符大小写（up/lower）不转换
     */
    private String  signCharType;
    /**
     * 验签的sign的key
     */
    private String  signKey="sign";

    /**
     * 下单请求报文加密处理类
     */
    private String  signHandlerClass="";
    /**
     * 下单响应报文加密处理类
     */
    private String  resonseHandlerClass="";
    /**
     * 验签的sign的算法:默认:SHA256WithRSA
     */
    private String  signRsaAlgorithms= FastConstants.SIGN_RSA_ALGORITHMS;

    /**
     * 订单加密key名称
     */
    private String privateKeyName;

    /**
     * 请求方式: post/get
     */
    private String  method="post";
    /**
     * 下单请求报文
     */
    private TreeMap<String, Object> reqParam;
    /**
     * 下单请求头信息
     */
    private Map<String,Object> reqHeaderParam;

    /**
     * 下单响应报文
     */
    private Map<String, Object> response;
    /**
     * 天下支付通知及反查域名
     */
    private List<String> payUrl;

    /**
     * 私钥加密
     */
    private String  privateKey="";
    /**
     * 公钥解密
     */
    private String  publicKey="";

    private String res;

    /**
     * 请求加密KEY类型 1公钥 2私钥
     */
    private String reqKeyType;

    /**
     * 反查参数名称
     */
    private String callBackParamKey = CallbackParam.参数NAME.getValue();

    /**
     反查参数发送开启关闭 true开启 false关闭
     */
    public Boolean callbackUrlOnOff = true;

    /**
     * 内外网标识 network_type 0内网 1外网
     */
    private String networkType;

    /**
     * 当前的金额类型
     */
    private String amountType;

    /**
     * 拆单补贴金额
     */
    private String splitSubsidyAmount;

    /**
     * 提款人银行预留手机号
     */
    private String bank_mobile;

    /**
     * 提款人身份证号
     */
    private String id_num;
}
