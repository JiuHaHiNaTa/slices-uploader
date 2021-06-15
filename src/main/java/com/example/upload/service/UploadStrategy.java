package com.example.upload.service;

import com.example.upload.common.response.RetResult;
import com.example.upload.dto.FileUploadRequestDTO;

/**
 * @author Jiuha
 */
public interface UploadStrategy {

    /**
     * 文件上传
     * @param param 文件上传请求
     * @return 文件上传保存信息
     */
    RetResult<Object> uploadFile(FileUploadRequestDTO param);
}
