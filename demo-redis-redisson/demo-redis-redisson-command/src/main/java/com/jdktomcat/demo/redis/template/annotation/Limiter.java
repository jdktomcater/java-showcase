package com.jdktomcat.demo.redis.template.annotation;


import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义注解限流
 *
 * @author jstimmy
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Limiter {

    /**
     * 限流值
     *
     * @return 值
     */
    int value() default 5;

    /**
     * 时间单位
     *
     * @return 时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 时间窗口
     *
     * @return 时间窗口
     */
    int window() default 10;
}
