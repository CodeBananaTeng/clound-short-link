package com.yulin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/12
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLinkWideAppDO {

    //===================短链业务本身信息=================

    /**
     * 短链码
     */
    private String code;

    /**
     * 账号
     */
    private Long accountNo;

    /**
     * 访问时间
     */
    private Long visitTime;

    /**
     * 站点来源（从那儿来的）只记录域名
     */
    private String referer;

    /**
     * 新老访客 1是新访客 0是就访客
     */
    private Integer isNew;

    /**
     * ip地址
     */
    private String ip;


    // ================设备相关字段==================
    /**
     * 浏览器名称
     */
    private String browserName;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 系统版本
     */
    private String osVersion;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备⼚商
     */
    private String deviceManufacturer;

    /**
     * ⽤户唯⼀标识
     */
    private String udid;

    //========================地理位置信息=============================
    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 运营商
     */
    private String isp;



}
