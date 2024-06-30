package com.jdktomcat.showcase.redis.redisson.lock.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.cluster.ClusterConnectionManager;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.connection.MasterSlaveEntry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class RedisLogAspect {

    @Resource
    private ClusterConnectionManager connectionManager;

    @Pointcut("execution(* com.jdktomcat.showcase.redis.redisson.lock.component.RedisComponent.*(..))")
    private void pointCut() {
    }

    @Around("pointCut()")
    public Object entry(ProceedingJoinPoint joinPoint) throws Throwable {
        // 方法名
        String method = joinPoint.getSignature().getName();
        // 接口入参
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            String key = args[0].toString();
            int slot = connectionManager.calcSlot(key);
            MasterSlaveEntry masterSlaveEntry = connectionManager.getEntry(slot);
            MasterSlaveServersConfig masterSlaveServersConfig = masterSlaveEntry.getConfig();
            String master = masterSlaveServersConfig.getMasterAddress().toString();
            String slave = Arrays.toString(masterSlaveServersConfig.getSlaveAddresses().toArray());
            log.info("LogAspect method={}, key={}, slot={}, master={}, slave={}", method, key, slot, master, slave);
        }
        // 调用实际接口耗时
        return joinPoint.proceed();
    }

}
