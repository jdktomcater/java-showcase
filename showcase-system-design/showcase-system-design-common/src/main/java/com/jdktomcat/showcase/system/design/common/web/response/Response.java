
package com.jdktomcat.showcase.system.design.common.web.response;

import com.jdktomcat.showcase.system.design.common.enums.ResponseCode;
import lombok.Data;

/**
 * response
 */
@Data
public class Response<T> {
    
    /**
     * code
     */
    private int status;
    
    /**
     * msg
     */
    private String msg;

    /**
     * data
     */
    private T data;

    /**
     * success
     * 
     * @return response
     */
    public static Response<Object> success() {
        Response<Object> response = new Response<>();
        response.setStatus(ResponseCode.SUCCESS.getStatus());
        response.setMsg(ResponseCode.SUCCESS.getMessage());
        return response;
    }
    
    /**
     * success with data
     *
     * @param data target data
     * @return response
     */
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setStatus(ResponseCode.SUCCESS.getStatus());
        response.setMsg(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    /**
     * fail
     *
     * @return response
     */
    public static Response<Object> fail() {
        Response<Object> response = new Response<>();
        response.setStatus(ResponseCode.FAIL.getStatus());
        response.setMsg(ResponseCode.FAIL.getMessage());
        return response;
    }
    /**
     * fail with data
     *
     * @param data data
     * @return response
     */
    public static <T> Response<T> fail(T data) {
        Response<T> response = new Response<>();
        response.setStatus(ResponseCode.FAIL.getStatus());
        response.setMsg(ResponseCode.FAIL.getMessage());
        response.setData(data);
        return response;
    }

}
