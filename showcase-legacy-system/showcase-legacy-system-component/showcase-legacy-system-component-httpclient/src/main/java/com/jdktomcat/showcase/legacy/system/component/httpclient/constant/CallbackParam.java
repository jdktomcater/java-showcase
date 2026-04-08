package com.jdktomcat.showcase.legacy.system.component.httpclient.constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 反查参数
 * @author marlon
 * @version 1.0.0
 * @Description
 * @date 2022-12-07
 */
@AllArgsConstructor
@Getter
public enum CallbackParam {
    //反查配置-参数key,反查url参数名称
    参数NAME(FastConstants.CALLBACK_KEY,"withdrawQueryUrl"),
    //反查配置-参数value,反查url接口地址
    参数VALUE(FastConstants.CALLBACK_VALUE,"/thirdwithdraw/withdraw/verify");

    private String code;
    private String value;

    private static final Map<String, String> MAP;

    static {
        CallbackParam[] values = CallbackParam.values();
        MAP = new LinkedHashMap<>(values.length);
        for (CallbackParam type : values) {
            MAP.put(type.getCode(), type.getValue());
        }
    }

    public static Map<String, String> getMapInfo() {
        return MAP;
    }

    public static String getValue(Integer code) {
        String name = code == null ? null : MAP.get(code);
        return name == null ? "-" : name;
    }
}
