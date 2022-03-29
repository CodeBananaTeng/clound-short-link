package com.yulin.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.yulin.config.WechatPayConfig;
import com.yulin.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther:LinuxTYL
 * @Date:2022/3/29
 * @Description:
 */
@RestController
@RequestMapping("/api/callback/order/v1/")
@Slf4j
public class PayCallbackController {

    @Autowired
    private WechatPayConfig wechatPayConfig;

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private ScheduledUpdateCertificatesVerifier verifier;

    /**
     * 获取报文
     * 验证签名
     * 解密
     * 处理业务逻辑
     * 响应请求
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("wechat")
    @ResponseBody
    public Map<String,String> wechatPayCallBack(HttpServletRequest request, HttpServletResponse response){
        //获取报⽂
        String body = getRequestBody(request);
        //随机串
        String nonceStr = request.getHeader("Wechatpay-Nonce");
        //微信传递过来的签名
        String signature = request.getHeader("Wechatpay-Signature");
        //证书序列号（微信平台）
        String serialNo = request.getHeader("Wechatpay-Serial");
        //时间戳
        String timestamp = request.getHeader("Wechatpay-Timestamp");

        //构造签名串 包含三个部分，时间戳，随机串，报文主体
        String signStr = Stream.of(timestamp, nonceStr, body).collect(Collectors.joining("\n", "", "\n"));

        Map<String, String> map = new HashMap<>(2);
        try {
            //验证签名是否通过
            boolean result = verifiedSign(serialNo, signStr, signature);
            if (result){
                //解密数据 TODO
                String plainBody = decryptBody(body);
                log.info("解密后的明文数据是:{}",plainBody);
                Map<String,String> paramsMap = convertWechatMessage(plainBody);
                //处理业务逻辑 TODO

                //响应微信 TODO
                map.put("code","SUCCESS");
                map.put("message","成功");
            }


        } catch (Exception e) {
            log.error("微信支付回调异常:{}",e);
        }
        return map;
    }

    /**
     * 转换body为map
     * @param plainBody
     * @return
     */
    private Map<String, String> convertWechatMessage(String plainBody) {
        Map<String,String> paramMap = new HashMap<>();
        JSONObject object = JSONObject.parseObject(plainBody);
        //商户订单号
        paramMap.put("out_trade_no",object.getString("out_trade_no"));
        //交易状态
        paramMap.put("trade_state",object.getString("trade_state"));
        //附加数据
        paramMap.put("account_no",object.getJSONObject("attach").getString("accountNo"));
        return paramMap;
    }

    /**
     * 解密数据
     * @param body
     * @return
     */
    private String decryptBody(String body) throws UnsupportedEncodingException, GeneralSecurityException {
        AesUtil aesUtil = new AesUtil(wechatPayConfig.getApiV3Key().getBytes("utf-8"));
        JSONObject object = JSONObject.parseObject(body);
        JSONObject resource = object.getJSONObject("resource");
        String ciphertext = resource.getString("ciphertext");
        String associated_data = resource.getString("associated_data");
        String nonce = resource.getString("nonce");
        return aesUtil.decryptToString(associated_data.getBytes("utf-8"),nonce.getBytes(StandardCharsets.UTF_8),ciphertext);
    }

    /**
     * 验证签名
     * @param seriaNo 微信平台证书序列号
     * @param signStr 自己组装的签名串
     * @param signature 微信返回的签名串
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean verifiedSign(String seriaNo,String signStr,String signature) throws UnsupportedEncodingException {
        return verifier.verify(seriaNo,signStr.getBytes("utf-8"),signature);
    }

    /**
     * 读取请求的数据流
     * @param request
     * @return
     * @throws IOException
     */
    private String getRequestBody(HttpServletRequest request){
        StringBuffer stringBuffer = new StringBuffer();
        try(ServletInputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
        }catch (IOException e){
            log.error("读取数据流异常:{}",e);
        }
        return stringBuffer.toString();
    }

}
