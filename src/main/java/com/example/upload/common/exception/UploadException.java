package com.example.upload.common.exception;

/**
 * 上传异常
 *
 * @author Jiuha
 */
public class UploadException extends RuntimeException {

    public UploadException() {
    }

    public UploadException(String message) {
        super(message);
    }
}
