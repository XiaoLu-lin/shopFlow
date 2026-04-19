package org.ysling.litemall.core.redis.cache;

import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.redis.util.Message;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * redis 缓存实现
 */
@Service
public class RedisCacheService {

    /**Redis操作的模板类*/
    private static RedisTemplate<Object,Object> redisTemplate;

    public RedisCacheService(RedisTemplate<Object, Object> redisTemplate){
        RedisCacheService.redisTemplate = redisTemplate;
    }

    /**
     * 获取 RedisConnection
     * @return RedisConnection
     */
    public static RedisConnection getConnection() {
        return redisTemplate.getRequiredConnectionFactory().getConnection();
    }

    /**
     * Get an item from the cache, nontransactionally
     *
     * @param key 缓存key
     * @return the cached object or <tt>null</tt>
     */
    public static Object get(Object key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Get an item from the cache, nontransactionally
     *
     * @param key 缓存key
     * @return the cached object or <tt>null</tt>
     */
    public static String getString(Object key) {
        try {
            return Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * multiGet
     *
     * @param keys 要查询的key集合
     * @return 集合
     */
    public static List<Object> multiGet(Collection<Object> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量set
     *
     * @param map 键值对
     */
    public static void multiSet(Map<Object,Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 批量删除
     *
     * @param keys 要删除的key集合
     */
    public static void multiDel(Collection<Object> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * Add an item to the cache, nontransactionally, with
     * failfast semantics
     *
     * @param key   缓存key
     * @param value 缓存value
     */
    public static void put(Object key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 往缓存中写入内容
     *
     * @param key   缓存key
     * @param value 缓存value
     * @param exp   超时时间，单位为秒
     */
    public static void put(Object key, Object value, Long exp) {
        put(key, value, exp, TimeUnit.SECONDS);
    }

    /**
     * 往缓存中写入内容
     *
     * @param key      缓存key
     * @param value    缓存value
     * @param exp      过期时间
     * @param timeUnit 过期单位
     */
    public static void put(Object key, Object value, Long exp, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, exp, timeUnit);
    }

    /**
     * 删除
     *
     * @param key 缓存key
     */
    public static Boolean remove(Object key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除
     *
     * @param key 缓存key
     */
    public static void vagueDel(Object key) {
        Set<Object> keys = redisTemplate.keys(key + "*");
        if (keys != null){
            redisTemplate.delete(keys);
        }
    }

    /**
     * Clear the cache
     */
    public static void clear() {
        Set<Object> keys = redisTemplate.keys("*");
        if (keys != null){
            redisTemplate.delete(keys);
        }
    }

    /**
     * 往缓存中写入内容
     *
     * @param key       缓存key
     * @param hashKey   缓存中hashKey
     * @param hashValue hash值
     */
    public static void putHash(Object key, Object hashKey, Object hashValue) {
        redisTemplate.opsForHash().put(key, hashKey, hashValue);
    }

    /**
     * 玩缓存中写入内容
     *
     * @param key 缓存key
     * @param map map value
     */
    public static void putAllHash(Object key, Map<Object,Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 读取缓存值
     *
     * @param key     缓存key
     * @param hashKey map value
     * @return 返回缓存中的数据
     */
    public static Object getHash(Object key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 读取缓存值
     *
     * @param key 缓存key
     * @return 缓存中的数据
     */
    public static Map<Object, Object> getHash(Object key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 是否包含
     *
     * @param key 缓存key
     * @return 缓存中的数据
     */
    public static boolean hasKey(Object key) {
        return redisTemplate.hasKey(key) != null;
    }

    /**
     * opsForStream它提供了一组操作来检索、发布和维护Redis Stream中的消息。
     * @return Stream操作连接
     */
    public static StreamOperations<Object, Object, Object> opsForStream(){
        return redisTemplate.opsForStream();
    }

    /**
     * 发送 Stream 消息。
     * @param message 消息对象
     */
    public static RecordId objectRecordCreate(String streamKey , Object message){
        // 将对象转为json
        String json = JSONUtil.toJsonStr(message);
        // 创建 Stream 消息
        ObjectRecord<Object, Object> record = ObjectRecord.create(streamKey, json);
        // 将消息添加至消息队列中
        return redisTemplate.opsForStream().add(record);
    }

    /**
     * 模糊匹配key
     *
     * @param pattern 模糊key
     * @return 符合条件的key
     */
    public static List<Object> keys(String pattern) {
        List<Object> keys = new ArrayList<>();
        scan(pattern, item -> {
            //符合条件的key
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }


    /**
     * scan 实现
     *
     * @param pattern  表达式
     * @param consumer 对迭代到的key进行操作
     */
    private static void scan(String pattern, Consumer<byte[]> consumer) {
        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                                 .count(Long.MAX_VALUE)
                                 .match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            }
        });
    }


    /**
     * 累计数目
     * 效率较高的 计数器
     * 如需清零，按照普通key 移除即可
     *
     * @param key   key值
     * @param value 去重统计值
     * @return 计数器结果
     */
    public static Long cumulative(Object key, Object value) {
        HyperLogLogOperations<Object, Object> operations = redisTemplate.opsForHyperLogLog();
        //add 方法对应 PFADD 命令
        return operations.add(key, value);
    }

    /**
     * 计数器结果
     * <p>
     * 效率较高的 计数器 统计返回
     * 如需清零，按照普通key 移除即可
     *
     * @param key 计数器key
     * @return 计数器结果
     */
    public static Long counter(Object key) {
        HyperLogLogOperations<Object, Object> operations = redisTemplate.opsForHyperLogLog();
        //add 方法对应 PFADD 命令
        return operations.size(key);
    }

    /**
     * 批量计数
     *
     * @param keys 要查询的key集合
     * @return 批量计数
     */
    public static List<Long> multiCounter(Collection<Object> keys) {
        if (keys == null) {
            return new ArrayList<>();
        }
        List<Long> result = new ArrayList<>();
        for (Object key : keys) {
            result.add(counter(key));
        }
        return result;
    }

    /**
     * 计数器结果
     * <p>
     * 效率较高的 计数器 统计返回
     * 如需清零，按照普通key 移除即可
     *
     * @param key key值
     * @return 计数器结果
     */
    public static Long mergeCounter(Object... key) {
        HyperLogLogOperations<Object, Object> operations = redisTemplate.opsForHyperLogLog();
        //计数器合并累加
        return operations.union(key[0], key);
    }

    /**
     * redis 计数器 累加
     * 注：到达liveTime之后，该次增加取消，即自动-1，而不是redis值为空
     *
     * @param key      为累计的key，同一key每次调用则值 +1
     * @param liveTime 单位秒后失效
     * @return 计数器结果
     */
    public static Long incr(String key, long liveTime) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        long increment = entityIdCounter.getAndIncrement();
        //初始设置过期时间
        if (increment == 0 && liveTime > 0) {
            entityIdCounter.expire(liveTime, TimeUnit.SECONDS);
        }
        return increment;
    }

    /**
     * redis 计数器 累加
     * 注：到达liveTime之后，该次增加取消，即自动-1，而不是redis值为空
     *
     * @param key      为累计的key，同一key每次调用则值 +1
     * @return 计数器结果
     */
    public static Long incr(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return entityIdCounter.getAndIncrement();
    }

    /**
     * 使用Sorted Set记录keyword
     * zincrby命令，对于一个Sorted Set，存在的就把分数加x(x可自行设定)，不存在就创建一个分数为1的成员
     *
     * @param sortedSetName sortedSetName的Sorted Set不用预先创建，不存在会自动创建，存在则向里添加数据
     * @param keyword       关键词
     */
    public static void incrementScore(String sortedSetName, String keyword) {
        //指向key名为KEY的zset元素
        redisTemplate.opsForZSet().incrementScore(sortedSetName, keyword, 1);
    }

    /**
     * 使用Sorted Set记录keyword
     * zincrby命令，对于一个Sorted Set，存在的就把分数加x(x可自行设定)，不存在就创建一个分数为1的成员
     *
     * @param sortedSetName sortedSetName的Sorted Set不用预先创建，不存在会自动创建，存在则向里添加数据
     * @param keyword       关键词
     * @param score         分数
     */
    public static void incrementScore(String sortedSetName, String keyword, Integer score) {
        redisTemplate.opsForZSet().incrementScore(sortedSetName, keyword, score);
    }

    /**
     * zrevrange命令, 查询Sorted Set中指定范围的值
     * 返回的有序集合中，score大的在前面
     * zrevrange方法无需担心用于指定范围的start和end出现越界报错问题
     *
     * @param sortedSetName sortedSetName
     * @param start         查询范围开始位置
     * @param end           查询范围结束位置
     * @return 符合排序的集合
     */
    public static Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String sortedSetName, Integer start, Integer end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(sortedSetName, start, end);
    }

    /**
     * zrevrange命令, 查询Sorted Set中指定范围的值
     * 返回的有序集合中，score大的在前面
     * zrevrange方法无需担心用于指定范围的start和end出现越界报错问题
     *
     * @param sortedSetName sortedSetName
     * @param count         获取数量
     * @return 符合排序的集合
     */
    public static Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String sortedSetName, Integer count) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(sortedSetName, 0, count);
    }


    /**
     * 向Zset里添加成员
     *
     * @param key   key值
     * @param score 分数，通常用于排序
     * @param value 值
     * @return 增加状态
     */
    public static boolean zAdd(String key, long score, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
    }


    /**
     * 获取 某key 下 某一分值区间的队列
     *
     * @param key  缓存key
     * @param from 开始时间
     * @param to   结束时间
     * @return 数据
     */
    public static Set<ZSetOperations.TypedTuple<Object>> zRangeByScore(String key, int from, long to) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, from, to);
    }

    /**
     * 移除 Zset队列值
     *
     * @param key   key值
     * @param value 删除的集合
     * @return 删除数量
     */
    public static Long zRemove(String key, String... value) {
        return redisTemplate.opsForZSet().remove(key, (Object) value);
    }

    /**
     * 主题订阅
     *
     * @param key 主题
     * @param message 消息
     */
    public static Long leftPush(String key, Message<?> message) {
        return redisTemplate.opsForList().leftPush(key, JSONUtil.toJsonStr(message));
    }

    /**
     * 广播消息
     *
     * @param key key
     * @param message 消息
     */
    public static void convertAndSend(String key, Message<?> message) {
        redisTemplate.convertAndSend(key, JSONUtil.toJsonStr(message));
    }
}
