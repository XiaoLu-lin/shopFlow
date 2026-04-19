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

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.File;


/**
 * 腾讯对象存储服务
 * @author Ysling
 */
@Slf4j
@Data
public class TencentStorage implements AbstractStorage {

    private String secretId;
    private String secretKey;
    private String region;
    private String bucketName;
    private volatile COSClient cosClient;

    /**
     * 懒加载腾讯对象存储客户端
     */
    private COSClient getCOSClient() {
        if (cosClient == null) {
            synchronized (this) {
                if (cosClient == null) {
                    // 1 初始化用户身份信息(secretId, secretKey)
                    COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
                    // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
                    ClientConfig clientConfig = new ClientConfig(new Region(region));
                    cosClient = new COSClient(cred, clientConfig);
                }
            }
        }
        return cosClient;
    }

    /**
     * 腾讯对象存储公共地址
     */
    private String getBaseUrl() {
        return "https://" + bucketName + ".cos." + region + ".myqcloud.com/";
    }

    @Override
    public void store(File file, String keyName) {
        try {
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            // 对象键（Key）是对象在存储桶中的唯一标识。例如，在对象的访问域名 `bucket1-1250000000.cos.ap-guangzhou.myqcloud.com/doc1/pic1.jpg`
            // 中，对象键为 doc1/pic1.jpg, 详情参考 [对象键](https://cloud.tencent.com/document/product/436/13324)
            getCOSClient().putObject(new PutObjectRequest(bucketName, getNewPath(keyName), file));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(MultipartFile file, String keyName) {
        try {
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            // 对象键（Key）是对象在存储桶中的唯一标识。例如，在对象的访问域名 `bucket1-1250000000.cos.ap-guangzhou.myqcloud.com/doc1/pic1.jpg`
            // 中，对象键为 doc1/pic1.jpg, 详情参考 [对象键](https://cloud.tencent.com/document/product/436/13324)
            getCOSClient().putObject(new PutObjectRequest(bucketName, getNewPath(keyName), file.getInputStream(), metadata));
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
            // 对象键（Key）是对象在存储桶中的唯一标识。例如，在对象的访问域名 `bucket1-1250000000.cos.ap-guangzhou.myqcloud.com/doc1/pic1.jpg`
            // 中，对象键为 doc1/pic1.jpg, 详情参考 [对象键](https://cloud.tencent.com/document/product/436/13324)
            getCOSClient().putObject(new PutObjectRequest(bucketName, getNewPath(keyName), inputStream, metadata));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String keyName) {
        try {
            getCOSClient().deleteObject(bucketName, getNewPath(keyName));
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
