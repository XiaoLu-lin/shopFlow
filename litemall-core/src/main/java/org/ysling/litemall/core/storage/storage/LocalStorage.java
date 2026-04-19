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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * 服务器本地对象存储服务
 * @author Ysling
 */
@Slf4j
@Data
public class LocalStorage implements AbstractStorage {

    /*** 根路径 */
    private Path rootLocation;
    /*** 添加对象存储地址 */
    private String storagePath;
    /*** 这个地方应该是wx模块的WxStorageController的fetch方法对应的地址 */
    private String address;

    /**
     * 添加对象存储·地址
     */
    public void setStoragePath(String storagePath) {
        try {
            this.storagePath = storagePath;
            this.rootLocation = Paths.get(storagePath);
            Files.createDirectories(rootLocation);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(File file, String keyName) {
        try {
            Path datePath = rootLocation.resolve(getNewPath(keyName));
            Files.createDirectories(datePath);
            Files.copy(file.toPath(), datePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(MultipartFile file, String keyName) {
        try {
            Path datePath = rootLocation.resolve(getNewPath(keyName));
            Files.createDirectories(datePath);
            Files.copy(file.getInputStream(), datePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(byte[] data, String keyName) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            Path datePath = rootLocation.resolve(getNewPath(keyName));
            Files.createDirectories(datePath);
            Files.copy(inputStream, datePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation))
                    .map(path -> rootLocation.relativize(path));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource loadAsResource(String keyName) {
        try {
            Resource resource = new UrlResource(load(keyName).toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Path load(String keyName) {
        return rootLocation.resolve(getNewPath(keyName));
    }

    @Override
    public void delete(String keyName) {
        try {
            Files.delete(load(keyName));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String generateUrl(String keyName) {
        return address + keyName;
    }
}