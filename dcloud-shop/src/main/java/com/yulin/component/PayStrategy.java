package com.yulin.component;

import com.yulin.vo.PayInfoVO;

/**
 * @Auther:LinuxTYL
 * @Date:2022/3/21
 * @Description:
 */
public interface PayStrategy {

    /**
     * 统一下单接口
     * @param payInfoVO
     * @return
     */
    String unifiedOrder(PayInfoVO payInfoVO);

    /**
     * 退款接口
     * @param payInfoVO
     * @return
     */
    default String refund(PayInfoVO payInfoVO){return "";}

    /**
     * 查询状态
     * @param payInfoVO
     * @return
     */
    default String queryPayStatus(PayInfoVO payInfoVO){return  "";}

    /**
     * 关闭订单
     * @param payInfoVO
     * @return
     */
    default String closeOrder(PayInfoVO payInfoVO){return "";}

}
