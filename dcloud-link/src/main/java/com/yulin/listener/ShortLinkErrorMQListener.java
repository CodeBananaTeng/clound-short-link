package com.yulin.listener;

import com.rabbitmq.client.Channel;
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
@RabbitListener(queues = "short_link.error.queue")
public class ShortLinkErrorMQListener {

    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.error("告警：监听到消息ShortLinkAddLinkMQListener eventMessage消息内容:{}",eventMessage);
        log.error("告警：message消息内容:{}",message);
        log.error("告警成功发送通知短信");
    }

}
