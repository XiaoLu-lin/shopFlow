package org.ysling.shopflow.core.utils;
// Copyright (c) [ysling] [927069313@qq.com]
// [naonao-jub] is licensed under Mulan PSL v2.
// You can use this software according to the terms and conditions of the Mulan PSL v2.
// You may obtain a copy of Mulan PSL v2 at:
//             http://license.coscl.org.cn/MulanPSL2
// THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
// MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// See the Mulan PSL v2 for more details.
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.jdbc.ConnectionImpl;
import lombok.extern.slf4j.Slf4j;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * 数据库备份
 */
@Slf4j
public class DbUtil {

    /**
     * 备份本地数据库
     * @param filePath  文件地址
     * @param hostInfo  数据库链接信息
     * @return          成功true:失败false
     */
    public static boolean backup(String filePath, HostInfo hostInfo) {
        // 备份命令
        StringBuilder cmd = new StringBuilder()
                .append("mysqldump")
                .append(" -h'")
                .append(hostInfo.getHost())
                .append("' -P'")
                .append(hostInfo.getPort())
                .append("' -u'")
                .append(hostInfo.getUser())
                .append("' -p'")
                .append(hostInfo.getPassword())
                .append("' ")
                .append(hostInfo.getProperty(PropertyKey.DBNAME.getKeyName()))
                .append(" > ")
                .append(filePath);
        return runtimeExec(cmd);
    }


    /**
     * 恢复数据库
     * @param filePath  文件地址
     * @param hostInfo  数据库链接信息
     * @return          成功true:失败false
     */
    public static boolean load(String filePath, HostInfo hostInfo) {
        StringBuilder cmd = new StringBuilder()
                .append("mysql ")
                .append(" -h'")
                .append(hostInfo.getHost())
                .append("' -P'")
                .append(hostInfo.getPort())
                .append("' -u'")
                .append(hostInfo.getUser())
                .append("' -p'")
                .append(hostInfo.getPassword())
                .append("' ")
                .append(hostInfo.getProperty(PropertyKey.DBNAME.getKeyName()))
                .append(" < ")
                .append(filePath);
        return runtimeExec(cmd);
    }

    /**
     * 获取数据库初始化信息
     * @param url           数据库地址
     * @param username      用户名
     * @param password      密码
     */
    public static HostInfo getHostInfo(String url, String username, String password){
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            HostInfo hostInfo = getHostInfo(connection);
            connection.close();
            return hostInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取数据库初始化信息
     * @param dataSource      数据库
     */
    public static HostInfo getHostInfo(DruidDataSource dataSource){
        try {
            String url = dataSource.getUrl();
            String username = dataSource.getUsername();
            String password = dataSource.getPassword();
            return getHostInfo(url, username, password);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过数据库链接获取数据库信息
     * @param connection 数据库链接
     */
    private static HostInfo getHostInfo(Connection connection){
        Properties properties = ((ConnectionImpl)connection).getProperties();
        //将map参数重新转换
        HashMap<String, String> hashMap = new HashMap<>();
        for (Object key :properties.keySet()) {
            String keyStr = key.toString();
            hashMap.put(keyStr, properties.getProperty(keyStr));
        }
        String host = properties.getProperty(PropertyKey.HOST.getKeyName());
        String port = properties.getProperty(PropertyKey.PORT.getKeyName());
        String username = properties.getProperty(PropertyKey.USER.getKeyName());
        String password = properties.getProperty(PropertyKey.PASSWORD.getKeyName());
        return new HostInfo(null, host, Integer.parseInt(port), username, password, hashMap);
    }

    /**
     * 执行系统命令
     * @param cmd 命令
     * @return 成功or失败
     */
    private static boolean runtimeExec(StringBuilder cmd) {
        try {
            String[] command = getCommand(cmd);
            //获取Runtime实例 执行恢复指令
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断操作系统类型、Linux|Windows
     */
    public static String[] getCommand(StringBuilder cmd) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            return new String[]{"cmd", "/c", String.valueOf(cmd)};
        } else if (osName.startsWith("linux")) {
            return new String[]{"bash", "-c", String.valueOf(cmd)};
        }
        return new String[]{"bash", "-c", String.valueOf(cmd)};
    }

}