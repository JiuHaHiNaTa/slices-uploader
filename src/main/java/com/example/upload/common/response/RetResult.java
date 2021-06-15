package com.example.upload.common.response;

import lombok.Data;

/**
 * 自定义返回类型
 *
 * @param <T>
 * @author Jiuha
 */
@Data
public class RetResult<T> {

    private int code;

    private String message;

    private T data;

    public RetResult(int code) {
        this(code, null, null);
    }

    public RetResult(int code, String message) {
        this(code, message, null);
    }

    public RetResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
