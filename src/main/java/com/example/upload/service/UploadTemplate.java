package com.example.upload.service;

import cn.hutool.core.io.FileUtil;
import com.example.upload.common.constants.FileConstants;
import com.example.upload.common.exception.UploadException;
import com.example.upload.common.response.RetResponse;
import com.example.upload.common.response.RetResult;
import com.example.upload.entity.FileUploadInfo;
import com.example.upload.dto.FileUploadRequestDTO;
import com.example.upload.mapper.FileUploadInfoMapper;
import com.example.upload.util.RedisUtil;
import com.example.upload.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 上传文件抽象类
 *
 * @author Jiuha
 */
@Slf4j
public abstract class UploadTemplate implements UploadStrategy {

    @Value("${upload.path}")
    private String uploadPath;

    @Resource
    public FileUploadInfoMapper fileUploadInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RetResult<Object> uploadFile(FileUploadRequestDTO param) {
        //上传文件
        boolean result = this.upload(param);
        //文件所有分片上传成功
        if (result) {
            //根据上传结果判断该部分是否上传完成
            File tmpFile = this.createTmpFile(param);
            boolean dataResult = this.saveAndFileUploadDTO(param.getFile().getOriginalFilename(), tmpFile);
            tmpFile.deleteOnExit();
            return dataResult ? RetResponse.makeResponse(HttpStatus.OK, "上传成功", null) : RetResponse.makeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "上传失败", null);
        }
        String md5 = com.example.upload.util.FileUtil.getMd5(param.getFile());
        Map<Integer, String> map = new HashMap<>(2);
        map.put(param.getChunkNumber(), md5);
        return RetResponse.makeResponse(HttpStatus.OK, "上传失败", map);
    }

    /**
     * 创建/获取 临时文件
     *
     * @param param 分片文件请求信息
     * @return 文件基本类
     */
    public File createTmpFile(FileUploadRequestDTO param) {
        MultipartFile file = param.getFile();
        String fileName = file.getOriginalFilename();
        LocalDateTime today = LocalDateTime.now();
        String uploadDirPath = uploadPath
                + today.getYear() + File.separator
                + today.getMonth() + File.separator
                + today.getDayOfMonth() + File.separator;
        String tmpFileName = fileName + "_tmp";
        File tmpDir = new File(uploadDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        return new File(uploadDirPath, tmpFileName);
    }

    /**
     * 上传文件类
     *
     * @param param 分片文件请求DTO类
     * @return 是否成功
     */
    public abstract boolean upload(FileUploadRequestDTO param);


    /**
     * 检测是否已经上传过文件通过查询数据库id进行判断
     *
     * @return true:存在MD5值相同的文件，false存在
     */
    public boolean checkFileAndMD5(FileUploadRequestDTO param) {
        RedisUtil redisUtil = SpringContextUtil.getBean(RedisUtil.class);
        Object value = redisUtil.hget(FileConstants.FILE_UPLOAD_STATUS, param.getIdentifier());
        boolean result = !Objects.isNull(value) && Boolean.parseBoolean(String.valueOf(value));
        log.info("是否符合秒传逻辑？ {}", result ? "是" : "否");
        return result;
    }

    /**
     * 校验分片文件是否上传完成,如果上传完成对文件进行重命名
     *
     * @param param 请求DTO
     * @param path  路径
     * @return true:false
     */
    public boolean checkAndSetUploadProgress(FileUploadRequestDTO param, String path) {
        //如果上传完成返回true
        String fileName = param.getFile().getOriginalFilename();
        LocalDateTime today = LocalDateTime.now();
        String uploadDirPath = uploadPath
                + today.getYear() + File.separator
                + today.getMonth() + File.separator
                + today.getDayOfMonth() + File.separator;
        File confFile = new File(uploadDirPath, fileName + ".conf");
        byte isComplete = Byte.MAX_VALUE;
        try (RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");) {
            //把该分段标记为 true 表示完成
            int number = param.getChunkNumber() - 1;
            int chunks = param.getTotalChunks() - 1;
            log.info("set part " + number + " complete");
            //创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认0,已上传的就是Byte.MAX_VALUE 127
            accessConfFile.setLength(chunks);
            accessConfFile.seek(number);
            accessConfFile.write(Byte.MAX_VALUE);
            //completeList 检查是否全部完成,如果数组里是否全部都是127(全部分片都成功上传)
            byte[] completeList = FileUtil.readBytes(confFile);
            for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
                //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
                isComplete = (byte) (isComplete & completeList[i]);
                log.info("check part " + i + " completeList :" + completeList[i] + " isComplete :" + isComplete);
            }
            log.info("config length:{} , check complete: {}", completeList.length, isComplete);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UploadException("文件上传异常！");
        }
        return setUploadProgressToRedis(param, uploadPath, fileName, confFile, isComplete);
    }

    /**
     * 把上传进度信息存进redis
     */
    public boolean setUploadProgressToRedis(FileUploadRequestDTO param, String uploadDirPath,
                                            String fileName, File confFile, byte isComplete) {
        RedisUtil redisUtil = SpringContextUtil.getBean(RedisUtil.class);
        //如果isComplete == Byte.MAX_VALUE说明文件所有分片已经上传完成
        if (isComplete == Byte.MAX_VALUE) {
            redisUtil.hset(FileConstants.FILE_UPLOAD_STATUS, param.getIdentifier(), "true");
            redisUtil.del(FileConstants.FILE_MD5_KEY + param.getIdentifier());
            //删除文件完整性校验文件
            confFile.delete();
            return true;
        } else {
            if (!redisUtil.hHasKey(FileConstants.FILE_UPLOAD_STATUS, param.getIdentifier())) {
                redisUtil.hset(FileConstants.FILE_UPLOAD_STATUS, param.getIdentifier(), "false");
                redisUtil.set(FileConstants.FILE_MD5_KEY + param.getIdentifier(),
                        uploadDirPath + File.separator + fileName + ".conf");
            }
            return false;
        }
    }

    /**
     * 文件上传完成后，保存文件信息到数据库
     */
    public boolean saveAndFileUploadDTO(String fileName, File tmpFile) {

        FileUploadInfo fileUploadInfo = null;
        try {
            fileUploadInfo = renameFile(tmpFile, fileName);
            if (fileUploadInfo != null) {
                log.info("upload complete !!" + " name=" + fileName);
                //保存文件信息到数据库
                fileUploadInfoMapper.save(fileUploadInfo);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileUploadInfo != null;
    }

    /**
     * 文件重命名（未上传完成前，文件名含有_tmp,上传完成后进行重命名）
     *
     * @param toBeRenamed   将要修改名字的文件
     * @param toFileNewName 新的名字
     */
    private FileUploadInfo renameFile(File toBeRenamed, String toFileNewName) {
        //检查要重命名的文件是否存在，是否是文件
        FileUploadInfo fileUploadInfo = new FileUploadInfo();
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            log.info("File does not exist: {}", toBeRenamed.getName());
            return null;
        }
        String ext = FileUtil.getSuffix(toFileNewName);
        String p = toBeRenamed.getParent();
        String filePath = p + File.separator + toFileNewName;
        File newFile = new File(filePath);
        //todo 修改文件名 windows下存在无法改名的问题，暂时使用copy代替，此处存在BUG使用rename改名失败，本地返回错误本地调用返回false，其他工具返回文件已经被进程占用
        FileUtil.copyFile(toBeRenamed, newFile);
        String md5 = com.example.upload.util.FileUtil.getMd5ByFile(newFile);
        fileUploadInfo.setUploadTime(LocalDateTime.now());
        fileUploadInfo.setUploadStatus(toFileNewName.equals(newFile.getName()) ? FileConstants.FINISHED : FileConstants.UNFINISHED);
        fileUploadInfo.setPath(filePath);
        fileUploadInfo.setSize(newFile.length());
        fileUploadInfo.setFileExt(ext);
        fileUploadInfo.setFileName(toFileNewName);
        fileUploadInfo.setFileId(md5);
        return fileUploadInfo;
    }
}
