package com.yulin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/23
 * @Description:
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.yulin.mapper")
@EnableFeignClients
@EnableDiscoveryClient
public class ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class,args);
    }

}
 