package com.jdktomcat.showcase.legacy.system.component.httpclient.context;

import lombok.Data;

@Data
public class QueryResult {
    /**
     * 三方订单状态
     */
    private String status;
    /**
     * 结果报文
     */
    private String msg;

}
