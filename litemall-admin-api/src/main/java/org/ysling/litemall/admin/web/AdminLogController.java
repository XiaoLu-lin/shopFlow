package org.ysling.litemall.admin.web;
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
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.annotation.RequiresPermissionsDesc;
import org.ysling.litemall.admin.model.log.body.LogListBody;
import org.ysling.litemall.admin.model.log.body.LogSystemListBody;
import org.ysling.litemall.admin.socket.AdminWebSocketContext;
import org.ysling.litemall.core.annotation.JsonBody;
import org.ysling.litemall.core.utils.ReadSelectedLine;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.admin.service.AdminLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ysling.litemall.db.entity.FileInfo;
import org.ysling.litemall.db.entity.PageResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 操作日志
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/log")
@Validated
public class AdminLogController {

    @Autowired
    private AdminLogService logService;
    @Autowired
    private ScheduledExecutorService executorService;

    /**
     * 查询
     */
    @SaCheckPermission("admin:log:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "操作日志"}, button = "查询")
    @GetMapping("/list")
    public Object list(LogListBody body) {
        return ResponseUtil.okList(logService.querySelective(body));
    }


    /**
     * 系统日志列表查询
     * @param body 请求参数
     */
    @SaCheckPermission("admin:log:system-list")
    @RequiresPermissionsDesc(menu = {"系统管理", "系统日志"}, button = "系统日志列表")
    @GetMapping("/system-list")
    public Object systemList(LogSystemListBody body) {
        ArrayList<FileInfo> fileInfoList = new ArrayList<>();
        File[] files = FileUtil.ls(System.getProperty("user.dir") + body.getFilePath());
        for (File file :files) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileType(FileUtil.getType(file));
            fileInfo.setFileName(file.getName());
            fileInfo.setFilePath(file.getPath());
            fileInfo.setFileSize(file.length());
            fileInfo.setSizeText(file.length());
            if (StringUtils.hasText(body.getFileName())){
                if (file.getName().contains(body.getFileName())){
                    fileInfoList.add(fileInfo);
                }
            }else {
                fileInfoList.add(fileInfo);
            }
        }
        return ResponseUtil.ok(new PageResult<>(fileInfoList, body));
    }


    /**
     * 系统日志列表读取
     * 异步推送数据
     * @param path 请求参数
     */
    @SaCheckPermission("admin:log:system-read")
    @RequiresPermissionsDesc(menu = {"系统管理", "系统日志"}, button = "系统日志读取")
    @GetMapping("/system-read")
    public Object systemRead(@JsonBody String path) {
        String masterId = StpUtil.getLoginIdAsString();
        File file = new File(path);
        // 第一次执行的时间为1秒，然后每隔1秒执行一次
        executorService.scheduleAtFixedRate(new Runnable() {
            /**上次文件大小*/
            private int fromIndex = 0;
            @Override
            public void run() {
                try {
                    List<String> readLines;
                    if (fromIndex == 0){
                        readLines = FileUtils.readLines(file,"UTF-8");
                    }else {
                        readLines = ReadSelectedLine.readLineByNum(file, fromIndex);
                    }
                    AdminWebSocketContext.sendLog(masterId, readLines);
                    fromIndex += readLines.size();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        return ResponseUtil.ok();
    }


}
