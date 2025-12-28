package com.jdktomcat.showcase.mybatis.plus.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
@Intercepts(value = {@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),})
@Slf4j
public class RetryInsertInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        //Executor.class  sql 获取方法
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];

        //sql语句类型 select、delete、insert、update
        String sqlCommandType = ms.getSqlCommandType().toString();
        // Executor only exposes "update" for INSERT/UPDATE/DELETE; restrict this interceptor to INSERT only.
        if (ms.getSqlCommandType() != SqlCommandType.INSERT) {
            return invocation.proceed();
        }
        Object result = null;
        try {
            result = invocation.proceed();
            log.info("RetryInsertInterceptor sqlCommandType:{} result:{}", sqlCommandType, result);
        } catch (InvocationTargetException exception) {
            log.error("MySqlInterceptor 拦截器拦截过程中出现异常！", exception);
            throw exception;
        }
        return result;
    }
}