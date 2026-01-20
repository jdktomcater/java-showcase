package com.jdktomcat.showcase.data.mybatis.plus.strategy.impl;

import com.baomidou.mybatisplus.annotation.TableId;
import com.jdktomcat.showcase.data.mybatis.plus.strategy.IdRetryHandleStrategy;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Field;

/**
 * 默认Id重试处理策略：将主键置空，重新基于mybatis雪花算法生成
 */
public class DefaultIdRetryHandleStrategy implements IdRetryHandleStrategy {

    /**
     * 主键重新生成策略
     *
     * @param invocation 调用
     */
    @Override
    public void handle(Invocation invocation) throws IllegalAccessException {
        Object[] args = invocation.getArgs();
        Object entity = args[1];
        // 获取类型
        Class<?> clazz = entity.getClass();
        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 检查是否包含注解:确认主键
            if (field.isAnnotationPresent(TableId.class)) {
                // 如果字段是私有的，需要设置可访问
                field.setAccessible(true);
                // 将字段置空
                field.set(entity, null);
                // 返回
                return;
            }
        }
    }
}
