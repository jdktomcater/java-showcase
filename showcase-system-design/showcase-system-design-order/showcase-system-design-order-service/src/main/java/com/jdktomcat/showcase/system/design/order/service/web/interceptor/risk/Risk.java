package com.jdktomcat.showcase.system.design.order.service.web.interceptor.risk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 风险标记，打上此注解的处理器方法将触发风控检查
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Risk {
    /**
     * 风险等级
     */
    CheckLevel level() default CheckLevel.HIGH;
}
