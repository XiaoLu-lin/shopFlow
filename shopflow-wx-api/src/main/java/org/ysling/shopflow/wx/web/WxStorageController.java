package org.ysling.shopflow.wx.web;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ysling.shopflow.wx.web.impl.WxWebStorageService;
import java.io.IOException;
import java.util.List;

/**
 * 对象存储服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/storage")
@Validated
public class WxStorageController {

    @Autowired
    private WxWebStorageService storageService;

    /**
     * 获取微信小程序上传的图片
     */
    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile file) throws IOException {
        return storageService.upload(file);
    }

    /**
     * 获取微信小程序上传的图片
     */
    @PostMapping("/uploads")
    public Object uploads(@RequestParam("files") List<MultipartFile> files) throws IOException {
        return storageService.upload(files);
    }

    /**
     * 访问存储对象
     * @param key 存储对象key
     */
    @GetMapping("/fetch/{key:.+}")
    public ResponseEntity<Resource> fetch(@PathVariable String key) {
        return storageService.fetch(key);
    }

    /**
     * 访问存储对象
     * @param key 存储对象key
     */
    @GetMapping("/download/{key:.+}")
    public ResponseEntity<Resource> download(@PathVariable String key) {
        return storageService.download(key);
    }

}
