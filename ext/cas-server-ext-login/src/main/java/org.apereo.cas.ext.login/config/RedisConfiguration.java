package org.apereo.cas.ext.login.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by wudongshen on 2017/2/17.
 */
@Configuration
@EnableConfigurationProperties(RedisConfig.class)
public class RedisConfiguration {

    @Autowired
    private RedisConfig redisConfig;

    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate() {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory conn = new JedisConnectionFactory();
        conn.setHostName(redisConfig.getHost());
        conn.setPort(redisConfig.getPort());
        conn.setTimeout(redisConfig.getTimeout());
        return conn;
    }
}
