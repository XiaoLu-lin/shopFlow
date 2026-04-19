package org.ysling.litemall.wx.web.impl;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.ysling.litemall.core.storage.service.StorageService;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象存储服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebStorageService {

    @Autowired
    private StorageService storageService;

    /**
     * 获取微信小程序上传的图片
     */
    public Object upload(MultipartFile file) throws IOException {
        return ResponseUtil.ok(storageService.store(file));
    }

    /**
     * 获取微信小程序上传的图片
     */
    public Object upload(List<MultipartFile> files) throws IOException {
        ArrayList<String> urls = new ArrayList<>();
        for (MultipartFile file :files) {
            urls.add(storageService.store(file).getUrl());
        }
        return ResponseUtil.ok(urls);
    }

    /**
     * 访问存储对象
     * @param key 存储对象key
     */
    public ResponseEntity<Resource> fetch(String key) {
        Resource file = storageService.generateFile(key);
        if (file == null){
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = storageService.getMediaType(key);
        return ResponseEntity.ok().contentType(mediaType).body(file);
    }

    /**
     * 访问存储对象
     * @param key 存储对象key
     */
    public ResponseEntity<Resource> download(String key) {
        Resource file = storageService.generateFile(key);
        if (file == null){
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = storageService.getMediaType(key);
        String headerValue = "attachment; filename=\"" + file.getFilename() + "\"";
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(file);
    }

}
