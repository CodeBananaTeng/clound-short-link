package com.yulin.listener;

import com.rabbitmq.client.Channel;
import com.yulin.enums.BizCodeEnum;
import com.yulin.exception.BizException;
import com.yulin.model.EventMessage;
import com.yulin.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author ：LinuxTYL
 * @date ：Created in 16/2/2022 下午3:51
 */
@Component
@Slf4j
@RabbitListener(queuesToDeclare = {@Queue("order.close.queue")})
public class ProductOrderMQListener {

    @Autowired
    private ProductOrderService productOrderService;

    @RabbitHandler
    public void productOrderHandler(EventMessage eventMessage, Message message, Channel channel){
        log.info("监听到ProductOrderMQListener消息 message消息内容:{}",message);

        try {

            //业务逻辑 关闭订单 TODO
            boolean b = productOrderService.closeProductOrder(eventMessage);
        }catch (Exception e){
            log.error("消费失败:{}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消费成功:{}",eventMessage);
    }

}
