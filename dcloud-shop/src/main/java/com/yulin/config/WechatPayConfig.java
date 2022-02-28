package com.yulin.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/28
 * @Description:
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "pay.wechat")
@Slf4j
public class WechatPayConfig {

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 公众号id 需要和商户号绑定
     */
    private String wxPayAppid;

    /**
     * 商户证书序列号,需要和证书对应
     */
    private String mchSerialNo;

    /**
     * api密钥
     */
    private String apiV3Key;

    /**
     * 商户私钥路径（微信服务端会根据证书序列号，找到证书获取公钥进⾏解密数据）
     */
    private String privateKeyPath;

    /**
     * ⽀付成功⻚⾯跳转
     */
    private String successReturnUrl;

    /**
     * ⽀付成功，回调通知
     */
    private String callbackUrl;

    public static class Url{

        /**
         * NATIVE下单接口
         */
        public static final String NATIVE_ORDER = "https://api.mch.weixin.qq.com/v3/pay/transactions/native";

        public static final String NATIVE_ORDER_PATH = "/v3/pay/transactions/native";

        /**
         * NATIVE订单查询接口，根据订单号查询
         * 1，根据微信订单号
         * 2，根据商户订单号
         */
        public static final String NATIVE_QUERY = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s?mchid=%s";

        public static final String NATIVE_QUERY_PATH = "/v3/pay/transactions/out-trade-no/%s?mchid=%s";


        /**
         * native关闭订单接口
         */
        public static final String NATIVE_CLOSE = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s/close";

        public static final String NATIVE_CLOSE_PATH = "/v3/pay/transactions/out-trade-no/%s/close";


    }

}
