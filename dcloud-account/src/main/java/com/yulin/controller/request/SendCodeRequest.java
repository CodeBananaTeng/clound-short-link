package com.yulin.controller.request;

import lombok.Data;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/17
 * @Description:
 */
@Data
public class SendCodeRequest {

    private String captcha;

    private String to;

}
