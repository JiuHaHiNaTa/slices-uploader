package com.example.upload.common.response;

import org.springframework.http.HttpStatus;

/**
 * 返回工厂类
 *
 * @author Jiuha
 */
public class RetResponse<T> {

    public static <T> RetResult<T> makeResponse(int code, String message, T data) {
        return new RetResult<>(code, message, data);
    }

    public static <T> RetResult<T> makeResponse(HttpStatus code, String message, T data) {
        return new RetResult<>(code.value(), message, data);
    }
}
