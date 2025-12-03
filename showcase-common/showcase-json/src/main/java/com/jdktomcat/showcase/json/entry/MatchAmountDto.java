
package com.jdktomcat.showcase.json.entry;

import lombok.Data;


/**
 * 测试实体类
 */
@Data
public class MatchAmountDto {

    /**
     * 商户编码
     */
    private String merchantID;

    /**
     * 固定金额集合
     */
    private String matchAmountList;

    /**
     * 新固定金额集合
     */
    private String matchAmountListNew;
}
