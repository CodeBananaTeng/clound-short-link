package com.yulin.listener;

import com.rabbitmq.client.Channel;
import com.yulin.enums.BizCodeEnum;
import com.yulin.exception.BizException;
import com.yulin.model.EventMessage;
import com.yulin.service.TrafficService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/1
 * @Description:
 */
@Component
@RabbitListener(queuesToDeclare = {
        @Queue("order.traffic.queue"),
        @Queue("traffic.free_init.queue")
})
@Slf4j
public class TrafficMQListener {

    @Autowired
    private TrafficService trafficService;

    @RabbitHandler
    public void trafficHandler(EventMessage eventMessage, Message message, Channel channel){

        log.info("监听消息trafficHandler:{}",eventMessage);

        try{
            trafficService.handleTrafficMessage(eventMessage);
        }catch (Exception e){
            log.error("消息消费失败:{}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消息消费成功:{}",eventMessage);
    }

}
