package com.jdktomcat.showcase.mq.rocketmq.transaction.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.model.WithdrawOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WithdrawOrderMapper extends BaseMapper<WithdrawOrder> {

}
