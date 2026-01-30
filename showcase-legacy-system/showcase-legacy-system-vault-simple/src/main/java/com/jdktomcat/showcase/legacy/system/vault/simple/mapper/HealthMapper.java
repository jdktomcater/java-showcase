package com.jdktomcat.showcase.legacy.system.vault.simple.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * health check mapper
 */
@Mapper
public interface HealthMapper {

    @Select("select 1")
    int ping();
}
