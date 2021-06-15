package com.example.upload.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * 文件工具类
 *
 * @author Jiuha
 */
@Slf4j
public class FileUtil {

    /**
     * 计算文件MD5
     *
     * @param file 文件
     * @return 文件MD5
     */
    public static String getMd5ByFile(File file) {
        return countMD5(file);
    }

    /**
     * 计算文件MD5
     *
     * @param path 文件路径
     * @return 文件MD5
     */
    public static String getMd5ByFile(String path) {
        File file = new File(path);
        return countMD5(file);
    }

    /**
     * 计算MD5值
     *
     * @param file 文件
     * @return MD5串
     */
    private static String countMD5(File file) {
        String value = null;
        try (FileInputStream in = new FileInputStream(file)) {
            MappedByteBuffer byteBuffer =
                    in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取上传文件的md5
     * @param file 上传文件值
     * @return MD5串
     */
    public static String getMd5(MultipartFile file) {
        try {
            //获取文件的byte信息
            byte[] uploadBytes = file.getBytes();
            // 拿到一个MD5转换器
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            //转换为16进制
            return new BigInteger(1, digest).toString(16);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
