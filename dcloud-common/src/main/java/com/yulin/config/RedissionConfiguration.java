package com.yulin.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/2
 * @Description:
 */
@Configuration
@Slf4j
public class RedissionConfiguration {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.password}")
    private String redisPwd;


    /**
     * 配置分布式锁的redisson
     * @return
     */
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        //单机⽅式

        config.useSingleServer().setPassword(redisPwd).setAddress("redis://"+redisHost+":"+redisPort);
        //集群

//config.useClusterServers().addNodeAddress("redis:192.31.21.1:6379","redis://192.31.21.2:6379")
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
    /**
     * 集群模式
     * 备注：可以⽤"rediss://"来启⽤SSL连接
     */
// @Bean
// public RedissonClient redissonClusterClient() {
//    Config config = new Config();
//    config.useClusterServers().setScanInterval(2000) //集群状态扫描间隔时间，单位是毫秒
//            .addNodeAddress("redis://127.0.0.1:7000")
//            .addNodeAddress("redis://127.0.0.1:7002");
//    RedissonClient redisson = Redisson.create(config);
//    return redisson;
// }
//    // 分布式锁
//    RLock lock = redissonClient.getLock(key);
//    // 尝试加锁，最多等待2秒，上锁以后5秒⾃动解锁[lockTime默认为5s, 可以⾃定义]
//    res = lock.tryLock(2, lockTime,TimeUnit.SECONDS);


}
