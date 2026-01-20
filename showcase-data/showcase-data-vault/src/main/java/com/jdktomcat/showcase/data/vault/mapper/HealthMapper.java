package com.jdktomcat.showcase.data.vault.mapper;

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
