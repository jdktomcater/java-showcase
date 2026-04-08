package com.jdktomcat.showcase.legacy.system.component.httpclient.exception;

/**
 * 抛出一个可重试查单异常：捕获到该异常，继续发起查单请求
 */
public class RetrieableQueryOrderException extends RuntimeException{

    public RetrieableQueryOrderException() {
        super();
    }

    public RetrieableQueryOrderException(String message) {
        super(message);
    }

    public RetrieableQueryOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
