package com.ddpw.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 客户端类
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redissonClient() {
        // 配置类
        Config config = new Config();
        // 添加redis地址，这里添加了单点的地址，也可以使用config.useClusterServers()添加集群地址
        config.useSingleServer().setAddress("redis://192.168.247.130:6379").setPassword("dmlab@neu");
        // 创建客户端
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient2() {
        // 配置类
        Config config = new Config();
        // 添加redis地址，这里添加了单点的地址，也可以使用config.useClusterServers()添加集群地址
        config.useSingleServer().setAddress("redis://192.168.247.130:6379").setPassword("dmlab@neu");
        // 创建客户端
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient3() {
        // 配置类
        Config config = new Config();
        // 添加redis地址，这里添加了单点的地址，也可以使用config.useClusterServers()添加集群地址
        config.useSingleServer().setAddress("redis://192.168.247.130:6379").setPassword("dmlab@neu");
        // 创建客户端
        return Redisson.create(config);
    }
}
