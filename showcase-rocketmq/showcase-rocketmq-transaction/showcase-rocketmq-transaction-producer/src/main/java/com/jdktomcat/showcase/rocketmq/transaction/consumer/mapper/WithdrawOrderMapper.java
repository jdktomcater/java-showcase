package com.jdktomcat.showcase.rocketmq.transaction.consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.model.WithdrawOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WithdrawOrderMapper extends BaseMapper<WithdrawOrder> {

}
