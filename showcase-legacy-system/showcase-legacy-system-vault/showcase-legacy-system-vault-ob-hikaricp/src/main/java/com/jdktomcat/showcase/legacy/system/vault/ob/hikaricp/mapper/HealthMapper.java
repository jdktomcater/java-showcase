package com.jdktomcat.showcase.legacy.system.vault.ob.hikaricp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * health check mapper
 */
@Mapper
public interface HealthMapper {

    @Select("select count(0) from t_merchant_agent_info")
    int ping();

    @Select("select current_user()")
    String currentUser();
}
