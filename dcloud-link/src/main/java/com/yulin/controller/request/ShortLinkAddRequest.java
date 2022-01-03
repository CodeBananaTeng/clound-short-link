package com.yulin.controller.request;

import lombok.Data;

import java.util.Date;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
@Data
public class ShortLinkAddRequest {

    /**
     * 组
     */
    private Long groupId;

    /**
     * 短链标题
     */
    private String title;

    /**
     * 原始URL
     */
    private String originalUrl;

    /**
     * 域名Id
     */
    private Long domainId;

    /**
     * 域名类型
     */
    private String domainType;

    /**
     * 过期时间
     */
    private Date expireTime;

}
