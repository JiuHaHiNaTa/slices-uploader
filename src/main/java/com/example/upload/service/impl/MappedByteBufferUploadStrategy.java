package com.example.upload.service.impl;

import com.example.upload.dto.FileUploadRequestDTO;
import com.example.upload.service.UploadTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

/**
 * MappedByteBuffer实现分片上传
 *
 * @author Jiuha
 */
@Service
public class MappedByteBufferUploadStrategy extends UploadTemplate {

    @Value("${upload.path}")
    private String uploadPath;

    @Value(("${upload.chunkSize}"))
    private long defaultChunkSize;

    @Override
    public boolean upload(FileUploadRequestDTO param) {
        File tmpFile = super.createTmpFile(param);
        MappedByteBuffer mappedByteBuffer = null;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(tmpFile, "rw");
             FileChannel fileChannel = randomAccessFile.getChannel()) {
            //确定分片大小
            long chunkSize = Objects.isNull(param.getChunkSize()) ? defaultChunkSize * 1024 : param.getChunkSize();
            long offset = chunkSize * (param.getChunkNumber() - 1);
            //获取分片数据
            byte[] fileData = param.getFile().getBytes();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, fileData.length);
            mappedByteBuffer.put(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Objects.requireNonNull(mappedByteBuffer).clear();
        }
        return super.checkAndSetUploadProgress(param, uploadPath);
    }
}
