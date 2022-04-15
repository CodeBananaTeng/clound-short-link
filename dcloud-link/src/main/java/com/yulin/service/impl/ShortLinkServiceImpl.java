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

        //需要预先检查下是否有⾜够多的可以进⾏创建
        String cacheKey = String.format(RedisKey.DAY_TOTAL_TRAFFIC, accountNo);
        //检查key是否存在，然后递减，是否⼤于等于0，使⽤lua脚本
        // 如果key不存在，则未使⽤过，lua返回值是0； 新增流量包的时候，不⽤重新计算次数，直接删除key,消费的时候回计算更新
        String script = "if redis.call('get',KEYS[1]) then return redis.call('decr',KEYS[1]) else return 0 end";
        // "if redis.call('get',KEYS[1]) then return redis.call('decr',KEYS[1]) else return 0 end";
        Long leftTimes = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(cacheKey), "");
        log.info("今日流量包剩余次数:{}",leftTimes);
        if (leftTimes >= 0){
            //满足流量包次数够
            //发送消息之前的操作，将url处理为含有前缀的，这样的话同一个url生成的短链就会不同
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
     * //⽣成⻓链摘要
     * //判断短链域名是否合法
     * //判断组名是否合法
     * //⽣成短链码
     * //加锁（加锁再查，不然查询后，加锁前有线程刚好新增）
     * //查询短链码是否存在
     * //构建短链mapping对象
     * //保存数据库
     * 处理短链新增逻辑
     * @param eventMessage
     * @return
     */
    @Override
    public boolean handleAddShortLink(EventMessage eventMessage) {
        Long accountNo = eventMessage.getAccountNo();
        String messageType = eventMessage.getEventMessageType();

        ShortLinkAddRequest shortLinkAddRequest = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkAddRequest.class);
        //短链域名校验
        DomainDO domainDO = checkDomain(shortLinkAddRequest.getDomainType(), shortLinkAddRequest.getDomainId(), shortLinkAddRequest.getGroupId());
        //校验组是否合法
        LinkGroupDO linkGroupDO = checkGroup(shortLinkAddRequest.getGroupId(), accountNo);

        //长链摘要
        String originalUrlDigest = CommonUtil.MD5(shortLinkAddRequest.getOriginalUrl());

        //短链码重复标记
        boolean duplicateCodeFlag = false;
        //生成短链码
        String shortLinkCode = shortLinkComponent.createShortLinkCode(shortLinkAddRequest.getOriginalUrl());
        //TODO 加锁
        String script = "if redis.call('EXISTS',KEYS[1])==0 then redis.call('set',KEYS[1],ARGV[1]); redis.call('expire',KEYS[1],ARGV[2]); return 1;" +
                " elseif redis.call('get',KEYS[1]) == ARGV[1] then return 2;" +
                " else return 0; end;";
        Long result = redisTemplate.execute(new
                DefaultRedisScript<>(script, Long.class), Arrays.asList(shortLinkCode), accountNo,100);

        //加锁成功
        if (result > 0){

            //是C端的
            if (EventMessageType.SHORT_LINK_ADD_LINK.name().equalsIgnoreCase(messageType)){
                //判断短链码是否被占用
                ShortLinkDO shortLinkCodeDOInDB = shortLinkManager.findByShortLinkCode(shortLinkCode);
                if (shortLinkCodeDOInDB == null){
                    boolean reduceFlag = reduceTraffic(eventMessage,shortLinkCode);
                    if (reduceFlag){
                        //扣减成功创建短链
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
                    //短链码已经存在，需要重新生成
                    log.error("C端短链码存在:{}",eventMessage);
                    duplicateCodeFlag = true;
                }
            }else if (EventMessageType.SHORT_LINK_ADD_MAPPING.name().equalsIgnoreCase(messageType)){
                //判断B端短链码是否存在
                GroupCodeMappingDO groupCodeMappingDOInDB = groupCodeMappingManager.findByCodeAndGroupId(shortLinkCode,linkGroupDO.getId(),accountNo);
                if (groupCodeMappingDOInDB == null){
                    //进入B端处理逻辑
                    GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                            .accountNo(accountNo).code(shortLinkCode)
                            .title(shortLinkAddRequest.getTitle()).originalUrl(shortLinkAddRequest.getOriginalUrl())
                            .domain(domainDO.getValue()).groupId(linkGroupDO.getId())
                            .expired(shortLinkAddRequest.getExpireTime()).sign(originalUrlDigest)
                            .state(ShortLinkStateEnum.ACTIVE.name()).del(0).build();
                    groupCodeMappingManager.add(groupCodeMappingDO);
                    return true;
                }else {
                    //短链码已经存在，需要重新生成
                    log.error("B端短链码存在:{}",eventMessage);
                    duplicateCodeFlag = true;
                }
            }
        }else {
            //加锁失败，自旋100ms再来调用，可能是锻短链码已经占用了，需要重新生成
            log.error("加锁失败:{}",eventMessage);
            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { }
            duplicateCodeFlag = true;
        }
        if (duplicateCodeFlag){
            String newOriginalUrl = CommonUtil.addUrlPrefixVersion(shortLinkAddRequest.getOriginalUrl());
            shortLinkAddRequest.setOriginalUrl(newOriginalUrl);
            eventMessage.setContent(JsonUtil.obj2Json(shortLinkAddRequest));
            log.warn("短链码保存失败重新生成:{}",eventMessage);
            handleAddShortLink(eventMessage);
        }
        return false;
    }

    /**
     * 扣减流量包
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
            log.error("流量包不足，扣减失败:{}",eventMessage);
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
        //校验短链域名
        DomainDO domainDO = checkDomain(request.getDomainType(), request.getDomainId(), accountNo);
        if (EventMessageType.SHORT_LINK_UPDATE_LINK.name().equalsIgnoreCase(messageType)){
            //此处进入C端的逻辑
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .accountNo(accountNo)
                    .code(request.getCode())
                    .title(request.getTitle())
                    .domain(domainDO.getValue())
                    .build();

            int rows = shortLinkManager.update(shortLinkDO);
            log.debug("更新C端短链rows:{}",rows);
            return true;
        }else if (EventMessageType.SHORT_LINK_UPDATE_MAPPING.name().equalsIgnoreCase(messageType)){
            //此处进入B端的校验逻辑
            GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                    .id(request.getMappingId())
                    .groupId(request.getGroupId())
                    .accountNo(accountNo)
                    .title(request.getTitle())
                    .domain(domainDO.getValue())
                    .build();
            int rows = groupCodeMappingManager.update(groupCodeMappingDO);
            log.debug("更新B端短链rows:{}",rows);
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
            //此处进入C端的处理逻辑
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .accountNo(accountNo)
                    .code(request.getCode())
                    .build();
            int rows = shortLinkManager.del(shortLinkDO);
            log.debug("删除C端:{}",rows);
            return true;
        }else if (EventMessageType.SHORT_LINK_DEL_MAPPING.name().equalsIgnoreCase(messageType)){
            //此处进入B端的处理逻辑
            GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                    .id(request.getMappingId())
                    .accountNo(accountNo)
                    .groupId(request.getGroupId())
                    .build();
            int rows = groupCodeMappingManager.del(groupCodeMappingDO);
            log.debug("删除B端短链:{}",rows);
            return true;
        }
        return false;
    }

    /**
     * 从B端查找，group_code_mapping表
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
        //删除短链队列
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
        //更新操作的MQ
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),rabbitMQConfig.getShortLinkUpdateRoutingKey(),eventMessage);

        return JsonData.buildSuccess();
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
