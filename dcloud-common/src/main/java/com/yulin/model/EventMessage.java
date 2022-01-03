package com.yulin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventMessage implements Serializable {

    /**
     * 消息队列的id
     */
    private String messageId;

    /**
     * 事件类型
     */
    private String eventMessageType;

    /**
     * 业务Id
     */
    private String bizId;

    /**
     * 账号
     */
    private Long accountNo;

    /**
     * 消息体
     */
    private String content;

    /**
     * 备注：处理异常之类的消息的时候
     */
    private String remark;
}
