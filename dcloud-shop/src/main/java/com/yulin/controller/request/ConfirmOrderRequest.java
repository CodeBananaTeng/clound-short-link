package com.yulin.controller.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/3
 * @Description:
 */
@Data
public class ConfirmOrderRequest {

    /**
     * 订单类型
     */
    private Long productId;



    /**
     * 购买数ᰁ
     */
    private Integer buyNum;



    /**
     * NEW 未⽀
     付订单,PAY已经⽀付订单,CANCEL超时取消订单
     */
    private String state;



    /**
     * 订单总⾦额
     */
    private BigDecimal totalAmount;

    /**
     * 订
     单实际⽀付价格
     */
    private BigDecimal payAmount;

    /**
     * 防止重复提交的令牌
     */
    private String token;

    /**
     * ⽀付类
     型，微信-银⾏-⽀付宝
     */
    private String payType;

    /**
     * 中断类型
     */
    private String clientType;


    /**
     * 0表示未删除，1表示已经
     删除
     */
    private Integer del;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 发票类型：0->不开发票；1->电⼦发票；2->纸质发票
     */
    private String billType;

    /**
     * 发票抬头
     */
    private String billHeader;

    /**
     * 发票内容
     */
    private String billContent;

    /**
     * 发票收票⼈电话
     */
    private String billReceiverPhone;

    /**
     * 发票收票⼈邮箱
     */
    private String billReceiverEmail;
}
