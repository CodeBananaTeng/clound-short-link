package com.yulin.listener;

import com.rabbitmq.client.Channel;
import com.yulin.enums.BizCodeEnum;
import com.yulin.enums.EventMessageType;
import com.yulin.exception.BizException;
import com.yulin.model.EventMessage;
import com.yulin.service.ShortLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
@Component
@Slf4j
@RabbitListener(queuesToDeclare = {@Queue("short_link.update.mapping.queue")})
public class ShortLinkUpdateMappingMQListener {

    @Autowired
    private ShortLinkService shortLinkService;

    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息ShortLinkUpdateMappingMQListener message消息内容:{}",message);
        try {
            eventMessage.setEventMessageType(EventMessageType.SHORT_LINK_UPDATE_MAPPING.name());
            //进入业务逻辑B端更新短链
            shortLinkService.handleUpdateShortLink(eventMessage);
        }catch (Exception e){
            log.error("消费失败:{}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消费成功:{}",eventMessage);
    }

}
