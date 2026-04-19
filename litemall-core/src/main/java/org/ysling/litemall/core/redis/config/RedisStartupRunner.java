package org.ysling.litemall.core.redis.config;
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

import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.stereotype.Component;
import org.ysling.litemall.core.redis.writer.RedisConnectionHolder;
import org.ysling.litemall.core.redis.writer.RedisConnectionWriter;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import org.ysling.litemall.db.domain.LitemallTenant;
import org.ysling.litemall.db.service.ITenantService;
import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

/**
 * redis连接工厂初始化类
 * 自动获取application.yml中的配置
 * @author Ysling
 */
@Slf4j
@Component
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisStartupRunner {

    @Autowired
    private RedisProperties properties;
    @Autowired
    private ITenantService tenantService;
    @Autowired
    private GenericObjectPoolConfig<Object> objectPoolConfig;
    @Autowired
    private RedisConnectionFactory connectionFactory;
    @Autowired
    private ClientResources clientResources;


    @PostConstruct
    public void init(){
        log.info("初始化 -> [初始化redis链接工厂]");
        //存储redis数据库索引对应的连接
        String defaultId = TenantContextHolder.getDefaultId();
        this.setRedisFactory(defaultId);
        //初始化租户redis索引
        List<LitemallTenant> tenantList = tenantService.list();
        for (LitemallTenant tenant : tenantList) {
            this.setRedisFactory(tenant.getId());
        }
    }

    /**
     * 添加redis链接库
     */
    public void setRedisFactory(String tenantId){
        int database = RedisConnectionHolder.redisFactorySize();
        RedisConnectionWriter connection = new RedisConnectionWriter();
        connection.setTenantId(tenantId);
        connection.setDatabase(database);
        if (TenantContextHolder.isDefaultId(tenantId)){
            connection.setConnectionFactory(connectionFactory);
        }else {
            connection.setConnectionFactory(redisConnectionFactory(database));
        }
        RedisConnectionHolder.setRedisFactory(tenantId, connection);
    }

    /**
     * 连接配置
     */
    public RedisStandaloneConfiguration redisConfiguration(Integer redisDatabase) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisDatabase);
        redisStandaloneConfiguration.setHostName(properties.getHost());
        redisStandaloneConfiguration.setPort(properties.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
        return redisStandaloneConfiguration;
    }

    /**
     * 租户redis链接工厂
     * @param redisDatabase redis索引
     * @return  redis链接工厂
     */
    public RedisConnectionFactory redisConnectionFactory(Integer redisDatabase) {
        log.info(String.format("初始化 -> [初始化redis链接工厂][redis库索引=%s]", redisDatabase));
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(Duration.ofSeconds(30)) //按照周期刷新拓扑
                .enableAllAdaptiveRefreshTriggers() //根据事件刷新拓扑
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                //redis命令超时时间,超时后才会使用新的拓扑信息重新建立连接
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(10)))
                .topologyRefreshOptions(topologyRefreshOptions)
                .build();

        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(objectPoolConfig)
                .clientResources(clientResources)
                .clientOptions(clusterClientOptions)
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration(redisDatabase), clientConfiguration);
        factory.afterPropertiesSet();
        factory.resetConnection();
        return factory;
    }



}
