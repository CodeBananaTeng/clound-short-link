package com.yulin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/4
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogRecord {

    //客户端IP
    private String ip;

    //产生时间戳
    private Long ts;

    //日志事件类型
    private String event;

    //udid，是设备的唯一标识，全称uniqueDeviceIdentifier
    private String udid;

    //业务Id
    private String bizId;

    //日志内容
    private Object data;
}
