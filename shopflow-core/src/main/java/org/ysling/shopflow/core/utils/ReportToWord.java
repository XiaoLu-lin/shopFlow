package org.ysling.shopflow.core.utils;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 数据库表结构word导出
 * @author Ysling
 */
public class ReportToWord {

    
//    public static void main(String[] args) {
//        HikariConfig hikariConfig = new HikariConfig();
//        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/shopflow?useSSL=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
//        hikariConfig.setUsername("root");
//        hikariConfig.setPassword("123456");
//        //设置可以获取tables remarks信息
//        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
//        hikariConfig.setMinimumIdle(2);
//        hikariConfig.setMaximumPoolSize(5);
//        DataSource dataSource = new HikariDataSource(hikariConfig);
//
//        // 生成文件配置
//        EngineConfig engineConfig = EngineConfig.builder()
//                // 生成文件路径，自己mac本地的地址，这里需要自己更换下路径
//                .fileOutputDir("C:\\Users\\ysl\\Desktop")
//                // 打开目录
//                .openOutputDir(false)
//                // 文件类型
//                .fileType(EngineFileType.WORD)
//                // 生成模板实现
//                .produceType(EngineTemplateType.freemarker).build();
//
//        // 生成文档配置（包含以下自定义版本号、描述等配置连接）
//        Configuration config = Configuration.builder()
//                .version("1.0")
//                .description("数据库设计文档")
//                .dataSource(dataSource)
//                .engineConfig(engineConfig)
//                .produceConfig(getProcessConfig())
//                .build();
//
//        // 执行生成
//        new DocumentationExecute(config).execute();
//    }

    public static ProcessConfig getProcessConfig() {
        // 忽略表名
        List<String> ignoreTableName = Arrays.asList("test", "test_group");
        // 忽略表前缀，如忽略a开头的数据库表
        List<String> ignorePrefix = Arrays.asList("test", "test");
        // 忽略表后缀
        List<String> ignoreSuffix = Arrays.asList("_test", "test_");
        return ProcessConfig.builder()
                //根据名称指定表生成
                .designatedTableName(Collections.emptyList())
                //根据表前缀生成
                .designatedTablePrefix(Collections.emptyList())
                //根据表后缀生成
                .designatedTableSuffix(Collections.emptyList())
                //忽略表名
                .ignoreTableName(ignoreTableName)
                //忽略表前缀
                .ignoreTablePrefix(ignorePrefix)
                //忽略表后缀
                .ignoreTableSuffix(ignoreSuffix).build();
    }


}

