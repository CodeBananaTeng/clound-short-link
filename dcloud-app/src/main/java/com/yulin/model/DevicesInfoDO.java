package com.yulin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/11
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DevicesInfoDO {

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

}
