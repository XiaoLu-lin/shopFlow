package org.ysling.shopflow.db.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.ysling.shopflow.db.entity.ColumnOverride;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * mybatis-plus代码生成器
 * @author Ysling
 */
public class MyBatisGeneratorRun {


    /**
     * mybatis-plus代码生成器
     */
    public static void main(String[] args) {
        //公共包路径
        String outputDir = System.getProperty("user.dir") + "/shopflow-db/src/main";
        //创建代码生成器
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/shopflow","root","123456")
                // 全局配置
                .globalConfig(builder -> {
                    // 设置作者
                    builder.author("ysling")
                        // 指定输出目录
                        .outputDir(outputDir + "/java")
                        //禁止打开输出目录，默认打开
                        .disableOpenDir();
                })
                // 包配置
                .packageConfig(builder -> {
                    // 设置父包名
                    builder.parent("org.ysling.shopflow.db") 
                         // 设置实体类包名
                        .entity("domain")
                         // 设置mapperXml生成路径
                        .pathInfo(Collections.singletonMap(OutputFile.xml, outputDir + "/resources/mappers")); 
                })
                //自定义模板参数
                .injectionConfig(builder -> { 
                    builder.customMap(Collections.singletonMap("typeHandlerList", getTypeHandler()));
                })
                //使用自定义模板
                .templateConfig(builder -> {
                    // 禁用模板	TemplateType.ENTITY
                    builder.disable(TemplateType.ENTITY) 
                        .entity("/templates/entity.java")
                        .service("/templates/service.java")
                        .serviceImpl("/templates/serviceImpl.java")
                        .mapper("/templates/mapper.java")
                        .xml("/templates/mapper.xml")
                        .controller("")
                        .build();
                })
                // 策略配置
                .strategyConfig(builder -> {
                    // 请输入表名，多个英文逗号分隔？所有输入 all
                    builder.addTablePrefix("shopflow_");
                    // 设置需要生成的表名
                    builder.addInclude(getTables("all")) 
                        // Entity 策略配置
                        .entityBuilder()
                        .formatFileName("Shopflow%s")
                        .versionColumnName("version")
                        .versionPropertyName("version")
                        .logicDeleteColumnName("deleted")
                        .logicDeletePropertyName("deleted")
                        .enableTableFieldAnnotation()
                        .enableColumnConstant()
                        .addTableFills(new Column("add_time", FieldFill.INSERT))
                        .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))
                        //开启 Lombok
                        .enableLombok()
                        // 覆盖已生成文件
                        .enableFileOverride()
                        //数据库表映射到实体的命名策略：下划线转驼峰命
                        .naming(NamingStrategy.underline_to_camel)
                        //数据库表字段映射到实体的命名策略：下划线转驼峰命
                        .columnNaming(NamingStrategy.underline_to_camel)    
                        // Mapper 策略配置
                        .mapperBuilder()
                        .formatMapperFileName("%sMapper")
                        .formatXmlFileName("%sMapper")
                        .enableBaseResultMap()
                        // 覆盖已生成文件
                        .enableFileOverride() 
                        // Service 策略配置
                        .serviceBuilder()
                        .superServiceClass(IBaseService.class)
                        .superServiceImplClass(IBaseServiceImpl.class)
                        // 覆盖已生成文件
                        .enableFileOverride()
                        //格式化 service 接口文件名称，%s进行匹配表名，如 UserService
                        .formatServiceFileName("I%sService")
                        //格式化 service 实现类文件名称，%s进行匹配表名，如 UserServiceImpl
                        .formatServiceImplFileName("%sServiceImpl"); 
                })
                .execute();

    }

    /**
     * 处理 all 情况
     * @param tables 表名
     */
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }


    /**
     * 将指定字段转换成数组
     */
    protected static ArrayList<ColumnOverride> getTypeHandler(){
        ArrayList<ColumnOverride> typeList = new ArrayList<>();
        typeList.add(new ColumnOverride("role_ids" , ColumnOverride.TYPE.STRING));
        typeList.add(new ColumnOverride("specifications" , ColumnOverride.TYPE.STRING));
        typeList.add(new ColumnOverride("pic_urls" , ColumnOverride.TYPE.STRING));
        typeList.add(new ColumnOverride("gallery" , ColumnOverride.TYPE.STRING));
        typeList.add(new ColumnOverride("goods_ids" , ColumnOverride.TYPE.STRING));
        typeList.add(new ColumnOverride("pictures" , ColumnOverride.TYPE.STRING));
        return typeList;
    }

}


