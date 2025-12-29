package com.jdktomcat.showcase.mybatis.plus.strategy.impl;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jdktomcat.showcase.mybatis.plus.component.RedisTableGenerateComponent;
import com.jdktomcat.showcase.mybatis.plus.strategy.IdRetryHandleStrategy;
import com.jdktomcat.showcase.mybatis.plus.util.SpringContextUtil;
import io.netty.util.internal.StringUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.boot.SpringApplication;

import java.lang.reflect.Field;

/**
 * 基于redis自增生成id策略
 */
public class RedisTableRetryHandleStrategy implements IdRetryHandleStrategy {
    /**
     * 主键重新生成策略
     *
     * @param invocation 调用
     */
    @Override
    public void handle(Invocation invocation) throws IllegalAccessException {
        RedisTableGenerateComponent redisTableGenerateComponent = SpringContextUtil.getBean(RedisTableGenerateComponent.class);
        Object[] args = invocation.getArgs();
        Object entity = args[1];
        // 获取类型
        Class<?> clazz = entity.getClass();
        TableName tableNameAnno = clazz.getAnnotation(TableName.class);
        if(tableNameAnno == null){
            return;
        }
        String tableName = tableNameAnno.value();
        if(StringUtil.isNullOrEmpty(tableName)){
            return;
        }
        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 检查是否包含注解:确认主键
            if (field.isAnnotationPresent(TableId.class)) {
                // 如果字段是私有的，需要设置可访问
                field.setAccessible(true);
                // 将字段置空
                field.set(entity, redisTableGenerateComponent.tableIdGenerate(tableName));
                // 返回
                return;
            }
        }


    }
}
