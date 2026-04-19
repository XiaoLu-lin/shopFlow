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

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 对象存储抽象接口
 */
public interface AbstractStorage {

    /**
     * 目录分离
     * @param newFileName 获取文件路径
     * @return 文件路径
     */
    default String getNewPath(String newFileName) {
        //获取hashCode值
        int hashCode = newFileName.hashCode();
        //作为一级目录
        int d1 = hashCode & 0xf;
        //把hashCode右移4位得到新的值
        int code = hashCode >>> 4;
        //作为二级目录
        int d2 = code & 0xf;
        //返回一二级目录
        return d1+"/"+d2+"/" + newFileName;
    }

    /**
     * 获取对象信息
     * @param keyName 对象名称
     * @return 对象信息
     */
    default Resource loadAsResource(String keyName){
        try {
            URL url = new URL(generateUrl(keyName));
            Resource resource = new UrlResource(url);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * 获取对象类型
     * @param keyName 对象名称
     */
    default String getMediaType(String keyName) {
        return URLConnection.guessContentTypeFromName(keyName);
    }

    /**
     * 存储一个文件对象
     * @param file    文件对象
     * @param keyName 对象名称
     */
    void store(File file, String keyName);

    /**
     * 存储一个文件对象
     * @param file    文件对象
     * @param keyName 对象名称
     */
    void store(MultipartFile file, String keyName);

    /**
     * 存储一个文件对象
     * @param data          文件数据
     * @param keyName       对象名称
     */
    void store(byte[] data, String keyName);

    /**
     * 获取所有对象地址
     * @return Path
     */
    default Stream<Path> loadAll(){
        return null;
    }

    /**
     * 获取对象地址
     * @param keyName 对象名称
     * @return        地址
     */
    default Path load(String keyName){
        return null;
    }

    /**
     * 删除对象
     * @param keyName 对象名称
     */
    void delete(String keyName);

    /**
     * 获取对象请求URL
     * @param keyName 对象名称
     * @return url地址
     */
    String generateUrl(String keyName);


}