package com.yulin.listener;

import com.rabbitmq.client.Channel;
import com.yulin.enums.BizCodeEnum;
import com.yulin.exception.BizException;
import com.yulin.model.EventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
@Component
@Slf4j
@RabbitListener(queues = "short_link.add.mapping.queue")
public class ShortLinkAddMappingMQListener {

    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息ShortLinkAddMappingMQListener message消息内容:{}",message);
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            //TODO 处理业务逻辑
        }catch (Exception e){
            //处理业务异常，还有进行其他操作，比如记录失败原因
            log.error("消费失败:{}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消费成功:{}",eventMessage);
        //确认消息消费成功
        //channel.basicAck(tag,false);
    }

}
