package com.example.upload.mapper;

import com.example.upload.entity.FileUploadInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jiuha
 */
public interface FileUploadInfoMapper extends JpaRepository<FileUploadInfo, String> {

    /**
     * 根据MD5值查询文件信息
     * @param fileId 文件ID值
     * @return 文件数据库保存信息
     */
    FileUploadInfo findByFileId(String fileId);
}
