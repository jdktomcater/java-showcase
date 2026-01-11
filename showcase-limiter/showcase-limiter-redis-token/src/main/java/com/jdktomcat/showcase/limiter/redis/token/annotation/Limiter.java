package com.jdktomcat.showcase.limiter.redis.token.annotation;


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
     * token permit count
     *
     * @return count
     */
    int token() default 5;

    /**
     * lease time
     *
     * @return lease time
     */
    int lease() default 3;
}
