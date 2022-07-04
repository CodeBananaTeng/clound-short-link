package com.yulin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkVisitStatsDO {

    /**
     * 窗⼝开始时间 Clickhouse⾥⾯会⽤到
     */
    String startTime;
    /**
     * 窗⼝结束时间
     */
    String endTime;
/**
 * ================================================
 */
    /**
     * 短链压缩码
     */
    private String code;
    /**
     * 租户id
     */
    private Long accountNo;
    /**
     * 访问时间
     */
    private Long visitTime;
    /**
     * 站点来源，只记录域名
     */
    private String referer;
    /**
     * 1是新访客，0是⽼访客
     */
    private Integer isNew;
    /**
     * 唯⼀标识
     */
    private String udid;
    //==============RegionInfoDO==================

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
    /**
     * 访问ip
     */
    private String ip;
//==============DeviceInfoDO==================
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
    //==============度量==================
    private Long uv=0L;
    private Long pv=0L;

}
