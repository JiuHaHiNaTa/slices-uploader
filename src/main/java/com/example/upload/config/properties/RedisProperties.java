package com.example.upload.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis配置属性
 */
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    /**
     * 客户端名称
     */
    private static String clientName;

    /**
     * 端口
     */
    private static String port;

    /**
     * 主机地址
     */
    private static String host;

    /**
     * 校验密码
     */
    private static String password;

    /**
     * 默认连接数据库编号（0-16）
     */
    private static String database;

    /**
     * 最大连接数
     */
    private static Integer maxActive;

    /**
     * 最大空闲连接数
     */
    private static Integer maxIdle;

    /**
     * 最小空闲连接数
     */
    private static Integer minIdle;

    /**
     * 连接最大等待时长
     */
    private static Long maxWaitTime;

    /**
     * 连接超时时间
     */
    private static Long timeout;

    public static String getClientName() {
        return clientName;
    }

    @Value("${spring.redis.client-name}")
    public void setClientName(String clientName) {
        RedisProperties.clientName = clientName;
    }

    public static String getPort() {
        return port;
    }

    @Value("${spring.redis.port}")
    public void setPort(String port) {
        RedisProperties.port = port;
    }

    public static String getHost() {
        return host;
    }

    @Value("${spring.redis.host}")
    public void setHost(String host) {
        RedisProperties.host = host;
    }

    public static String getPassword() {
        return password;
    }

    @Value("${spring.redis.password}")
    public void setPassword(String password) {
        RedisProperties.password = password;
    }

    public static String getDatabase() {
        return database;
    }

    @Value("${spring.redis.database}")
    public void setDatabase(String database) {
        RedisProperties.database = database;
    }

    public static Integer getMaxActive() {
        return maxActive;
    }

    @Value("${spring.redis.jedis.pool.max-active}")
    public void setMaxActive(Integer maxActive) {
        RedisProperties.maxActive = maxActive;
    }

    public static Integer getMaxIdle() {
        return maxIdle;
    }

    @Value("${spring.redis.jedis.pool.max-idle}")
    public void setMaxIdle(Integer maxIdle) {
        RedisProperties.maxIdle = maxIdle;
    }

    public static Integer getMinIdle() {
        return minIdle;
    }

    @Value("${spring.redis.jedis.pool.min-idle}")
    public void setMinIdle(Integer minIdle) {
        RedisProperties.minIdle = minIdle;
    }

    public static Long getMaxWaitTime() {
        return maxWaitTime;
    }

    @Value("${spring.redis.jedis.pool.max-wait}")
    public void setMaxWaitTime(Long maxWaitTime) {
        RedisProperties.maxWaitTime = maxWaitTime;
    }

    public static Long getTimeout() {
        return timeout;
    }

    @Value("${spring.redis.timeout}")
    public void setTimeout(Long timeout) {
        RedisProperties.timeout = timeout;
    }

}
