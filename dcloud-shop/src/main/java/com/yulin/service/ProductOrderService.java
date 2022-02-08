package com.yulin.service;

import com.yulin.controller.request.ConfirmOrderRequest;
import com.yulin.controller.request.ProductOrderPageRequest;
import com.yulin.utils.JsonData;

import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/3
 * @Description:
 */
public interface ProductOrderService {

    Map<String, Object> page(ProductOrderPageRequest productOrderPageRequest);

    /**
     * 查询订单接口
     * @param outTradeNo
     * @return
     */
    String queryProductOrderState(String outTradeNo);

    JsonData confirmOrder(ConfirmOrderRequest orderRequest);

}
