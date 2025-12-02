package com.jdktomcat.showcase.mybatis.plus.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;


/**
 * 订单撮合规则对象 mp_matchup_rule
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "mp_matchup_rule", autoResultMap = true)
public class MatchupRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 1启用，0禁用
     */
    private Long status;

    /**
     * 规则值
     */
    private String ruleValue;

    /**
     * 支付方式:商户编码集合；比如：1:10006_10007
     */
    private String ruleEnName;

    /**
     * 支付方式
     */
    private Long payTypeCode;

    /**
     * 规则名称
     */
    private String ruleCnName;

    /**
     * 撮合url
     */
    private String matchUrl;

    /**
     * 零钱池地址
     */
    private Long smallPool;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long ruleKey;

    /**
     * 撮合池规则集合:[{"amount":100,"pool_max":100,"pool_high":0.7,"pool_low":0.2},{"amount":200,"pool_max":60,"pool_high":0.7,"pool_low":0.2}]
     */
    private String matchPoolRules;

    private String matchPoolRulesNew;

    /**
     * 不匹配的层级配置
     */
    private String notMatchLevels;

    /**
     * 撮合模式:大于100为混合商户撮合模式
     */
    private Integer ruleOrderNo;
    /**
     * 0 不拆单  1 拆单
     */
    private Integer splitSwitch;
    /**
     * 提款最大拆分数量（包括超过的转自出的订单），默认最大3笔
     */
    private Integer maxSplitCount;

    /**
     * 提款最大拆分数量（包括超过的转自出的订单），默认最大3笔  外部极速
     */
    private Integer outMaxSplitCount;


    /**
     * 1:金额水位撮合池固化(需要手动调整)，默认为：0
     */
    private Integer matchPoolType;

    /**
     * 步长
     */
    private String stepSizeRules;

    private Integer ruleType;
}
