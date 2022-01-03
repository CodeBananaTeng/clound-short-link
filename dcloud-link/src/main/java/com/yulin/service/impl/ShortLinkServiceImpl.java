package com.yulin.service.impl;

import com.yulin.config.RabbitMQConfig;
import com.yulin.controller.request.ShortLinkAddRequest;
import com.yulin.enums.EventMessageType;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manager.ShortLinkManager;
import com.yulin.model.EventMessage;
import com.yulin.model.ShortLinkDO;
import com.yulin.service.ShortLinkService;
import com.yulin.utils.IDUtil;
import com.yulin.utils.JsonData;
import com.yulin.utils.JsonUtil;
import com.yulin.vo.ShortLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/27
 * @Description:
 */
@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    private ShortLinkManager shortLinkManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Override
    public ShortLinkVO parseShortLinkCode(String shortLinkCode) {
        ShortLinkDO shortLinkDO = shortLinkManager.findByShortLinkCode(shortLinkCode);
        if (shortLinkDO == null){
            return null;
        }
        ShortLinkVO shortLinkVO = new ShortLinkVO();
        BeanUtils.copyProperties(shortLinkDO,shortLinkVO);
        return shortLinkVO;
    }

    @Override
    public JsonData createShortLink(ShortLinkAddRequest request) {

        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();


        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeID().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_ADD.name())
                .build();
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),rabbitMQConfig.getShortLinkAddRoutingKey(),eventMessage);

        return JsonData.buildSuccess();
    }

}
