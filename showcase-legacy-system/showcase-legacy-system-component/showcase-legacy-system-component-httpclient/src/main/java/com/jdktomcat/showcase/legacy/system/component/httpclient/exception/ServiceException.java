package com.jdktomcat.showcase.legacy.system.component.httpclient.exception;


import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.ResultStatus;

import java.util.Objects;

public class ServiceException extends RuntimeException {

    private String code;
    //平台订单号
    private String data;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message, Throwable ex) {
        super(message, ex);
    }

    public ServiceException(String code, String message, Throwable ex) {
        super(message, ex);
        this.code = code;
    }
    public ServiceException(String message, ResultStatus serviceErrCode, String data) {
        super(message);
        this.data = data;
        if (Objects.nonNull(serviceErrCode)) {
            this.code = serviceErrCode.getCode();
        }
    }

    public String getCode() {
        return code;
    }

    public String getData() {
        return data;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}