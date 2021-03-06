package com.yulin.service.impl;

import com.yulin.component.ShortLinkComponent;
import com.yulin.config.RabbitMQConfig;
import com.yulin.constant.RedisKey;
import com.yulin.controller.request.*;
import com.yulin.enums.BizCodeEnum;
import com.yulin.enums.DomainTypeEnum;
import com.yulin.enums.EventMessageType;
import com.yulin.enums.ShortLinkStateEnum;
import com.yulin.feign.TrafficFeignService;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manager.DomainManager;
import com.yulin.manager.GroupCodeMappingManager;
import com.yulin.manager.LinkGroupManager;
import com.yulin.manager.ShortLinkManager;
import com.yulin.model.*;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private GroupCodeMappingManager groupCodeMappingManager;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private TrafficFeignService trafficFeignService;

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

        //????????????????????????????????????????????????????????????
        String cacheKey = String.format(RedisKey.DAY_TOTAL_TRAFFIC, accountNo);
        //??????key????????????????????????????????????????????????0?????????lua??????
        // ??????key??????????????????????????????lua????????????0??? ??????????????????????????????????????????????????????????????????key,??????????????????????????????
        String script = "if redis.call('get',KEYS[1]) then return redis.call('decr',KEYS[1]) else return 0 end";
        // "if redis.call('get',KEYS[1]) then return redis.call('decr',KEYS[1]) else return 0 end";
        Long leftTimes = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(cacheKey), "");
        log.info("???????????????????????????:{}",leftTimes);
        if (leftTimes >= 0){
            //????????????????????????
            //?????????????????????????????????url????????????????????????????????????????????????url???????????????????????????
            String newUrl = CommonUtil.addUrlPrefix(request.getOriginalUrl());
            request.setOriginalUrl(newUrl);

            EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                    .content(JsonUtil.obj2Json(request))
                    .messageId(IDUtil.geneSnowFlakeID().toString())
                    .eventMessageType(EventMessageType.SHORT_LINK_ADD.name())
                    .build();
            rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),rabbitMQConfig.getShortLinkAddRoutingKey(),eventMessage);
            return JsonData.buildSuccess();
        }else {
            return JsonData.buildResult(BizCodeEnum.TRAFFIC_REDUCE_FAIL);
        }

    }

    /**
     * //??????????????????
     * //??????????????????????????????
     * //????????????????????????
     * //???????????????
     * //???????????????????????????????????????????????????????????????????????????
     * //???????????????????????????
     * //????????????mapping??????
     * //???????????????
     * ????????????????????????
     * @param eventMessage
     * @return
     */
    @Override
    public boolean handleAddShortLink(EventMessage eventMessage) {
        Long accountNo = eventMessage.getAccountNo();
        String messageType = eventMessage.getEventMessageType();

        ShortLinkAddRequest shortLinkAddRequest = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkAddRequest.class);
        //??????????????????
        DomainDO domainDO = checkDomain(shortLinkAddRequest.getDomainType(), shortLinkAddRequest.getDomainId(), shortLinkAddRequest.getGroupId());
        //?????????????????????
        LinkGroupDO linkGroupDO = checkGroup(shortLinkAddRequest.getGroupId(), accountNo);

        //????????????
        String originalUrlDigest = CommonUtil.MD5(shortLinkAddRequest.getOriginalUrl());

        //?????????????????????
        boolean duplicateCodeFlag = false;
        //???????????????
        String shortLinkCode = shortLinkComponent.createShortLinkCode(shortLinkAddRequest.getOriginalUrl());
        //TODO ??????
        String script = "if redis.call('EXISTS',KEYS[1])==0 then redis.call('set',KEYS[1],ARGV[1]); redis.call('expire',KEYS[1],ARGV[2]); return 1;" +
                " elseif redis.call('get',KEYS[1]) == ARGV[1] then return 2;" +
                " else return 0; end;";
        Long result = redisTemplate.execute(new
                DefaultRedisScript<>(script, Long.class), Arrays.asList(shortLinkCode), accountNo,100);

        //????????????
        if (result > 0){

            //???C??????
            if (EventMessageType.SHORT_LINK_ADD_LINK.name().equalsIgnoreCase(messageType)){
                //??????????????????????????????
                ShortLinkDO shortLinkCodeDOInDB = shortLinkManager.findByShortLinkCode(shortLinkCode);
                if (shortLinkCodeDOInDB == null){
                    boolean reduceFlag = reduceTraffic(eventMessage,shortLinkCode);
                    if (reduceFlag){
                        //????????????????????????
                        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                                .accountNo(accountNo).code(shortLinkCode)
                                .title(shortLinkAddRequest.getTitle()).originalUrl(shortLinkAddRequest.getOriginalUrl())
                                .domain(domainDO.getValue()).groupId(linkGroupDO.getId())
                                .expired(shortLinkAddRequest.getExpireTime()).sign(originalUrlDigest)
                                .state(ShortLinkStateEnum.ACTIVE.name()).del(0).build();
                        shortLinkManager.addShortLink(shortLinkDO);
                    }
                    return true;
                }else {
                    //??????????????????????????????????????????
                    log.error("C??????????????????:{}",eventMessage);
                    duplicateCodeFlag = true;
                }
            }else if (EventMessageType.SHORT_LINK_ADD_MAPPING.name().equalsIgnoreCase(messageType)){
                //??????B????????????????????????
                GroupCodeMappingDO groupCodeMappingDOInDB = groupCodeMappingManager.findByCodeAndGroupId(shortLinkCode,linkGroupDO.getId(),accountNo);
                if (groupCodeMappingDOInDB == null){
                    //??????B???????????????
                    GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                            .accountNo(accountNo).code(shortLinkCode)
                            .title(shortLinkAddRequest.getTitle()).originalUrl(shortLinkAddRequest.getOriginalUrl())
                            .domain(domainDO.getValue()).groupId(linkGroupDO.getId())
                            .expired(shortLinkAddRequest.getExpireTime()).sign(originalUrlDigest)
                            .state(ShortLinkStateEnum.ACTIVE.name()).del(0).build();
                    groupCodeMappingManager.add(groupCodeMappingDO);
                    return true;
                }else {
                    //??????????????????????????????????????????
                    log.error("B??????????????????:{}",eventMessage);
                    duplicateCodeFlag = true;
                }
            }
        }else {
            //?????????????????????100ms????????????????????????????????????????????????????????????????????????
            log.error("????????????:{}",eventMessage);
            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { }
            duplicateCodeFlag = true;
        }
        if (duplicateCodeFlag){
            String newOriginalUrl = CommonUtil.addUrlPrefixVersion(shortLinkAddRequest.getOriginalUrl());
            shortLinkAddRequest.setOriginalUrl(newOriginalUrl);
            eventMessage.setContent(JsonUtil.obj2Json(shortLinkAddRequest));
            log.warn("?????????????????????????????????:{}",eventMessage);
            handleAddShortLink(eventMessage);
        }
        return false;
    }

    /**
     * ???????????????
     * @param eventMessage
     * @param shortLinkCode
     * @return
     */
    private boolean reduceTraffic(EventMessage eventMessage, String shortLinkCode) {
        UseTrafficRequest request = UseTrafficRequest.builder()
                .accountNo(eventMessage.getAccountNo())
                .bizId(shortLinkCode)
                .build();
        JsonData jsonData = trafficFeignService.useTraffic(request);
        if (jsonData.getCode() != 0){
            log.error("??????????????????????????????:{}",eventMessage);
        }else {
            return false;
        }
        return true;
    }

    @Override
    public boolean handleUpdateShortLink(EventMessage eventMessage) {
        long accountNo = eventMessage.getAccountNo();
        String messageType = eventMessage.getEventMessageType();
        ShortLinkUpdateRequest request = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkUpdateRequest.class);
        //??????????????????
        DomainDO domainDO = checkDomain(request.getDomainType(), request.getDomainId(), accountNo);
        if (EventMessageType.SHORT_LINK_UPDATE_LINK.name().equalsIgnoreCase(messageType)){
            //????????????C????????????
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .accountNo(accountNo)
                    .code(request.getCode())
                    .title(request.getTitle())
                    .domain(domainDO.getValue())
                    .build();

            int rows = shortLinkManager.update(shortLinkDO);
            log.debug("??????C?????????rows:{}",rows);
            return true;
        }else if (EventMessageType.SHORT_LINK_UPDATE_MAPPING.name().equalsIgnoreCase(messageType)){
            //????????????B??????????????????
            GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                    .id(request.getMappingId())
                    .groupId(request.getGroupId())
                    .accountNo(accountNo)
                    .title(request.getTitle())
                    .domain(domainDO.getValue())
                    .build();
            int rows = groupCodeMappingManager.update(groupCodeMappingDO);
            log.debug("??????B?????????rows:{}",rows);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleDelShortLink(EventMessage eventMessage) {
        Long accountNo = eventMessage.getAccountNo();
        String messageType = eventMessage.getEventMessageType();
        ShortLinkDelRequest request = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkDelRequest.class);

        if (EventMessageType.SHORT_LINK_DEL_LINK.name().equalsIgnoreCase(messageType)){
            //????????????C??????????????????
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .accountNo(accountNo)
                    .code(request.getCode())
                    .build();
            int rows = shortLinkManager.del(shortLinkDO);
            log.debug("??????C???:{}",rows);
            return true;
        }else if (EventMessageType.SHORT_LINK_DEL_MAPPING.name().equalsIgnoreCase(messageType)){
            //????????????B??????????????????
            GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                    .id(request.getMappingId())
                    .accountNo(accountNo)
                    .groupId(request.getGroupId())
                    .build();
            int rows = groupCodeMappingManager.del(groupCodeMappingDO);
            log.debug("??????B?????????:{}",rows);
            return true;
        }
        return false;
    }

    /**
     * ???B????????????group_code_mapping???
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> pageByGroupId(ShortLinkPageRequest request) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        Map<String, Object> result = groupCodeMappingManager.pageShortLinkByGroupId(request.getPage(), request.getSize(), loginUser.getAccountNo(), request.getGroupId());
        return result;
    }

    @Override
    public JsonData del(ShortLinkDelRequest request) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();



        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeID().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_DEL.name())
                .build();
        //??????????????????
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),rabbitMQConfig.getShortLinkDelRoutingKey(),eventMessage);

        return JsonData.buildSuccess();
    }

    @Override
    public JsonData update(ShortLinkUpdateRequest request) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();



        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeID().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_UPDATE.name())
                .build();
        //???????????????MQ
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),rabbitMQConfig.getShortLinkUpdateRoutingKey(),eventMessage);

        return JsonData.buildSuccess();
    }

    /**
     * ????????????
     * @param domainType
     * @param domainId
     * @param accountNo
     * @return
     */
    private DomainDO checkDomain(String domainType,Long domainId,Long accountNo){
        DomainDO domainDO = null;
        //??????????????????
        if (DomainTypeEnum.CUSTOM.name().equalsIgnoreCase(domainType)){
            domainDO = domainManager.findById(domainId,accountNo);
        }else {
            //???????????????????????????
            domainDO = domainManager.findByDomainTypeAndId(domainId,DomainTypeEnum.OFFICIAL);
        }
        Assert.notNull(domainDO,"?????????????????????");
        return domainDO;
    }

    /**
     * ????????????
     * @param groupId
     * @param accountNo
     * @return
     */
    private LinkGroupDO checkGroup(Long groupId,Long accountNo){
        LinkGroupDO linkGroupDO = linkGroupManager.detail(groupId, accountNo);
        Assert.notNull(linkGroupDO,"???????????????");
        return linkGroupDO;
    }

}
