package org.ysling.litemall.core.redis.writer;
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

import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 自定义的RedisCacheWriter，重写了spring-data-redis中的 CustomRedisCacheWriter。原因是后者在清理缓存时使用了redis的keys命令。
 * 该命令存在安全风险，因此，我们重写了这个类，对 clean 方法进行重写，该用scan命令进行实现。
 *
 * @Author: ysling
 */
public class CustomRedisCacheWriter extends RedisConnectionHolder implements RedisCacheWriter {

    private RedisConnectionFactory connectionFactory;
    private final Duration sleepTime;
    private final CacheStatisticsCollector statistics;

    public CustomRedisCacheWriter(RedisConnectionFactory connectionFactory) {
        this(connectionFactory, Duration.ZERO);
    }

    CustomRedisCacheWriter(RedisConnectionFactory connectionFactory, Duration sleepTime) {
        this(connectionFactory, sleepTime, CacheStatisticsCollector.none());
    }

    CustomRedisCacheWriter(RedisConnectionFactory connectionFactory, Duration sleepTime, CacheStatisticsCollector cacheStatisticsCollector) {
        Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");
        Assert.notNull(sleepTime, "SleepTime must not be null!");
        Assert.notNull(cacheStatisticsCollector, "CacheStatisticsCollector must not be null!");
        this.connectionFactory = connectionFactory;
        this.sleepTime = sleepTime;
        this.statistics = cacheStatisticsCollector;
    }

    /**
     * 重置链接
     * @param tenantId 租户ID
     */
    public void setRedisDatabase(String tenantId) {
        if (tenantId == null) {
            return;
        }
        //根据租户id获取对应redis索引
        RedisConnectionWriter connectionWriter = getRedisFactory(tenantId);
        if (connectionWriter == null) {
            connectionWriter = getRedisFactory(TenantContextHolder.getDefaultId());
            if (connectionWriter == null){
                return;
            }
        }
        // 获取LettuceConnectionFactory连接工厂
        LettuceConnectionFactory factory = (LettuceConnectionFactory) connectionFactory;
        // 判断当前索引是否和需要设置的索引一致如果一致则不改变索引
        if (Objects.equals(factory.getDatabase(), connectionWriter.getDatabase())){
            return;
        }
        //根据redis索引获取连接工厂
        RedisConnectionFactory connectionFactory = connectionWriter.getConnectionFactory();
        Assert.notNull(connectionFactory , "Factory must not be null!");
        this.connectionFactory = connectionFactory;
    }

    /**
     * 添加缓存
     * @param name  前缀
     * @param key   key
     * @param value 值
     * @param ttl   时间
     */
    @Override
    public void put(@Nonnull String name, @Nonnull byte[] key, @Nonnull byte[] value, @Nullable Duration ttl) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(value, "Value must not be null!");
        this.execute(name, (connection) -> {
            if (shouldExpireWithin(ttl)) {
                connection.set(key, value, Expiration.from(ttl.toMillis(), TimeUnit.MILLISECONDS), SetOption.upsert());
            } else {
                connection.set(key, value);
            }
            return "OK";
        });
        this.statistics.incPuts(name);
    }

    /**
     * 获取key
     * @param name 前缀
     * @param key key
     * @return bete
     */
    @Override
    public byte[] get(@Nonnull String name, @Nonnull byte[] key) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        byte[] result = this.execute(name, (connection) -> connection.get(key));
        this.statistics.incGets(name);
        if (result != null) {
            this.statistics.incHits(name);
        } else {
            this.statistics.incMisses(name);
        }
        return result;
    }


    /**
     * putIfAbsent是根据内部的分布式锁来实现的
     * @param name  前缀
     * @param key   key
     * @param value value
     * @param ttl   时间
     * @return      byte
     */
    @Override
    public byte[] putIfAbsent(@Nonnull String name, @Nonnull byte[] key, @Nonnull byte[] value, @Nullable Duration ttl) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(value, "Value must not be null!");
        return this.execute(name, (connection) -> {
            if (this.isLockingCacheWriter()) {
                this.doLock(name, connection);
            }
            byte[] var7;
            try {
                boolean put;
                if (shouldExpireWithin(ttl)) {
                    put = Boolean.TRUE.equals(connection.set(key, value, Expiration.from(ttl), SetOption.ifAbsent()));
                } else {
                    put = Boolean.TRUE.equals(connection.setNX(key, value));
                }
                if (put) {
                    this.statistics.incPuts(name);
                    return null;
                }
                var7 = connection.get(key);
            } finally {
                if (this.isLockingCacheWriter()) {
                    this.doUnlock(name, connection);
                }
            }
            return var7;
        });
    }

    /**
     * 删除单个key
     * @param name 前缀
     * @param key  key
     */
    @Override
    public void remove(@Nonnull String name, @Nonnull byte[] key) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        this.execute(name, (connection) -> connection.del(new byte[][]{key}));
        this.statistics.incDeletes(name);
    }

    /**
     * 清除租户所有缓存
     * @param tenantId 租户ID
     */
    public void cleanTenant(String tenantId) {
        Assert.notNull(tenantId, "tenantId must not be null!");
        //如果是默认租户更新数据则清除所有连接的key
        setRedisDatabase(tenantId);
        this.getScanExecute("*", "*".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 批量删除key
     * 自定义使用scan命令代替原本的keys命令搜索key
     * @param name 前置
     * @param pattern 参数
     */
    @Override
    public void clean(@Nonnull String name, @Nonnull byte[] pattern) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(pattern, "Pattern must not be null!");
        this.getScanExecute(name, pattern);
    }
    

    /**
     * 批量删除key
     * @param name 前置
     * @param pattern 参数
     */
    private void getScanExecute(String name, byte[] pattern) {
        this.execute(name, (connection) -> {
            boolean wasLocked = false;
            try {
                if (this.isLockingCacheWriter()) {
                    this.doLock(name, connection);
                    wasLocked = true;
                }
                // 使用scan命令代替原本的keys命令搜索key
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                        .match(new String(pattern))
                        .count(1000).build());

                Set<byte[]> byteSet = new HashSet<>();
                while (cursor.hasNext()) {
                    byteSet.add(cursor.next());
                }

                byte[][] keys = byteSet.toArray(new byte[0][]);

                if (keys.length > 0) {
                    this.statistics.incDeletesBy(name, keys.length);
                    connection.del(keys);
                }
            } finally {
                if (wasLocked && this.isLockingCacheWriter()) {
                    this.doUnlock(name, connection);
                }
            }
            return "OK";
        });
    }

    /**
     * 拿到redis连接，执行命令。 当然执行之前，去获取锁
     */
    private <T> T execute(String name, Function<RedisConnection, T> callback) {
        //获取redis连接
        RedisConnection connection = connectionFactory.getConnection();
        T var4;
        try {
            this.checkAndPotentiallyWaitUntilUnlocked(name, connection);
            var4 = callback.apply(connection);
        } finally {
            connection.close();
        }
        return var4;
    }

    /**
     * 前缀锁
     * @param name 前缀
     * @param connection 链接
     */
    private void checkAndPotentiallyWaitUntilUnlocked(String name, RedisConnection connection) {
        if (this.isLockingCacheWriter()) {
            long lockWaitTimeNs = System.nanoTime();
            try {
                // 自旋：去查看锁 若为true（表示锁还在）
                while(this.doCheckLock(name, connection)) {
                    Thread.sleep(this.sleepTime.toMillis());
                }
            } catch (InterruptedException var9) {
                Thread.currentThread().interrupt();
                throw new PessimisticLockingFailureException(String.format("Interrupted while waiting to unlock cache %s", name), var9);
            } finally {
                this.statistics.incLockTime(name, System.nanoTime() - lockWaitTimeNs);
            }

        }
    }

    @Nonnull
    @Override
    public CacheStatistics getCacheStatistics(@Nonnull String cacheName) {
        return this.statistics.getCacheStatistics(cacheName);
    }

    @Override
    public void clearStatistics(@Nonnull String name) {
        this.statistics.reset(name);
    }

    @Nonnull
    @Override
    public RedisCacheWriter withStatisticsCollector(@Nonnull CacheStatisticsCollector cacheStatisticsCollector) {
        return new CustomRedisCacheWriter(connectionFactory, this.sleepTime, cacheStatisticsCollector);
    }

    private Boolean doLock(String name, RedisConnection connection) {
        return connection.setNX(createCacheLockKey(name), new byte[0]);
    }

    private Long doUnlock(String name, RedisConnection connection) {
        return connection.del(new byte[][]{createCacheLockKey(name)});
    }

    boolean doCheckLock(String name, RedisConnection connection) {
        return Boolean.TRUE.equals(connection.exists(createCacheLockKey(name)));
    }

    private boolean isLockingCacheWriter() {
        return !this.sleepTime.isZero() && !this.sleepTime.isNegative();
    }

    private static boolean shouldExpireWithin(@Nullable Duration ttl) {
        return ttl != null && !ttl.isZero() && !ttl.isNegative();
    }

    private static byte[] createCacheLockKey(String name) {
        return (name + "~lock").getBytes(StandardCharsets.UTF_8);
    }
}
