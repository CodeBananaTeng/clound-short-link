package com.yulin.controller;

import com.yulin.service.NotifyService;
import com.yulin.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
@RestController
@RequestMapping("/api/account/v1")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    /**
     * 测试验证码发送接口，用于验证优化
     * @return
     */
    @GetMapping("/send_code")
    public JsonData sendCode(){
        notifyService.testSend();
        return JsonData.buildSuccess();
    }

}
