package com.jdktomcat.showcase.data.driver.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class IndexVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal succeedpayamount;
    private Long succeedcount;
    private Long failcount;
    private Long totalcount;
    private Long supplierbranchid;
    private Date querytime;
    private Date queryorderstatus;
}
