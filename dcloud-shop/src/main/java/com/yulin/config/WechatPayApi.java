package com.yulin.config;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/28
 * @Description:
 */
public class WechatPayApi {

    /**
     * 微信支付主机地址
     */
    public static final String HOST = "https://api.mch.weixin.qq.com";

    /**
     * native下单
     */
    public static final String NATIVE_ORDER = HOST + "/v3/pay/transactions/native";

    /**
     * native订单状态查询,根据商户的订单号
     */
    public static final String NATIVE_QUERY = HOST + "/v3/pay/transactions/out-trade-no/%s?mchid=%s";

}
