package com.yulin.controller;


import com.yulin.annotation.RepeatSubmit;
import com.yulin.constant.RedisKey;
import com.yulin.controller.request.ProductOrderPageRequest;
import com.yulin.enums.BizCodeEnum;
import com.yulin.enums.ClientTypeEnum;
import com.yulin.enums.ProductOrderPayTypeEnum;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.controller.request.ConfirmOrderRequest;
import com.yulin.service.ProductOrderService;
import com.yulin.utils.CommonUtil;
import com.yulin.utils.JsonData;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yulin
 * @since 2022-01-24
 */
@RestController
@RequestMapping("/api/order/v1")
@Slf4j
public class ProductOrderController {

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 下单前获取令牌用于防重提交
     * @return
     */
    @GetMapping("token")
    public JsonData getOrderToken(){
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        String token = CommonUtil.getStringNumRandom(32);
        String key = String.format(RedisKey.SUBMIT_ORDER_TOKEN_KEY,accountNo,token);

        //令牌有效时间30分钟
        redisTemplate.opsForValue().set(key, String.valueOf(Thread.currentThread().getId()),30, TimeUnit.MINUTES);

        return JsonData.buildSuccess(token);
    }
    /**
     * 分页
     * @return
     */
    @PostMapping("page")
    @RepeatSubmit(limitType = RepeatSubmit.Type.PARAM)
    public JsonData page(@RequestBody ProductOrderPageRequest orderPageRequest){
        Map<String,Object> pageResult = productOrderService.page(orderPageRequest);
        return JsonData.buildSuccess(pageResult);
    }

    /**
     * 查询订单状态
     * @param outTradeNo
     * @return
     */
    @GetMapping("query_state")
    public JsonData queryState(@RequestParam(value = "out_trade_no") String outTradeNo){
        String state = productOrderService.queryProductOrderState(outTradeNo);
        return StringUtils.isNoneBlank(state)?JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST):JsonData.buildSuccess(state);
    }

    /**
     * 下单接口
     * @param orderRequest
     * @param response
     */
    @PostMapping("confirm")
    public void confirmOrder(@RequestBody ConfirmOrderRequest orderRequest, HttpServletResponse response){
        JsonData jsonData = productOrderService.confirmOrder(orderRequest);
        if (jsonData.getCode() == 0){
            //支付成功
            //端类型
            String clientType = orderRequest.getClientType();
            //支付类型
            String payType = orderRequest.getPayType();
            //如果是支付宝支付，跳转网页，SDK除外
            if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.ALI_PAY.name())){
                //支付宝需要区分PC,H5
                if (clientType.equalsIgnoreCase(ClientTypeEnum.PC.name())){
                    //主要关注的是PC端的支付
                    CommonUtil.sendHtmlMessage(response,jsonData);
                }else if (clientType.equalsIgnoreCase(ClientTypeEnum.APP.name())){
                    //app支付
                }else if (clientType.equalsIgnoreCase(ClientTypeEnum.H5.name())){
                    //H5支付
                }


            }else if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT_PAY.name())){
                //微信支付 直接响应json，里面含有连接，前段根据这个连接生成二维码
                CommonUtil.sendJsonMessage(response,jsonData);
            }
        }else {
            //支付失败
            System.out.println(("创建订单失败:"+jsonData.toString()));
            CommonUtil.sendJsonMessage(response,jsonData);
        }
    }

}

