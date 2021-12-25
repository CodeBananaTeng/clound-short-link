package com.yulin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/15
 * @Description:
 */

@MapperScan("com.yulin.mapper")
@EnableTransactionManagement
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
public class LinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinkApplication.class,args);
    }
}
