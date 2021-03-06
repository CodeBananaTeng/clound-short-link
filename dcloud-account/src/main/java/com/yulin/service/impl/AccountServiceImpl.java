package com.yulin.service.impl;

import com.yulin.config.RabbitMQConfig;
import com.yulin.controller.request.AccountLoginRequest;
import com.yulin.controller.request.AccountRegisterRequest;
import com.yulin.enums.AuthTypeEnum;
import com.yulin.enums.BizCodeEnum;
import com.yulin.enums.EventMessageType;
import com.yulin.enums.SengCodeEnum;
import com.yulin.manager.AccountManage;
import com.yulin.model.AccountDO;
import com.yulin.model.EventMessage;
import com.yulin.model.LoginUser;
import com.yulin.service.AccountService;
import com.yulin.service.NotifyService;
import com.yulin.utils.CommonUtil;
import com.yulin.utils.IDUtil;
import com.yulin.utils.JWTUtil;
import com.yulin.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/15
 * @Description:
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private AccountManage accountManage;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    /**
     * 免费流量包商品ID固定为1
     */
    private static final Long FREE_TRAFFIC_PRODUCT_ID = 1L;

    /**
     * ⼿机验证码验证
     * 密码加密（TODO）
     * 账号唯⼀性检查(TODO)
     * 插⼊数据库
     * 新注册⽤户福利发放(TODO)
     * @param registerRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public JsonData register(AccountRegisterRequest registerRequest) {
        //手机验证码，判断验证码是否false
        boolean checkCode = false;
        if (StringUtils.isNoneBlank(registerRequest.getPhone())){
            checkCode = notifyService.checkCode(SengCodeEnum.USER_REGISTER, registerRequest.getPhone(), registerRequest.getCode());

        }
        //验证码错误
        if (!checkCode){
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }
        AccountDO accountDO = new AccountDO();
        BeanUtils.copyProperties(registerRequest,accountDO);
        //设置认证级别，刚刚注册就是默认的
        accountDO.setAuth(AuthTypeEnum.DEFAULT.name());

        //生成唯一的账号 TODO
        //accountDO.setAccountNo(CommonUtil.getCurrentTimestamp());
        accountDO.setAccountNo(Long.valueOf(IDUtil.geneSnowFlakeID().toString()));

        //设置密码,秘钥 盐
        accountDO.setSecret("$1$" + CommonUtil.getStringNumRandom(8));

        String cryptPwd = Md5Crypt.md5Crypt(registerRequest.getPwd().getBytes(),accountDO.getSecret());
        accountDO.setPwd(cryptPwd);
        int rows = accountManage.insert(accountDO);
        log.info("rows:{},注册成功",rows);

        //用户注册成功发放福利 TODO
        userRegisterInitTask(accountDO);

        return JsonData.buildSuccess();
    }

    /**
     * 这个密码加上盐后的密文是否匹配
     * 1，根据手机号查看是否有记录
     * 2，
     * 用户注册
     * @return
     */
    @Override
    public JsonData login(AccountLoginRequest request) {

        List<AccountDO> accountDOList = accountManage.findByPhone(request.getPhone());

        if(accountDOList!=null && accountDOList.size() ==1){

            AccountDO accountDO = accountDOList.get(0);

            String md5Crypt = Md5Crypt.md5Crypt(request.getPwd().getBytes(), accountDO.getSecret());
            if(md5Crypt.equalsIgnoreCase(accountDO.getPwd())){

                LoginUser loginUser = LoginUser.builder().build();
                BeanUtils.copyProperties(accountDO,loginUser);


                String token = JWTUtil.geneJsonWebToken(loginUser);

                return JsonData.buildSuccess(token);

            }else {
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
            }


        }else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER);
        }


    }

    /**
     * 用户初始化发放流量包
     * @param request
     */
    private JsonData userRegisterInitTask(AccountDO request) {
        EventMessage build = EventMessage.builder()
                .messageId(IDUtil.geneSnowFlakeID().toString())
                .accountNo(request.getAccountNo())
                .eventMessageType(EventMessageType.TRAFFIC_FREE_INIT.name())
                .bizId(FREE_TRAFFIC_PRODUCT_ID.toString())
                .build();
        //发放流量包消息
        rabbitTemplate.convertAndSend(rabbitMQConfig.getTrafficEventExchange(),rabbitMQConfig.getTrafficFreeInitRoutingKey(),build);
        return null;
    }

}
