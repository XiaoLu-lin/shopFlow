package org.ysling.litemall.core.jobs;
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

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.mysql.cj.conf.HostInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.core.handler.ActionLogHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ysling.litemall.core.utils.DbUtil;
import org.ysling.litemall.core.utils.FileUtil;
import javax.sql.DataSource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 数据库定时备份任务
 * 在backup文件夹中备份最近七日的数据库文件
 * @author Ysling
 */
@Slf4j
@Component
public class DbJob {

    @Autowired
    private DataSource dataSource;
    /**声明需要格式化的格式(日期加时间)*/
    private final DateTimeFormatter dfDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");

    /**
     * 每个小时备份一次。
     */
    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void backup() {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        Map<String, DataSource> dataSources = ds.getDataSources();
        for (String key :dataSources.keySet()) {
            ItemDataSource itemDataSource = (ItemDataSource)dataSources.get(key);
            DruidDataSource druidDataSource = (DruidDataSource)itemDataSource.getDataSource();
            HostInfo hostInfo = DbUtil.getHostInfo(druidDataSource);
            if (hostInfo == null){
                continue;
            }
            String fileName = dfDateTime.format(LocalDateTime.now()) + ".sql";
            String basePath = FileUtil.getBasePath("/backup/"+key);
            File file = new File(basePath, fileName);
            // 备份今天数据库
            if (DbUtil.backup(file.getPath(), hostInfo)){
                ActionLogHandler.logGeneralSucceed("系统处理定时任务","数据库备份");
            }else {
                ActionLogHandler.logGeneralFail("系统处理定时任务","数据库备份");
            }
            // 删除3天前数据库备份文件
            String fileBeforeName = dfDateTime.format(LocalDateTime.now().minusDays(3)) +".sql";
            File fileBefore = new File(basePath, fileBeforeName);
            if (fileBefore.exists()) {
                //删除3天前数据库备份文件
                if (!fileBefore.delete()){
                    ActionLogHandler.logGeneralSucceed("系统处理定时任务","数据库备份 -> [文件删除失败]");
                }
            }
        }
    }

}
