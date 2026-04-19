package org.ysling.litemall.core.redis.writer;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * redis链接存储工具类
 * @author Ysling
 */
public class RedisConnectionHolder {

    /**存储租户对应的redis数据库索引*/
    private static ConcurrentHashMap<String, RedisConnectionWriter> redisFactory = new ConcurrentHashMap<>();

    public static Integer redisFactorySize(){
        return redisFactory.size();
    }

    public static void setRedisFactory(ConcurrentHashMap<String, RedisConnectionWriter> redisFactory) {
        RedisConnectionHolder.redisFactory = redisFactory;
    }

    public static void setRedisFactory(String key , RedisConnectionWriter value) {
        RedisConnectionHolder.redisFactory.put(key, value);
    }

    public static void removeRedisFactory(String key) {
        RedisConnectionWriter writer = RedisConnectionHolder.redisFactory.get(key);
        if (writer != null){
            RedisConnectionFactory factory = writer.getConnectionFactory();
            RedisConnection connection = factory.getConnection();
            connection.close();
        }
        RedisConnectionHolder.redisFactory.remove(key);
    }

    public static RedisConnectionWriter getRedisFactory(String tenantId) {
        return redisFactory.get(tenantId);
    }

    public static ConcurrentHashMap<String, RedisConnectionWriter> getRedisFactory() {
        return redisFactory;
    }

}
