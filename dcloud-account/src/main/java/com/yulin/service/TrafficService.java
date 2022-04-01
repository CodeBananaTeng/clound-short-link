package com.yulin.service;

import com.yulin.model.EventMessage;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/1
 * @Description:
 */
public interface TrafficService {

    /**
     * 消费传递过来的业务消息
     * @param eventMessage
     */
    void handleTrafficMessage(EventMessage eventMessage);

}
