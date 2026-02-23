package com.jdktomcat.showcase.legacy.system.vault.verify.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * health check mapper
 */
@Mapper
public interface HealthMapper {

    @Select("select count(0) from pay.agent_info")
    int ping();

    @Select("select current_user()")
    String currentUser();

    @Update("UPDATE pay.rule_operate_log SET rule_name = #{ruleName} WHERE id = #{id}")
    int update(@Param("id") Long id, @Param("ruleName") String ruleName);
}
