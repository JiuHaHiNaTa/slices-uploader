package com.example.upload.controller;

import com.example.upload.common.response.RetResponse;
import com.example.upload.common.response.RetResult;
import com.example.upload.dto.FileUploadRequestDTO;
import com.example.upload.service.impl.MappedByteBufferUploadStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 文件上传接口
 *
 * @author Jiuha
 */
@RestController
public class UploadController {

    @Resource
    private MappedByteBufferUploadStrategy mappedByteBufferUploadStrategy;

    @PostMapping("/upload")
    public RetResult<Object> acceptUploadFile(FileUploadRequestDTO fileUploadRequestDTO) {
        //校验文件是否存在，文件MD5值校验,如果文件MD5值已经存在，直接返回上传成功，实现秒传
        boolean check = mappedByteBufferUploadStrategy.checkFileAndMD5(fileUploadRequestDTO);
        if (check) {
            return RetResponse.makeResponse(HttpStatus.OK, "上传完成", null);
        }
        return mappedByteBufferUploadStrategy.uploadFile(fileUploadRequestDTO);
    }
}
