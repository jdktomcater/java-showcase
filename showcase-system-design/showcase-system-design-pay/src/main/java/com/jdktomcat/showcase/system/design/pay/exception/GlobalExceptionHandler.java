package com.jdktomcat.showcase.system.design.pay.exception;

import com.jdktomcat.showcase.system.design.pay.response.Response;
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
    public Response<String> handleException(Exception e) {
        return Response.fail(e.getMessage());
    }
}
