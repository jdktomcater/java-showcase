
package com.jdktomcat.showcase.data.sharding.jdbc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jdktomcat.showcase.data.sharding.jdbc.entry.DepositOrderLog;
import org.springframework.stereotype.Repository;


/**
 * 数据库访问接口
 */
@Repository
public interface DepositOrderLogMapper extends BaseMapper<DepositOrderLog> {

}
