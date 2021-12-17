package com.yulin.service.impl;

import com.yulin.enums.SengCodeEnum;
import com.yulin.service.NotifyService;
import com.yulin.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public JsonData sendCode(SengCodeEnum userRegister, String to) {
        return null;
    }
}
