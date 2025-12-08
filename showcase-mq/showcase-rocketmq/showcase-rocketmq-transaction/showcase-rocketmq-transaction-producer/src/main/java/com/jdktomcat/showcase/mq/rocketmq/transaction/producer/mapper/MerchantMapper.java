package com.jdktomcat.showcase.mq.rocketmq.transaction.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.model.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    @Update("update t_merchant set available = (available - #{amount}),frozen = (frozen + #{amount}) where id = #{merchantId} and available >= #{amount} and available >= #{amount};")
    int subtract(@Param("merchantId") Integer merchantId, @Param("amount") BigDecimal amount);
}