package com.jdktomcat.showcase.legacy.system.component.httpclient.constant;

/**
 * 三方IP白名单默认类型和状态
 * @author marlon
 */
public enum NetworkTypeEnum {
    内网("0", "内网"),
    外网("1", "外网");
    private String networkTypeId;
    private String networkTypeName;
    private NetworkTypeEnum(String networkTypeId, String networkTypeName) {
        this.networkTypeId = networkTypeId;
        this.networkTypeName = networkTypeName;
    }

    public String getNetworkTypeId() {
        return networkTypeId;
    }

    public void setNetworkTypeId(String networkTypeId) {
        this.networkTypeId = networkTypeId;
    }

    public String getNetworkTypeName() {
        return networkTypeName;
    }

    public void setNetworkTypeName(String networkTypeName) {
        this.networkTypeName = networkTypeName;
    }
}
