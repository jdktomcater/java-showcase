package com.jdktomcat.showcase.mq.rocketmq.transaction.consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jdktomcat.showcase.mq.rocketmq.transaction.consumer.model.MacRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MacRecordMapper extends BaseMapper<MacRecord> {

}
