package com.yulin.service.impl;

import com.yulin.component.SmsComponent;
import com.yulin.config.SmsConfig;
import com.yulin.constant.RedisKey;
import com.yulin.enums.BizCodeEnum;
import com.yulin.enums.SengCodeEnum;
import com.yulin.service.NotifyService;
import com.yulin.utils.CheckUtil;
import com.yulin.utils.CommonUtil;
import com.yulin.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;


/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    /**
     * 十分钟有效
     */
    private static final int CODE_EXPIRED = 60 * 1000 * 10;

    @Autowired
    private SmsComponent smsComponent;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsConfig smsConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public JsonData sendCode(SengCodeEnum sengCodeEnum, String to) {

        String cacheKey = String.format(RedisKey.CHECK_CODE_KEY,sengCodeEnum.name(),to);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);

        //如果不为空,在判断是否为60s内重复发送
        if (StringUtils.isNoneBlank(cacheValue)){
            long ttl = Long.parseLong(cacheValue.split("_")[1]);
            //判断当前时间戳-发送的时间戳 <60就不发送
            long leftTime = CommonUtil.getCurrentTimestamp() - ttl;
            if ( leftTime < 60000){
                log.info("重复发送短信验证码，时间间隔:{}秒",leftTime);
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }
        //生成拼接好验证码
        String code = CommonUtil.getRandomCode(6);
        String value = code+"_"+CommonUtil.getCurrentTimestamp();
        redisTemplate.opsForValue().set(cacheKey,value,CODE_EXPIRED, TimeUnit.MILLISECONDS);

        if (CheckUtil.isEmail(to)){
            //是邮箱，就发送邮箱验证码 TODO
        }else if (CheckUtil.isPhone(to)){
            //发送手机验证码
            smsComponent.send(to, smsConfig.getTemplateId(),code);

        }

        return JsonData.buildSuccess();
    }
}
