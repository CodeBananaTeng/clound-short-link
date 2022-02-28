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


}
