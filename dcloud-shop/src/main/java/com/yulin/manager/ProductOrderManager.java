package com.yulin.manager;

import com.yulin.model.ProductDO;
import com.yulin.model.ProductOrderDO;

import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/24
 * @Description:
 */
public interface ProductOrderManager {

    /**
     * 新增
     * @param productOrderDO
     * @return
     */
    int add(ProductOrderDO productOrderDO);

    /**
     * 通过订单号和账号查询
     * @param outTradeNo
     * @param accountNo
     * @return
     */
    ProductOrderDO findByOutTradeNoAndAccountNo(String outTradeNo,Long accountNo);

    /**
     * 更新订单状态
     * @param outTradeNo
     * @param accountNo
     * @param newState
     * @param oldState
     * @return
     */
    int updateOrderPayStatus(String outTradeNo,Long accountNo,String newState,String oldState);

    /**
     * 分页查看订单状态，支持筛选状态
     * @param page
     * @param size
     * @param accountNo
     * @param state
     * @return
     */
    Map<String,Object> page(int page,int size,Long accountNo,String state);
}
