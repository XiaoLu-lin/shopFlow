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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.redis.lock.RedissonLocker;
import org.ysling.litemall.core.redis.util.LockUtil;
import org.ysling.litemall.core.redis.writer.CustomRedisCacheWriter;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import org.ysling.litemall.core.utils.BeanUtil;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/**
 * 自定义redis配置
 * 自动获取application.yml中的配置
 * @author Ysling
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnClass(RedisOperations.class)
public class RedisConfigurationSupport extends CachingConfigurerSupport {

    @Autowired
    private RedisProperties properties;

    /**
     * RedissonClient,单机模式
     */
    @Bean(name="redisson", destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + properties.getHost() + ":" + properties.getPort());
        if (StringUtils.hasText(properties.getPassword())){
            config.useSingleServer().setPassword(properties.getPassword());
        }
        config.useSingleServer().setDatabase(properties.getDatabase());
        return Redisson.create(config);
    }

    /**
     * shutdown方法
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    public DefaultClientResources lettuceClientResources() {
        return DefaultClientResources.create();
    }


    /**
     * 注入redis分布式锁
     */
    @Bean
    public RedissonLocker redissonLocker(@Qualifier("redisson")RedissonClient redissonClient) {
        RedissonLocker locker = new RedissonLocker(redissonClient);
        LockUtil.setLocker(locker);
        return locker;
    }

    /**
     * GenericObjectPoolConfig 连接池配置
     */
    @Bean
    public GenericObjectPoolConfig<Object> objectPoolConfig() {
        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(properties.getLettuce().getPool().getMaxIdle());
        poolConfig.setMinIdle(properties.getLettuce().getPool().getMinIdle());
        poolConfig.setMaxTotal(properties.getLettuce().getPool().getMaxActive());
        return poolConfig;
    }

    /**
     连接配置
     */
    @Bean
    public RedisStandaloneConfiguration redisConfiguration() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(properties.getDatabase());
        redisStandaloneConfiguration.setHostName(properties.getHost());
        redisStandaloneConfiguration.setPort(properties.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
        return redisStandaloneConfiguration;
    }

    /**
     Lettuce 配置
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory(ClientResources clientResources) {
        log.info(String.format("初始化 -> [初始化redis链接工厂][redis库索引=%s]",properties.getDatabase()));
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
                .poolConfig(objectPoolConfig())
                .clientResources(clientResources)
                .clientOptions(clusterClientOptions)
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration(), clientConfiguration);
        factory.afterPropertiesSet();
        factory.resetConnection();
        return factory;
    }


    @Bean
    @Primary
    public CustomRedisCacheWriter redisCacheWriter(RedisConnectionFactory redisConnectionFactory) {
        return new CustomRedisCacheWriter(redisConnectionFactory);
    }

    /**
     * 当有多个管理器的时候，必须使用该注解在一个管理器上注释：表示该管理器为默认的管理器
     * @return 缓存
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(-1))
                .computePrefixWith(name -> name + ":");
        // 注入cacheManager
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromCacheWriter(redisCacheWriter(connectionFactory))
                .cacheDefaults(config)
                .build();
    }

    /**
     * 配置 一个 RedisTemplate bean
     * 为啥要加 @SuppressWarnings("all") :
     * 相传 Spring Boot 2.7 以上的版本 redisConnectionFactory 会爆红提示bean不存在，但其实是正常使用的
     * ps: 不要加 @ConditionalOnSingleCandidate 注解，会导致你自己定义的这个注解不会被注入
     *
     * @param redisConnectionFactory factory
     * @return RedisTemplate
     */
    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        RedisSerializer<String> redisSerializer = RedisSerializer.string();
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        return redisTemplate;
    }

    /**
     * 自定义缓存key生成策略，默认key生成策略
     * target: 类
     * method: 方法
     * params: 参数
     * @return KeyGenerator
     * 注意: 如果是自定义方法名不是  keyGenerator 则该方法只是声明了key的生成策略,
     * 还未被使用,需在@Cacheable注解中指定keyGenerator
     * 如: @Cacheable(value = "key", keyGenerator = "cacheKeyGenerator")
     */
    @Bean
    @Primary
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            //获取当前租户
            String tenantId = TenantContextHolder.getLocalTenantId();
            //重置redis连接
            BeanUtil.getBean(CustomRedisCacheWriter.class).setRedisDatabase(tenantId);
            //自定义key生成策略
            StringBuilder sb = new StringBuilder();
            sb.append(method.getName()).append(":");
            for (Object obj : params) {
                // 由于参数可能不同, hashCode肯定不一样, 缓存的key也需要不一样
                if (obj instanceof AbstractWrapper){
                    AbstractWrapper<?,?,?> wrapper = (AbstractWrapper<?,?,?>) obj;
                    wrapper.getCustomSqlSegment();
                    Map<String, Object> nameValuePairs = wrapper.getParamNameValuePairs();
                    for (Object value :nameValuePairs.values()) {
                        sb.append(JSON.toJSONString(value).hashCode());
                    }
                }else {
                    sb.append(JSON.toJSONString(obj).hashCode());
                }
            }
            return sb.toString();
        };
    }


    @Bean
    @Primary
    @Override
    public CacheErrorHandler errorHandler() {
        //异常处理，当Redis发生异常时，打印日志，但是程序正常走
        log.info("初始化 -> [Redis CacheErrorHandler]");
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(@Nonnull RuntimeException e,@Nonnull Cache cache,@Nonnull Object key) {
                log.error("Redis occur handleCacheGetError：key -> "+key, e);
            }

            @Override
            public void handleCachePutError(@Nonnull RuntimeException e,@Nonnull Cache cache,@Nonnull Object key, Object value) {
                log.error("Redis occur handleCachePutError：key -> "+key+"；value -> " +value, e);
            }

            @Override
            public void handleCacheEvictError(@Nonnull RuntimeException e,@Nonnull Cache cache,@Nonnull Object key) {
                log.error("Redis occur handleCacheEvictError：key -> "+key, e);
            }

            @Override
            public void handleCacheClearError(@Nonnull RuntimeException e,@Nonnull Cache cache) {
                log.error("Redis occur handleCacheClearError：", e);
            }
        };
    }

}
