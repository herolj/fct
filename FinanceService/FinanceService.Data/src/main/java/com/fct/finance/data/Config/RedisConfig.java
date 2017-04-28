package com.fct.finance.data.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by jon on 2017/4/7.
 */
@EnableAutoConfiguration
public class RedisConfig {

    @Bean
    public JedisPool redisConnectionFactory(Environment env) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(100);
        config.setMinIdle(1);
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);
        config.setTestOnReturn(false);
        config.setBlockWhenExhausted(true);
        config.setJmxEnabled(true);
        config.setJmxNamePrefix("jedis-pool");
        config.setNumTestsPerEvictionRun(100);
        config.setTimeBetweenEvictionRunsMillis(60000L);
        config.setMinEvictableIdleTimeMillis(300000L);
        config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
        config.setTimeBetweenEvictionRunsMillis(600000L);
        String host = env.getProperty("redis.host.url");
        Integer port = Integer.valueOf(env.getProperty("redis.host.port"));
        String password = env.getProperty("redis.host.password");
        return password == null?new JedisPool(config, host, port.intValue()):new JedisPool(config, host, port.intValue(), 2000, password);
    }
}
