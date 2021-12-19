package com.yulin.service;

import com.yulin.controller.request.AccountLoginRequest;
import com.yulin.controller.request.AccountRegisterRequest;
import com.yulin.utils.JsonData;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/15
 * @Description:
 */
public interface AccountService {
    /**
     * 用户注册
     * @param registerRequest
     * @return
     */
    JsonData register(AccountRegisterRequest registerRequest);

    /**
     * 用户登录
     * @return
     */
    JsonData login(AccountLoginRequest request);
}
