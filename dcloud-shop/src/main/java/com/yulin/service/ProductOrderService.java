package com.yulin.service;

import com.yulin.request.ConfirmOrderRequest;
import com.yulin.utils.JsonData;

import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/3
 * @Description:
 */
public interface ProductOrderService {

    Map<String, Object> page(int page, int size, String state);

    /**
     * 查询订单接口
     * @param outTradeNo
     * @return
     */
    String queryProductOrderState(String outTradeNo);

    JsonData confirmOrder(ConfirmOrderRequest orderRequest);

}
