package com.yulin.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther:LinuxTYL
 * @Date:2022/3/30
 * @Description:
 */
@Configuration
@Slf4j
@Data
public class RabbitMQConfig {

    /**
     * 消息转换器
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    //================流量包处理：⽤户初始化福利==================================
    /**
     * 交换机
     */
    private String trafficEventExchange = "traffic.event.exchange";

    /**
     * ⽤户注册 免费流量包新增 队列
     */
    private String trafficFreeInitQueue = "traffic.free_init.queue";

    /**
     * ⽤户注册 免费流量包新增 队列路由key
     *
     */
    private String trafficFreeInitRoutingKey = "traffic.free_init.routing.key";

    /**
     * 创建交换机 Topic类型
     * ⼀般⼀个微服务⼀个交换机
     * @return
     */
    @Bean
    public Exchange trafficEventExchange(){
        return new TopicExchange(trafficEventExchange,true,false);
    }

    /**
     * 队列的绑定关系建⽴:新⽤户注册免费流ᰁ包
     * @return
     */
    @Bean
    public Binding trafficFreeInitBinding(){
        return new Binding(trafficFreeInitQueue,Binding.DestinationType.QUEUE, trafficEventExchange,trafficFreeInitRoutingKey,null);
    }

    /**
     * 免费流量包队列
     */
    @Bean
    public Queue trafficFreeInitQueue(){
        return new Queue(trafficFreeInitQueue,true,false,false);
    }

}
