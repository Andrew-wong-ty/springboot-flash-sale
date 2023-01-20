package com.project.concurrency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * redis store binary data, but we do not want it. thus serializing it
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // key序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 哈希类型的key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 哈希value序列化
        redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());

        //注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;

    }

    @Bean
    public DefaultRedisScript<Long> script(){
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        // lua脚本可以使得多个redis操作保持原子性
        defaultRedisScript.setLocation(new ClassPathResource("stock.lua"));
        defaultRedisScript.setResultType(Long.class);
        return defaultRedisScript; // 返回库存
    }
}
