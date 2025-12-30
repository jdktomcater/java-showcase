package com.jdktomcat.showcase.mybatis.plus.interceptor;

import com.jdktomcat.showcase.mybatis.plus.config.RetryConfig;
import com.jdktomcat.showcase.mybatis.plus.entry.ChannelAccountRecord;
import com.jdktomcat.showcase.mybatis.plus.enums.RetryHandlerEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@Component
@Intercepts(value = {@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),})
public class RetryInsertInterceptor implements Interceptor {

    /**
     * 重试配置
     */
    @Autowired
    private RetryConfig retryConfig;

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        String sqlCommandType = ms.getSqlCommandType().toString();
        // 只处理插入SQL语句动作
        if (ms.getSqlCommandType() != SqlCommandType.INSERT) {
            return invocation.proceed();
        }
        Object result;
        try {
            result = invocation.proceed();
            log.info("RetryInsertInterceptor sqlCommandType:{} result:{}", sqlCommandType, result);
        } catch (InvocationTargetException exception) {
            log.error("MySqlInterceptor 拦截器拦截过程中出现异常！", exception);
            if(isRetry(invocation, exception)){
                result = invocation.proceed();
                log.info("RetryInsertInterceptor retry sqlCommandType:{} result:{}", sqlCommandType, result);
            }else{
                throw exception;
            }
        }
        return result;
    }

    /**
     * 判断是否需要重试
     *
     * @param invocation 调用链
     * @return true 需要重试 false 不需要重试
     */
    private boolean isRetry(Invocation invocation,InvocationTargetException exception) {
        try{
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            String sqlId = ms.getId();
            // 判断异常类型
            Throwable throwable = exception.getCause();
            // 异常类型
            if(!(throwable instanceof SQLIntegrityConstraintViolationException sqlException)){
                return false;
            }
            // sql状态码校验
            if(!sqlException.getSQLState().equalsIgnoreCase("2300")){
                return false;
            }
            // SQL错误码校验
            if(sqlException.getErrorCode()!=1062){
                return false;
            }
            // 错误详情
            if(!(sqlException.getMessage()!=null && sqlException.getMessage().contains("PRIMARY"))){
                return false;
            }
            // 是否配置
            if(!retryConfig.getConfig().containsKey(sqlId)){
                return false;
            }
            // 配置类型
            Integer handleType =  retryConfig.getConfig().get(sqlId);
            RetryHandlerEnum retryHandlerEnum = RetryHandlerEnum.getInstance(handleType);
            if(retryHandlerEnum==null){
                return false;
            }
            retryHandlerEnum.getIdRetryHandleStrategy().handle(invocation);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}