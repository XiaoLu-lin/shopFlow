package org.ysling.shopflow.core.utils;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * json转换通用工具类
 */
@Slf4j
public class JacksonUtil {

    /**
     * 通用json对象
     */
    private static final ObjectMapper mapper;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        mapper = new ObjectMapper();
        //禁用时间戳
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //允许将空字符串反序列化为null对象，而不是引发异常
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        //通过禁用FAIL_ON_UNKNOWN_PROPERTIES特性，您可以配置ObjectMapper在遇到未知属性时不引发异常。
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //日期格式化
        JavaTimeModule module = new JavaTimeModule();
        //日期序列化
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATETIME_FORMATTER));
        //反序列化
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATETIME_FORMATTER));
        mapper.registerModule(module);
    }

    /**
     * 获取日期时间戳反序列化通用对象
     */
    public static ObjectMapper getTimestampMapper(){
        //日期格式化
        JavaTimeModule module = new JavaTimeModule();
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>(){
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
                return LocalDateTime.ofInstant(Instant.ofEpochSecond(p.getLongValue()), ZoneOffset.UTC);
            }
        });
        ObjectMapper mapper = getMapper();
        mapper.registerModule(module);
        return mapper;
    }

    /**
     * 获取通用序列化对象
     */
    public static ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * 获取通用序列化对象
     */
    private static final Gson gson = new GsonBuilder()
            .serializeNulls() // 序列化空字段
            .create();

    /**
     * json转泛型
     * @param json      json字符串
     * @param typeToken 泛型对象
     */
    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }


    /**
     * 获取json字符串中某个字段
     * @param body  json字符串
     * @param field 字段名
     */
    public static List<String> parseStringList(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return mapper.convertValue(leaf, new TypeReference<List<String>>() {});
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * 获取json字符串中某个字段
     * @param body  json字符串
     * @param field 字段名
     */
    public static List<Integer> parseIntegerList(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return mapper.convertValue(leaf, new TypeReference<List<Integer>>() {});
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取json字符串中某个字段
     * @param body json字符串
     * @param clazz 目标类
     */
    public static <T> List<T> parseList(String body, Class<T> clazz) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node != null && node.isArray()) {
                List<T> list = new ArrayList<>();
                for(JsonNode jsonNode : node){
                    list.add(mapper.treeToValue(jsonNode, clazz));
                }
                return list;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    /**
     * 获取json字符串中某个字段
     * @param body  json字符串
     * @param field 字段名
     */
    public static Integer parseInteger(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asInt();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取json字符串中某个字段
     * @param body  json字符串
     * @param field 字段名
     */
    public static String parseString(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asText();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取json字符串中某个字段
     * @param body  json字符串
     * @param field 字段名
     */
    public static Boolean parseBoolean(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asBoolean();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取json字符串中某个字段
     * @param body  json字符串
     * @param field 字段名
     */
    public static Short parseShort(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return (short) leaf.asInt();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取json字符串中某个字段
     * @param body  json字符串
     * @param field 字段名
     */
    public static Byte parseByte(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return (byte) leaf.asInt();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取json字符串中某个字段并转换成指定类型
     * @param body  json字符串
     * @param field 字段名
     * @param clazz 数据类型
     */
    public static <T> T parseObject(String body, String field, Class<T> clazz) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            node = node.get(field);
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取json字符串并转换成指定类型
     * @param body  json字符串
     * @param clazz 数据类型
     */
    public static <T> T parseObject(String body, Class<T> clazz) {
        try {
            return mapper.readValue(body, clazz);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取json字符串并转换成指定类型
     * @param body  json字符串
     * @param typeRef 带泛型的数据类型
     */
    public static <T> T parseObject(String body, TypeReference<T> typeRef) {
        try {
            return mapper.readValue(body, typeRef);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将字符串转成JsonNode
     * @param json json字符串
     */
    public static JsonNode toNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将json转成map
     * @param data json字符串
     */
    public static Map<String, String> toMap(String data) {
        try {
            return mapper.readValue(data, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将对象转为json字符串
     * @param data  对象
     */
    public static String toJson(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }



}