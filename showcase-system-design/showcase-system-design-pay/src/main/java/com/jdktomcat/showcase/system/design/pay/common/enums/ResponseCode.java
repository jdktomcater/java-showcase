
package com.jdktomcat.showcase.system.design.pay.common.enums;

import lombok.Getter;

/**
 * response code enum
 */
@Getter
public enum ResponseCode {
    
    /**
     * success
     */
    SUCCESS(200, "success"),

    /**
     * fail
     */
    FAIL(3000, "fail");

    /**
     * status
     */
    private final int status;

    /**
     * description message
     */
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
