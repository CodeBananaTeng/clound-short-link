package com.yulin.enums;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
public enum EventMessageType {
    /**
     * 短链创建
     */
    SHORT_LINK_ADD,

    /**
     * 短链创建C端
     */
    SHORT_LINK_ADD_LINK,

    /**
     * 短链创建B端
     */
    SHORT_LINK_ADD_MAPPING,

    /**
     * 短链删除
     */
    SHORT_LINK_DEL,

    /**
     * 短链删除C端
     */
    SHORT_LINK_DEL_LINK,

    /**
     * 短链删除 B端
     */
    SHORT_LINK_DEL_MAPPING,

    /**
     * 短链更新
     */
    SHORT_LINK_UPDATE,

    /**
     * 短链更新C端
     */
    SHORT_LINK_UPDATE_LINK,

    /**
     * 短链更新 B端
     */
    SHORT_LINK_UPDATE_MAPPING,

    /**
     * 新建商品订单
     */
    PRODUCT_ORDER_NEW,

    /**
     * 订单支付
     */
    PRODUCT_ORDER_PAY,

    /**
     * 流量包
     */
    TRAFFIC_FREE_INIT,

}
