package com.yulin.service;

import com.yulin.controller.request.TrafficPageRequest;
import com.yulin.model.EventMessage;
import com.yulin.vo.TrafficVO;

import java.util.Map;

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

    /**
     * 查找可用的流量包
     * @param request
     * @return
     */
    Map<String, Object> pageAvailable(TrafficPageRequest request);

    /**
     * 查找VO
     * @param trafficId
     * @return
     */
    TrafficVO detail(Long trafficId);
}
