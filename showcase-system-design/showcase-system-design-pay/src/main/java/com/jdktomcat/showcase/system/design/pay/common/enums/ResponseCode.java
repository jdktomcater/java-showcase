
package com.jdktomcat.showcase.system.design.pay.common.enums;

/**
 * response code enum
 */
public enum ResponseCode {
    
    /**
     * success
     */
    SUCCESS(200, "success"),

    /**
     * fail
     */
    FAIL(3000, "fail");


    private final int status;
    
    private final String message;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }



}
