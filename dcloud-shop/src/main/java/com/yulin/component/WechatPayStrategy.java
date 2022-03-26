package com.yulin.component;

import com.alibaba.fastjson.JSONObject;
import com.yulin.config.WechatPayApi;
import com.yulin.config.WechatPayConfig;
import com.yulin.utils.CommonUtil;
import com.yulin.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 微信支付
 * @Auther:LinuxTYL
 * @Date:2022/3/21
 * @Description:
 */
@Service
@Slf4j
public class WechatPayStrategy implements PayStrategy{

    @Autowired
    private WechatPayConfig payConfig;

    @Autowired
    private CloseableHttpClient wechatPayClient;


    @Override
    public String unifiedOrder(PayInfoVO payInfoVO) {
        //过期时间 RFC 3339格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        //⽀付订单过期时间
        String timeExpire = sdf.format(new Date(System.currentTimeMillis() + payInfoVO.getOrderPayTimeoutMills()));
        JSONObject amountObj = new JSONObject();
        //数据库存储是double⽐如，100.99元，微信⽀付需要以分为单位,需要乘以100
        int amount = payInfoVO.getPayFee().multiply(BigDecimal.valueOf(100)).intValue();
        amountObj.put("total", amount);
        amountObj.put("currency", "CNY");
        JSONObject payObj = new JSONObject();
        payObj.put("mchid", payConfig.getMchId());
        payObj.put("out_trade_no", payInfoVO.getOutTradeNo());
        payObj.put("appid", payConfig.getWxPayAppid());
        payObj.put("description", payInfoVO.getTitle());
        payObj.put("notify_url", payConfig.getCallbackUrl());
        payObj.put("time_expire", timeExpire);
        payObj.put("amount", amountObj);
        //回调携带
        payObj.put("attach", "{\"accountNo\":" + payInfoVO.getAccountNo() + "}");
        // 处理请求body参数
        String body = payObj.toJSONString();
        log.info("请求参数:{}", body);
        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentType("application/json");
        HttpPost httpPost = new HttpPost(WechatPayApi.NATIVE_ORDER);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);
        String result = "";
        try (CloseableHttpResponse response = wechatPayClient.execute(httpPost)) {
            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());
            log.debug("微信⽀付响应:resp code={}，return body={}", statusCode, responseStr);

            //处理成功
            if (statusCode == HttpStatus.OK.value()) {
                JSONObject jsonObject = JSONObject.parseObject(responseStr);
                if (jsonObject.containsKey("code_url")) {
                    result = jsonObject.getString("code_url");
                }
            } else {
                log.error("微信⽀付响应失败:respcode={}，return body={}", statusCode, responseStr);
            }
        } catch (Exception e) {
            log.error("微信⽀付响应异常信息:{}", e);
        }
        return result;

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
