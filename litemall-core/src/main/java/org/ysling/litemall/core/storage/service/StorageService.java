package org.ysling.litemall.core.storage.service;
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

import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.ysling.litemall.core.baidu.service.BaibuAipSecCheckService;
import org.ysling.litemall.core.storage.storage.AbstractStorage;
import org.ysling.litemall.db.domain.LitemallStorage;
import org.ysling.litemall.db.service.IStorageService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Objects;

/**
 * 提供存储服务类，所有存储服务均由该类对外提供
 */
@Data
public class StorageService {

    /**对象存储文件表*/
    private IStorageService storageService;
    /**当前使用的对象存储类型*/
    private String active;
    /**存储通用地址*/
    private String storagePath;
    /**对象存储抽象接口*/
    private AbstractStorage abstractStorage;

    /**
     * 存储一个文件对象
     * @param data          输入流
     * @param fileName      文件名
     */
    public LitemallStorage store(byte[] data, String fileName) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        String keyName = generateKey(inputStream, fileName);
        LitemallStorage storage = isStorage(keyName, data.length);
        if (storage != null){
            return storage;
        }
        abstractStorage.store(data, keyName);
        return addStorage(fileName, data.length, keyName);
    }


    /**
     * 存储一个文件对象
     * @param file 文件对象
     */
    public LitemallStorage store(File file){
        //添加到对象存储
        String keyName = generateKey(file, file.getName());
        LitemallStorage storage = isStorage(keyName, file.length());
        if (storage != null){
            return storage;
        }
        abstractStorage.store(file, keyName);
        return addStorage(file.getName(), file.length(), keyName);
    }


    /**
     * 存储一个文件对象
     * @param file 文件对象
     */
    public LitemallStorage store(MultipartFile file) {
        try {
            String filename = Objects.requireNonNull(file.getOriginalFilename());
            String keyName = generateKey(file.getInputStream(), filename);
            LitemallStorage storage = isStorage(keyName, file.getSize());
            if (storage != null){
                return storage;
            }
            abstractStorage.store(file, keyName);
            return addStorage(filename, file.getSize(), keyName);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取对象存储是否存在
     * @param keyName 对象存储唯一key
     * @return 对象
     */
    public LitemallStorage isStorage(String keyName, long size) {
        Resource resource = abstractStorage.loadAsResource(keyName);
        if (resource == null){
            return null;
        }
        return getStorage(keyName, size, keyName);
    }

    /**
     * 将对象存入数据库
     */
    private LitemallStorage addStorage(String name, long size, String keyName){
        String url = generateUrl(keyName);
        this.picSecCheck(keyName, url);
        LitemallStorage storage = getStorage(name, size, keyName);
        storageService.add(storage);
        return storage;
    }

    /**
     * 获取对象
     */
    private LitemallStorage getStorage(String name, Long size, String keyName){
        String url = generateUrl(keyName);
        LitemallStorage storageInfo = new LitemallStorage();
        storageInfo.setName(name);
        storageInfo.setSize(size.intValue());
        storageInfo.setType(getMediaType(keyName).toString());
        storageInfo.setKey(keyName);
        storageInfo.setUrl(url);
        return storageInfo;
    }

    /**
     * 违规图片校验
     * @param keyName   文件key
     * @param url       文件地址
     */
    public void picSecCheck(String keyName, String url) {
        try {
            BaibuAipSecCheckService.picSecCheck(url);
        }catch (Exception e){
            delete(keyName);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取对象key值
     * @param inputStream   文件流
     * @param filename      文件名称
     */
    private String generateKey(InputStream inputStream, String filename) {
        int index = filename.lastIndexOf('.');
        if (index < 0){
            return DigestUtil.md5Hex(inputStream);
        }
        String suffix = filename.substring(index);
        return DigestUtil.md5Hex(inputStream) + suffix;
    }

    /**
     * 获取对象key值
     * @param file     文件
     * @param filename 文件名称
     */
    private String generateKey(File file, String filename) {
        int index = filename.lastIndexOf('.');
        if (index < 0){
            return DigestUtil.md5Hex(file);
        }
        String suffix = filename.substring(index);
        return DigestUtil.md5Hex(file) + suffix;
    }

    /**
     * 获取Resource
     * @param keyName 对象key
     */
    public Resource generateFile(String keyName) {
        if (keyName == null || keyName.contains("../")) {
            return null;
        }
        return loadAsResource(keyName);
    }

    /**
     * 获取对象类型
     * @param keyName 对象key
     */
    public MediaType getMediaType(String keyName) {
        String type = URLConnection.guessContentTypeFromName(keyName);
        if (type == null){
            return MediaType.MULTIPART_FORM_DATA;
        }
        return MediaType.parseMediaType(type);
    }

    /**
     * 获取Resource
     * @param keyName 对象key
     */
    public Resource loadAsResource(String keyName) {
        return abstractStorage.loadAsResource(keyName);
    }

    /**
     * 删除对象
     * @param keyName 对象key
     */
    public void delete(String keyName) {
        abstractStorage.delete(keyName);
    }

    /**
     * 获取对象地址
     * @param keyName   对象key
     */
    private String generateUrl(String keyName) {
        return abstractStorage.generateUrl(keyName);
    }

}
