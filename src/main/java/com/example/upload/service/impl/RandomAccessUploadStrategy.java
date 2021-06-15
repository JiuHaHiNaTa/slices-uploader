package com.example.upload.service.impl;

import com.example.upload.dto.FileUploadRequestDTO;
import com.example.upload.service.UploadTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

/**
 * RandomAccessFile实现分片上传
 *
 * @author Jiuha
 */
@Service
public class RandomAccessUploadStrategy extends UploadTemplate {

    @Value("${upload.path}")
    private String uploadPath;

    @Value(("${upload.chunkSize}"))
    private long defaultChunkSize;

    @Override
    public boolean upload(FileUploadRequestDTO param) {
        File tmpFile = super.createTmpFile(param);
        if (!tmpFile.exists()) {
            return false;
        }
        try (RandomAccessFile accessFile = new RandomAccessFile(tmpFile, "rw")) {
            //确定分片大小
            long chunkSize = Objects.isNull(param.getCurrentChunkSize()) ? defaultChunkSize * 1024 : param.getCurrentChunkSize();
            long offset = chunkSize * param.getChunkNumber();
            //定位偏移量继续上传
            accessFile.seek(offset);
            //写入分片数据
            accessFile.write(param.getFile().getBytes());
            return super.checkAndSetUploadProgress(param, uploadPath);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return false;
    }
}