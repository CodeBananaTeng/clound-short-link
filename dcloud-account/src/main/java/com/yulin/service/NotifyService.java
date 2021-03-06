package com.yulin.service;

import com.yulin.enums.SengCodeEnum;
import com.yulin.utils.JsonData;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
public interface NotifyService {
    /**
     * 发送注册验证码
     * @param userRegister
     * @param to
     * @return
     */
    JsonData sendCode(SengCodeEnum userRegister, String to);

    /**
     * 验证验证码的正确性
     * @return
     */
    boolean checkCode(SengCodeEnum userRegister, String to,String code);

}
