package com.yulin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/7
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayInfoVO {

    private String outTradeNo;

    /**
     * 订单总金额 单位为分
     */
    private BigDecimal payFee;

    /**
     * 支付类型 微信，支付宝
     */
    private String payType;

    /**
     * 端类型 APP/h5/pc
     */
    private String clientType;

    /**
     * 标题
     */
    private String title;

    /**
     * 详情
     */
    private String description;

    /**
     * 订单支付超时 毫秒
     */
    private long orderPayTimeoutMills;

    /**
     * 用户标识
     */
    private long accountNo;
}
