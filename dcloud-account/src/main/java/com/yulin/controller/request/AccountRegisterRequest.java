package com.yulin.controller.request;

import lombok.Data;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/19
 * @Description:
 */
@Data
public class AccountRegisterRequest {

    private String headImg;

    /**
     * ⼿机号
     */
    private String phone;

    /**
     * 密码
     */
    private String pwd;



    /**
     * 邮箱
     */
    private String mail;

    /**
     * ⽤户名
     */
    private String username;

    /**
     * 短信验证码
     */
    private String code;

}
