package org.ysling.shopflow.core.storage.storage;
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


import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * 千牛对象存储
 */
@Slf4j
@Data
public class QiniuStorage implements AbstractStorage {

    private Auth auth;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private BucketManager bucketManager;
    private volatile UploadManager uploadManager;

    /**
     * 初始化
     */
    private void getInitAuth() {
        if (uploadManager == null) {
            synchronized (this) {
                if (uploadManager == null) {
                    if (auth == null) {
                        auth = Auth.create(accessKey, secretKey);
                    }
                    uploadManager = new UploadManager(new Configuration());
                }
            }
        }
    }

    @Override
    public void store(File file, String keyName) {
        try {
            this.getInitAuth();
            String upToken = auth.uploadToken(bucketName);
            uploadManager.put(new FileInputStream(file), getNewPath(keyName), upToken, null, getMediaType(keyName));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(MultipartFile file, String keyName) {
        try {
            this.getInitAuth();
            String upToken = auth.uploadToken(bucketName);
            uploadManager.put(file.getInputStream(), getNewPath(keyName), upToken, null, file.getContentType());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(byte[] data, String keyName) {
        try {
            this.getInitAuth();
            String upToken = auth.uploadToken(bucketName);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            uploadManager.put(inputStream, getNewPath(keyName), upToken, null, getMediaType(keyName));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String keyName) {
        try {
            this.getInitAuth();
            bucketManager.delete(bucketName, getNewPath(keyName));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateUrl(String keyName) {
        return endpoint + "/" + getNewPath(keyName);
    }
}
