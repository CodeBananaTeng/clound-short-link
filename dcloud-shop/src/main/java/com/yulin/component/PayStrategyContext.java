package com.yulin.component;

import com.yulin.vo.PayInfoVO;

/**
 * 支付的上下文
 * @Auther:LinuxTYL
 * @Date:2022/3/21
 * @Description:
 */


public class PayStrategyContext {

    private PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy){
        this.payStrategy = payStrategy;
    }

    /**
     * 根据策略对象执行不同的下单接口
     * @return
     */
    public String executeUnifiedOrder(PayInfoVO payInfoVO){
        return payStrategy.unifiedOrder(payInfoVO);
    }

    /**
     * 根据策略对象执行不懂的退款接口
     * @return
     */
    public String executeRefundOrder(PayInfoVO payInfoVO){
        return payStrategy.refund(payInfoVO);
    }

    /**
     * 根据策略对象执行不懂的关闭订单接口
     * @return
     */
    public String executeCloseOrder(PayInfoVO payInfoVO){
        return payStrategy.closeOrder(payInfoVO);
    }

    /**
     * 根据策略对象执行不懂的查询订单接口
     * @return
     */
    public String executeQueryPayStateOrder(PayInfoVO payInfoVO){
        return payStrategy.queryPayStatus(payInfoVO);
    }





}
