package com.example.upload.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Jiuha
 */
@Data
public class FileUploadRequestDTO {

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 分片号
     */
    private Integer chunkNumber;

    /**
     * 分片大小
     */
    private Long chunkSize;

    /**
     * 分片大小
     */
    private Long currentChunkSize;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 上传时文件名
     */
    private String filename;

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 文件MD5值,唯一标识
     */
    private String identifier;

}
