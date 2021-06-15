package com.example.upload.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 文件上传数据库保存信息
 *
 * @author Jiuha
 */
@Data
@Entity
@Table(name = "t_file")
public class FileUploadInfo {

    /**
     * 主键ID
     */
    @GenericGenerator(name = "jpa-uuid", strategy = "uuid2")
    @GeneratedValue(generator = "jpa-uuid")
    @Id
    @Column(length = 100)
    private String id;

    /**
     * 文件上传状态
     * 0:未完全上传  1:已经上传完全
     */
    private Integer uploadStatus;

    /**
     * 上传完成时间戳
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime;

    /**
     * 文件保存路径
     */
    private String path;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件扩展名
     */
    private String fileExt;

    /**
     * 文件原始文件名
     */
    private String fileName;

    /**
     * 文件MD5值
     */
    private String fileId;
}
