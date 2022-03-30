package com.yulin.service;

import com.yulin.controller.request.ConfirmOrderRequest;
import com.yulin.controller.request.ProductOrderPageRequest;
import com.yulin.enums.ProductOrderPayTypeEnum;
import com.yulin.model.EventMessage;
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

    /**
     * 关闭订单接口
     * @param eventMessage
     * @return
     */
    boolean closeProductOrder(EventMessage eventMessage);

    /**
     * 处理微信回调通知
     * @param wechatPay
     * @param paramsMap
     */
    JsonData processOrderCallbackMessage(ProductOrderPayTypeEnum wechatPay, Map<String, String> paramsMap);

    /**
     * 处理订单类的消息
     * @param eventMessage
     */
    void handleProductOrderMessage(EventMessage eventMessage);
}
