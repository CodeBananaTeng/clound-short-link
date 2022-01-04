package com.yulin.service.impl;

import com.yulin.component.ShortLinkComponent;
import com.yulin.config.RabbitMQConfig;
import com.yulin.controller.request.ShortLinkAddRequest;
import com.yulin.enums.DomainTypeEnum;
import com.yulin.enums.EventMessageType;
import com.yulin.enums.ShortLinkStateEnum;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manager.DomainManager;
import com.yulin.manager.LinkGroupManager;
import com.yulin.manager.ShortLinkManager;
import com.yulin.mapper.LinkGroupMapper;
import com.yulin.model.DomainDO;
import com.yulin.model.EventMessage;
import com.yulin.model.LinkGroupDO;
import com.yulin.model.ShortLinkDO;
import com.yulin.service.ShortLinkService;
import com.yulin.utils.CommonUtil;
import com.yulin.utils.IDUtil;
import com.yulin.utils.JsonData;
import com.yulin.utils.JsonUtil;
import com.yulin.vo.ShortLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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

    @Autowired
    private DomainManager domainManager;

    @Autowired
    private LinkGroupManager linkGroupManager;

    @Autowired
    private ShortLinkComponent shortLinkComponent;



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

    /**
     * 处理短链新增逻辑
     * @param eventMessage
     * @return
     */
    @Override
    public boolean handlerAddShortLink(EventMessage eventMessage) {
        Long accountNo = eventMessage.getAccountNo();
        String messageType = eventMessage.getEventMessageType();

        ShortLinkAddRequest shortLinkAddRequest = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkAddRequest.class);
        //短链域名校验
        DomainDO domainDO = checkDomain(shortLinkAddRequest.getDomainType(), shortLinkAddRequest.getDomainId(), shortLinkAddRequest.getGroupId());
        //校验组是否合法
        LinkGroupDO linkGroupDO = checkGroup(shortLinkAddRequest.getGroupId(), accountNo);

        //长链摘要
        String originalUrlDigest = CommonUtil.MD5(shortLinkAddRequest.getOriginalUrl());
        //生成短链码
        String shortLinkCode = shortLinkComponent.createShortLinkCode(shortLinkAddRequest.getOriginalUrl());

        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .accountNo(accountNo)
                .code(shortLinkCode)
                .title(shortLinkAddRequest.getTitle())
                .originalUrl(shortLinkAddRequest.getOriginalUrl())
                .domain(domainDO.getValue())
                .groupId(linkGroupDO.getId())
                .expired(shortLinkAddRequest.getExpireTime())
                .sign(originalUrlDigest)
                .state(ShortLinkStateEnum.ACTIVE.name())
                .del(0)
                .build();
        shortLinkManager.addShortLink(shortLinkDO);
        return true;
    }

    /**
     * 校验域名
     * @param domainType
     * @param domainId
     * @param accountNo
     * @return
     */
    private DomainDO checkDomain(String domainType,Long domainId,Long accountNo){
        DomainDO domainDO = null;
        //是否为自建的
        if (DomainTypeEnum.CUSTOM.name().equalsIgnoreCase(domainType)){
            domainDO = domainManager.findById(domainId,accountNo);
        }else {
            //如果不是就是官方的
            domainDO = domainManager.findByDomainTypeAndId(domainId,DomainTypeEnum.OFFICIAL);
        }
        Assert.notNull(domainDO,"短链域名不合法");
        return domainDO;
    }

    /**
     * 校验组名
     * @param groupId
     * @param accountNo
     * @return
     */
    private LinkGroupDO checkGroup(Long groupId,Long accountNo){
        LinkGroupDO linkGroupDO = linkGroupManager.detail(groupId, accountNo);
        Assert.notNull(linkGroupDO,"组名不合法");
        return linkGroupDO;
    }
}
