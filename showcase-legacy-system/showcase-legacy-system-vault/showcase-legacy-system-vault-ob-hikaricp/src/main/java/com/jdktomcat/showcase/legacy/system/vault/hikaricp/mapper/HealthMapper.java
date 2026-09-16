package com.jdktomcat.showcase.legacy.system.vault.hikaricp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * health check mapper
 */
@Mapper
public interface HealthMapper {

    @Select("select count(0) from t_bankcard_agent")
    int ping();

    @Select("select current_user()")
    String currentUser();
}
