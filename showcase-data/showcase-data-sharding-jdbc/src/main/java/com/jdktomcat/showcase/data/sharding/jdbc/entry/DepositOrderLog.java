package com.jdktomcat.showcase.data.sharding.jdbc.entry;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "deposit_order_log", autoResultMap = true)
public class DepositOrderLog {

    @TableId
    private Long id;

    private String platformOrderId;

    private String orderId;

    private Date actionTime;

    private Long score;

    private String resume;

    private Integer poolType;

    private Integer matchResult;
}
