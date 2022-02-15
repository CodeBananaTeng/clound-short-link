package com.yulin.aspect;

import com.yulin.annotation.RepeatSubmit;
import com.yulin.constant.RedisKey;
import com.yulin.enums.BizCodeEnum;
import com.yulin.exception.BizException;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 *
 * 定义一个切面类
 * @Auther:LinuxTYL
 * @Date:2022/2/8
 * @Description:
 */
@Aspect
@Component
@Slf4j
public class RepeatSubmitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     *
     *
     * 定义 @Pointcut注解表达式，
     * ⽅式⼀：@annotation：当执⾏的⽅法上拥有指定的注解时⽣效（我们采⽤这）
     * ⽅式⼆：execution：⼀般⽤于指定⽅法的执⾏
     */
    @Pointcut("@annotation(repeatSubmit)")
    public void pointCutNoRepeatSubmit(RepeatSubmit repeatSubmit){

    }
    /**
     * 环绕通知, 围绕着⽅法执⾏
     * @Around 可以⽤来在调⽤⼀个具体⽅法前和调⽤后来完成⼀些具体的任务。
     *
     * ⽅式⼀：单⽤ @Around("execution(*net.xdclass.controller.*.*(..))")可以
     * ⽅式⼆：⽤@Pointcut和@Around联合注解也可以（我们采⽤这个）
     *
     *
     * 两种⽅式
     * ⽅式⼀：加锁 固定时间内不能᯿复提交
     * <p>
     * ⽅式⼆：先请求获取token，这边再删除token,删除成功则是第⼀次提交
     *
     * @param joinPoint
     * @param repeatSubmit
     * @return
     * @throws Throwable
     */
    @Around("pointCutNoRepeatSubmit(repeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint,RepeatSubmit repeatSubmit) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        //用于记录成功或者失败
        boolean res = false;

        //防重提交类型
        String type = repeatSubmit.limitType().name();

        if (type.equalsIgnoreCase(RepeatSubmit.Type.PARAM.name())){
            //方式一，参数形式防重提交 TODO
            long lockTime = repeatSubmit.lockTime();

            String ipAddr = CommonUtil.getIpAddr(request);
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            String className = method.getDeclaringClass().getName();
            //根据用户请求的地址，方法，类名，用户Id来创建key
            String key = String.format("%s-%s-%s-%s",ipAddr,className,method,accountNo);

            //加锁 TODO
            //res = redisTemplate.opsForValue().setIfAbsent(key,"1",lockTime, TimeUnit.SECONDS);
            RLock lock = redissonClient.getLock(key);
            res = lock.tryLock(0, lockTime, TimeUnit.SECONDS);

        }else {
            //方式二，令牌形式防重提交
            String requestToken = request.getHeader("request-token");
            if (StringUtils.isBlank(requestToken)){
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_EQUAL_FAIL);
            }
            String redisKey = "order-server:repeat_submit:"+CommonUtil.MD5(String.format(RedisKey.SUBMIT_ORDER_TOKEN_KEY, accountNo, requestToken)) ;

            /**
             * 提交表单的token key
             * ⽅式⼀：不⽤lua脚本获取再判断，之前是因为key组成是 order:submit:accountNo, value是对应的token，所以需要先获取值，再判断
             * ⽅式⼆：可以直接key是order:submit:accountNo:token,然后直接删除成功则完成
             */
            res = redisTemplate.delete(redisKey);
        }
        if (!res){
            //订单恶意重复提交 此处就是做一个防重提交的处理可以使抛出异常
            log.error("订单请求重复提交");
            return null;
        }
        log.info("环绕通知执行前");
        Object obj = joinPoint.proceed();
        log.info("环绕通知执行后");
        return obj;
    }
}
