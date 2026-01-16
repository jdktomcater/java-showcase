package com.jdktomcat.showcase.system.design.pay.exception;

import com.jdktomcat.showcase.system.design.pay.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * system error
     */
    @ExceptionHandler(Exception.class)
    public Response<String> handleException(Exception e) {
        log.error("occur exception:{}", e.getMessage(), e);
        return Response.fail(e.getMessage());
    }
}
