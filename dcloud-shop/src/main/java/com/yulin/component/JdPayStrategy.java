package com.yulin.component;

import com.yulin.vo.PayInfoVO;

/**
 * 京东支付
 * @Auther:LinuxTYL
 * @Date:2022/3/21
 * @Description:
 */
public class JdPayStrategy implements PayStrategy{
    @Override
    public String unifiedOrder(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String refund(PayInfoVO payInfoVO) {
        return PayStrategy.super.refund(payInfoVO);
    }

    @Override
    public String queryPayStatus(PayInfoVO payInfoVO) {
        return PayStrategy.super.queryPayStatus(payInfoVO);
    }

    @Override
    public String closeOrder(PayInfoVO payInfoVO) {
        return PayStrategy.super.closeOrder(payInfoVO);
    }
}
