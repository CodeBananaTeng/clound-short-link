package com.yulin.config;

import groovy.util.logging.Slf4j;
import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：LinuxTYL
 * @date ：Created in 16/2/2022 下午3:06
 *发送 关单消息-》延迟exchange-》order.close.delay.queue-》死信exchange-》order.close.queue
 */
@Configuration
@Slf4j
@Data
public class RabbitMQConfig {

    /**
     * 交换机
     */
    private String orderEventExchange = "order.event.exchange";

    /**
     * 延迟队列，不能被消费者监听
     */
    private String orderCloseDelayQueue = "order.close.delay.queue";

    /**
     * 关单队列，延迟消息过期之后转发的队列
     */
    private String orderCloseQueue = "order.close.queue";

    /**
     * 进入到延迟队列的routingkey
     */
    private String orderCloseDelayRoutingKey = "order.close.delay.routing.key";

    /**
     * 进入私信队列的routingkey，对应进入私信队列的routingkey
     */
    private String orderCloseRoutingKey = "order.close.routing.key";

    /**
     * 过期时间，毫秒为单位，临时改为1分钟过期
     */
    private Integer ttl = 1000 * 60;

    /**
     * 消息转换器
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建交换机，topic类型，一个业务一个交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){

        return new TopicExchange(orderEventExchange,true,false);

    }

    /**
     * 延迟队列
     * @return
     */
    @Bean
    public Queue orderCloseDelayQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",orderEventExchange);
        args.put("x-dead-letter-routing-key", orderCloseRoutingKey);
        args.put("x-message-ttl",ttl);
        return new Queue(orderCloseDelayQueue,true,false,false,args);
    }

    /**
     * 私信队列，是一个普通队列，用于被监听
     * @return
     */
    @Bean
    public Queue orderCloseQueue(){
        return new Queue(orderCloseQueue,true,false,false);
    }

    /**
     * 建立第一个延迟队列和交换机的绑定关系
     * @return
     */
    @Bean
    public Binding orderCloseDelayBinding(){
        return new Binding(orderCloseDelayQueue, Binding.DestinationType.QUEUE,orderEventExchange,orderCloseDelayRoutingKey,null);
    }

    /**
     * 死信队列和私信交换机建立绑定关系
     * @return
     */
    @Bean
    public Binding orderCloseBinding(){
        return new Binding(orderCloseQueue, Binding.DestinationType.QUEUE,orderEventExchange,orderCloseRoutingKey,null);
    }


}