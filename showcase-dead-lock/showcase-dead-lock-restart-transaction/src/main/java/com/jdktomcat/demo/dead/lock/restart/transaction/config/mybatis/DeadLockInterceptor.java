package com.jdktomcat.demo.dead.lock.restart.transaction.config.mybatis;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;

/**
 * mybatis死锁拦截器
 */
@Intercepts({
        //type指定代理的是那个对象，method指定代理Executor中的那个方法,args指定Executor中的query方法都有哪些参数对象
        //由于Executor中有两个query，因此需要两个@Signature
        //需要代理的对象和方法
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
})
@Slf4j
public class DeadLockInterceptor extends MybatisPlusInterceptor {

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        try{
            return invocation.proceed();
        }catch (InvocationTargetException exception){
            log.error("数据库更新异常！", exception);
            return restartTransaction(invocation, exception);
        }
    }


    private Object restartTransaction(Invocation invocation, InvocationTargetException exception) throws InvocationTargetException {
        Throwable throwable = exception.getTargetException();
        if(throwable instanceof MySQLTransactionRollbackException){
            MySQLTransactionRollbackException transactionRollbackException = (MySQLTransactionRollbackException)throwable;
            if(transactionRollbackException.getSQLState().equalsIgnoreCase("40001")){
                // 死锁场景处理
                log.info("即将回滚事务：{}", TransactionSynchronizationManager.getCurrentTransactionName());
                Map<Object, Object>  resourceMap = TransactionSynchronizationManager.getResourceMap();
                log.info("即将回滚事务中的资源信息：{}", JSONObject.toJSONString(resourceMap));
            }
        }
        throw exception;
    }
}
