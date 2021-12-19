package com.yulin.controller.request;

import lombok.Data;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/19
 * @Description:
 */
@Data
public class AccountLoginRequest {

    private String phone;

    private String pwd;

}
