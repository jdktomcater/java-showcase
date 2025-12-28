package com.jdktomcat.showcase.mybatis.plus.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

@Component
@Intercepts(value = {@Signature(type = Executor.class, method = "insert", args = {MappedStatement.class, Object.class}),})
@Slf4j
public class RetryInsertInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //Executor.class  sql 获取方法
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        //id为执行的mapper方法的全路径名，如com.uv.dao.UserMapper.insertUser
        //sql语句类型 select、delete、insert、update
        String sqlCommandType = ms.getSqlCommandType().toString();
        Object result = null;
        try {
            result = invocation.proceed();
            log.info("RetryInsertInterceptor result:{}", result);
        } catch (Exception e) {
            log.error("MySqlInterceptor 拦截器拦截过程中出现异常！", e);
        }
        return result;
    }
}