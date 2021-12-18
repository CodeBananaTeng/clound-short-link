package com.yulin.service;

import com.yulin.controller.request.AccountRegisterRequest;
import com.yulin.utils.JsonData;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/15
 * @Description:
 */
public interface AccountService {
    JsonData register(AccountRegisterRequest registerRequest);

}
