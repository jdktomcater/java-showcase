
package com.jdktomcat.showcase.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.jdktomcat.showcase.mybatis.plus.entry.DepositOrderLog;
import com.jdktomcat.showcase.mybatis.plus.entry.MatchupRule;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


/**
 * 数据库访问接口
 */
@Repository
public interface DepositOrderLogMapper extends BaseMapper<DepositOrderLog> {

}
