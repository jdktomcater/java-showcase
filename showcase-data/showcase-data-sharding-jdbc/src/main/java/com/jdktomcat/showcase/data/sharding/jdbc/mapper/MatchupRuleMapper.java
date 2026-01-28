
package com.jdktomcat.showcase.data.sharding.jdbc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.jdktomcat.showcase.data.sharding.jdbc.entry.MatchupRule;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


/**
 * 数据库访问接口
 */
@Repository
public interface MatchupRuleMapper extends BaseMapper<MatchupRule> {

    /**
     * 根据主键更新
     *
     * @param matchupRule 撮合规则
     * @return 更新结果
     */
    int updateById(@Param(Constants.ENTITY) MatchupRule matchupRule);
}
