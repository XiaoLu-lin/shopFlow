package org.ysling.litemall.core.system;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.system.annotation.SystemEnums;
import org.ysling.litemall.core.utils.JacksonUtil;
import org.ysling.litemall.db.domain.LitemallSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.db.service.ISystemService;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 先注入redisCacheService才注入当前类
 * 系统启动服务，用于设置系统配置信息、检查系统状态及打印系统信息
 * @author Ysling
 */
@Slf4j
@Component
public class SystemService implements ApplicationRunner {

    @Autowired
    private Environment environment;
    @Autowired
    private ISystemService systemService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("初始化 -> [系统配置类]");
        initConfig();
        getSystemInfo();
    }

    /**
     * 初始化系统配置信息
     */
    public void initConfig(){
        this.initDbSystem(getModelList());
        this.initRedisConfig();
        this.removeNotConfig();
    }

    /**
     * 初始化系统配置信息
     */
    public void initRedisConfig(){
        HashMap<String, LitemallSystem> dataMap = new HashMap<>();
        List<LitemallSystem> systemList = systemService.list();
        for (LitemallSystem system :systemList) {
            dataMap.put(system.getName(), system);
        }
        SystemUtils.updateConfigs(dataMap);
    }

    /**
     * 初始化数据库配置
     * @param systemList 配置列表
     */
    private void initDbSystem(List<LitemallSystem> systemList){
        for (LitemallSystem system : systemList) {
            saveOrUpdate(system);
        }
    }

    /**
     * 添加或更新系统配置
     * @param system    配置信息
     */
    private void saveOrUpdate(LitemallSystem system){
        QueryWrapper<LitemallSystem> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallSystem.NAME, system.getName());
        List<LitemallSystem> systemList = systemService.list(wrapper);
        if (systemList.isEmpty()){
            if (!systemService.save(system)){
                throw new RuntimeException("系统配置添加失败：" + system.getName());
            }
        }else {
            for (LitemallSystem item :systemList) {
                item.setValue(null);
                if (!systemService.updateById(item)){
                    throw new RuntimeException("系统配置更新失败：" + item.getName());
                }
            }
        }
    }

    /**
     * 删除常量枚举中没有的配置
     */
    public void removeNotConfig(){
        HashMap<String, Integer> dateMap = new HashMap<>();
        List<LitemallSystem> systemList = systemService.list();
        for (LitemallSystem system :systemList) {
            Integer count = dateMap.get(system.getName());
            dateMap.put(system.getName(), count == null ? 1 : count + 1);
            String config = SystemUtils.getConfig(system.getName());
            if (!StringUtils.hasText(config) || count != null){
                systemService.removeById(system.getId());
            }
        }
    }

    /**
     * 获取被注解标记的枚举并转换成配置列表
     */
    public List<LitemallSystem> getModelList(){
        Set<Class<?>> classes = scanEntitiesWithAnnotation(SystemEnums.class);
        ArrayList<ConfigGroup> configGroups = new ArrayList<>();
        ArrayList<LitemallSystem> modelList = new ArrayList<>();
        for (Class<?> clazz :classes) {
            if (clazz.isEnum()){
                SystemEnums systemEnums = AnnotationUtils.findAnnotation(clazz, SystemEnums.class);
                if (systemEnums != null){
                    configGroups.add(new ConfigGroup(systemEnums.name(), systemEnums.prefix()));
                    Object[] enumConstants = clazz.getEnumConstants();
                    for (Object item :enumConstants) {
                        String json = JacksonUtil.toJson(item);
                        LitemallSystem system = JacksonUtil.parseObject(json, LitemallSystem.class);
                        if (system != null){
                            if (!StringUtils.hasText(system.getName())){
                                continue;
                            }
                            if (!StringUtils.hasText(system.getValue())){
                                continue;
                            }
                            if (!StringUtils.hasText(system.getDepict())){
                                continue;
                            }
                            modelList.add(system);
                        }
                    }
                }
            }
        }
        SystemUtils.setConfigGroups(configGroups);
        return modelList;
    }


    /**
     * 获取指定包下的注解标记类
     * @param annotationClass   注解类
     * @return  被注解标记的类列表
     */
    public Set<Class<?>> scanEntitiesWithAnnotation(Class<? extends Annotation> annotationClass) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        Set<Class<?>> annotatedClasses = new HashSet<>();
        for (BeanDefinition bd : scanner.findCandidateComponents("org.ysling.litemall")) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                annotatedClasses.add(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return annotatedClasses;
    }


    /**
     * 打印系统配置信息
     */
    private void getSystemInfo() {
        log.info("====================Litemall-plus 初始化信息-开始====================");
        log.info("初始化信息 -> [cpu核心数 -> {}]", Runtime.getRuntime().availableProcessors());
        log.info("初始化信息 -> [服务器端口 -> {}]", environment.getProperty("server.port"));
        log.info("====================Litemall-plus 初始化信息-结束====================");
        //解决druid 日志报错：discard long time none received connection:xxx
        System.setProperty("druid.mysql.usePingMethod", "false");
        //设置http请求超时时间30分钟（单位：毫秒）
        System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(30 * 60 * 1000));
        System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(30 * 60 * 1000));
    }


}
