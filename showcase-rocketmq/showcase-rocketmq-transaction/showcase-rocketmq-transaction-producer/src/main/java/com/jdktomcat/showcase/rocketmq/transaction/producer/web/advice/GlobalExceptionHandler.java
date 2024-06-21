package com.jdktomcat.showcase.rocketmq.transaction.producer.web.advice;

import com.jdktomcat.showcase.rocketmq.transaction.producer.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author js-oswald
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("服务器错误，请联系管理员：" + e.getMessage(), e);
        return Result.failed("服务器错误，请联系管理员");
    }
}

