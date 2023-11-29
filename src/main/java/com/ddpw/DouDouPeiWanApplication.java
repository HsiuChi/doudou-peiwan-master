package com.ddpw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.ddpw.mapper")
@SpringBootApplication
public class DouDouPeiWanApplication {

    public static void main(String[] args) {
        SpringApplication.run(DouDouPeiWanApplication.class, args);
    }

}
