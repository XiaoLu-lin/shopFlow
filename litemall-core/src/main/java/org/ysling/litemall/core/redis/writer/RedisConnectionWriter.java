package org.ysling.litemall.core.redis.writer;

import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * redis链接实体类
 * @author Ysling
 */
public class RedisConnectionWriter {

    /**租户ID*/
    private String tenantId;
    /**redis数据库索引*/
    private Integer database;
    /**redis链接工厂*/
    private RedisConnectionFactory connectionFactory;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public RedisConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
