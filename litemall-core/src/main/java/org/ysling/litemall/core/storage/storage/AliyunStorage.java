package org.ysling.litemall.core.storage.storage;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [naonao-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.File;


/**
 * 阿里云对象存储服务
 * @author Ysling
 */
@Slf4j
@Data
public class AliyunStorage implements AbstractStorage {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private volatile OSSClient ossClient;

    /**
     * 获取阿里云OSS客户端对象
     */
    private OSSClient getOSSClient() {
        if (ossClient == null) {
            synchronized (this) {
                if (ossClient == null) {
                    ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
                }
            }
        }
        return ossClient;
    }

    /**
     * 阿里对象存储公共地址
     */
    private String getBaseUrl() {
        return "https://" + bucketName + "." + endpoint + "/";
    }

    /**
     * 阿里云OSS对象存储简单上传实现
     */
    @Override
    public void store(File file, String keyName) {
        try {
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length());
            metadata.setContentType(getMediaType(keyName));
            // 对象键（Key）是对象在存储桶中的唯一标识。
            getOSSClient().putObject(new PutObjectRequest(bucketName, getNewPath(keyName), file, metadata));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 阿里云OSS对象存储简单上传实现
     */
    @Override
    public void store(MultipartFile file, String keyName) {
        try {
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(getMediaType(keyName));
            // 对象键（Key）是对象在存储桶中的唯一标识。
            getOSSClient().putObject(new PutObjectRequest(bucketName, getNewPath(keyName), file.getInputStream(), metadata));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(byte[] data, String keyName) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            metadata.setContentType(getMediaType(keyName));
            // 对象键（Key）是对象在存储桶中的唯一标识。
            getOSSClient().putObject(new PutObjectRequest(bucketName, getNewPath(keyName), inputStream, metadata));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String keyName) {
        try {
            getOSSClient().deleteObject(bucketName, getNewPath(keyName));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateUrl(String keyName) {
        return getBaseUrl() + getNewPath(keyName);
    }


}
