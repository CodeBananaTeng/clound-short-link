package com.yulin.service.impl;

import com.yulin.component.PayFactory;
import com.yulin.config.RabbitMQConfig;
import com.yulin.constant.TimeConstant;
import com.yulin.controller.request.ProductOrderPageRequest;
import com.yulin.enums.*;
import com.yulin.exception.BizException;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manager.ProductManager;
import com.yulin.manager.ProductOrderManager;
import com.yulin.model.EventMessage;
import com.yulin.model.LoginUser;
import com.yulin.model.ProductDO;
import com.yulin.model.ProductOrderDO;
import com.yulin.controller.request.ConfirmOrderRequest;
import com.yulin.service.ProductOrderService;
import com.yulin.utils.CommonUtil;
import com.yulin.utils.JsonData;
import com.yulin.utils.JsonUtil;
import com.yulin.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/3
 * @Description:
 */
@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderManager productOrderManager;

    @Autowired
    private ProductManager productManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private PayFactory payFactory;

    @Override
    public Map<String, Object> page(ProductOrderPageRequest orderPageRequest) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        Map<String, Object> pageResult = productOrderManager.page(orderPageRequest.getPage(), orderPageRequest.getSize(), accountNo, orderPageRequest.getState());
        return pageResult;
    }

    @Override
    public String queryProductOrderState(String outTradeNo) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        ProductOrderDO productOrderDO = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        if (productOrderDO == null){
            return "";
        }else {
            return productOrderDO.getState();
        }
    }

    /**
     * 防重提交（TODO）
     * 获取最新的流量包价格
     * 订单验价
     * 如果有优惠券或者其他抵扣
     * 验证前端显示和后台计算价格
     * 创建订单对象保存数据库
     * 发送延迟消息-⽤于⾃动关单（TODO）
     * 创建⽀付信息-对接三⽅⽀付（TODO）
     * 回调更新订单状态（TODO）
     * ⽀付成功创建流量包（TODO）
     * @param orderRequest
     * @return
     */
    @Override
    @Transactional
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String orderOutTradeNo = CommonUtil.getStringNumRandom(32);
        ProductDO productDO = productManager.findDetailById(orderRequest.getProductId());
        //验证价格
        this.checkPrice(productDO,orderRequest);
        //创建订单
        ProductOrderDO productOrderDO = this.setProductOrder(orderRequest,loginUser,orderOutTradeNo,productDO);

        //创建支付对象
        PayInfoVO payInfoVO = PayInfoVO.builder().accountNo(loginUser.getAccountNo()).outTradeNo(orderOutTradeNo)
                .clientType(orderRequest.getClientType()).payType(orderRequest.getPayType())
                .title(productDO.getTitle()).description("")
                .payFee(orderRequest.getPayAmount()).orderPayTimeoutMills(TimeConstant.ORDER_PAY_TIMEOUT_MILLS).build();

        //发送关单延迟消息 TODO
        EventMessage eventMessage = EventMessage.builder()
                .eventMessageType(EventMessageType.PRODUCT_ORDER_NEW.name())
                .accountNo(loginUser.getAccountNo())
                .bizId(orderOutTradeNo)
                .build();
        rabbitTemplate.convertAndSend(rabbitMQConfig.getOrderEventExchange(),rabbitMQConfig.getOrderCloseDelayRoutingKey(),eventMessage);

        //调用支付信息 TODO
        String codeUrl = payFactory.pay(payInfoVO);
        if (StringUtils.isNoneBlank(codeUrl)){
            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("code_url",codeUrl);
            resultMap.put("out_trade_no",payInfoVO.getOutTradeNo());
            return JsonData.buildSuccess(resultMap);
        }
        return JsonData.buildSuccess(BizCodeEnum.PAY_ORDER_FAIL);
    }

    /**
     * //延迟消息的时间 需要⽐订单过期 时间⻓⼀点，这样就不存
     * 在查询的时候，⽤户还能⽀付成功
     * * //查询订单是否存在，如果已经⽀付则正常结束
     * * //如果订单未⽀付，主动调⽤第三⽅⽀付平台查询订单状态
     * * //确认未⽀付，本地取消订单
     * * //如果第三⽅平台已经⽀付，主动的把订单状态改成已
     * ⽀付，造成该原因的情况可能是⽀付通道回调有问题，然后触发⽀付后
     * 的动作，如何触发？RPC还是？
     * 关闭订单接口
     * @param eventMessage
     * @return
     */
    @Override
    public boolean closeProductOrder(EventMessage eventMessage) {
        String outTradeNo = eventMessage.getBizId();
        Long accountNo = eventMessage.getAccountNo();
        ProductOrderDO productOrderDO = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        if (productOrderDO == null){
            //订单不存在
            log.warn("订单不存在");
            return true;
        }
        if (productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())){
            //已经支付
            log.info("直接确认消息，订单已经支付:{}",eventMessage);
            return true;
        }
        if (productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.NEW.name())){
            //未支付，需要向第三方支付平台查询支付状态
            PayInfoVO payInfoVO = new PayInfoVO();
            payInfoVO.setPayType(productOrderDO.getPayType());
            payInfoVO.setOutTradeNo(outTradeNo);
            payInfoVO.setAccountNo(accountNo);
            //TODO 查询第三方支付平台
            String payResult = "";
            if (StringUtils.isBlank(payResult)){
                //如果是空的就是未支付，取消本地的订单
                productOrderManager.updateOrderPayStatus(outTradeNo,accountNo,ProductOrderStateEnum.CANCEL.name(), ProductOrderStateEnum.NEW.name());
                log.info("订单未支付成功，本地取消订单:{}",eventMessage);
            }else {
                //支付成功主动把订单状态改为支付
                log.warn("支付成功，但是微信回调通知失败，需要排查问题:{}",eventMessage);
                productOrderManager.updateOrderPayStatus(outTradeNo,accountNo,ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
                //发送MQ消息（触发支付成功后的逻辑） TODO
            }
        }
        //其他状态，例如取消就返回true
        return true;
    }


    /**
     * 处理微信回调通知
     * @param payTypeEnum
     * @param paramsMap
     */
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public JsonData processOrderCallbackMessage(ProductOrderPayTypeEnum payTypeEnum, Map<String, String> paramsMap) {
        //获取商户订单号
        String outTradeNo = paramsMap.get("out_trade_no");
        //交易状态
        String tradeState = paramsMap.get("trade_state");
        //用户的编号
        Long accountNo = Long.valueOf(paramsMap.get("account_no"));
        ProductOrderDO productOrderDO = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        Map<String,Object> content =  new HashMap<>(4);
        content.put("outTradeNo",outTradeNo);
        content.put("buyNum",productOrderDO.getBuyNum());
        content.put("accountNo",accountNo);
        content.put("product",productOrderDO.getProductSnapshot());
        //构建消息
        EventMessage eventMessage = EventMessage.builder()
                .bizId(outTradeNo)
                .accountNo(accountNo)
                .messageId(outTradeNo)
                .content(JsonUtil.obj2Json(content))
                .eventMessageType(EventMessageType.ORDER_PAY.name())
                .build();
        if (payTypeEnum.name().equalsIgnoreCase(ProductOrderPayTypeEnum.ALI_PAY.name())){
            //支付宝支付逻辑 TODO
        }else if (payTypeEnum.name().equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT_PAY.name())){
            //微信支付的内容
            if ("SUCCESS".equalsIgnoreCase(tradeState)){
                //成功就发送消息
                rabbitTemplate.convertAndSend(rabbitMQConfig.getOrderEventExchange(),rabbitMQConfig.getOrderUpdateTrafficRoutingKey(),eventMessage);
                return JsonData.buildSuccess();
            }
        }
        return JsonData.buildResult(BizCodeEnum.PAY_ORDER_CALLBACK_NOT_SUCCESS);
    }

    private ProductOrderDO setProductOrder(ConfirmOrderRequest orderRequest, LoginUser loginUser, String orderOutTradeNo, ProductDO productDO) {
        ProductOrderDO productOrderDO = new ProductOrderDO();

        //设置用户信息
        productOrderDO.setAccountNo(loginUser.getAccountNo());
        productOrderDO.setNickname(loginUser.getUsername());

        //设置商品信息
        productOrderDO.setProductId(productDO.getId());
        productOrderDO.setProductTitle(productDO.getTitle());
        productOrderDO.setProductSnapshot(JsonUtil.obj2Json(productDO));
        productOrderDO.setProductAmount(productDO.getAmount());

        //设置订单信息
        productOrderDO.setBuyNum(orderRequest.getBuyNum());
        productOrderDO.setOutTradeNo(orderOutTradeNo);
        productOrderDO.setCreateTime(new Date());
        productOrderDO.setDel(0);


        //发票信息
        productOrderDO.setBillType(BillTypeEnum.valueOf(orderRequest.getBillType()).name());
        productOrderDO.setBillHeader(orderRequest.getBillHeader());
        productOrderDO.setBillReceiverPhone(orderRequest.getBillReceiverPhone());
        productOrderDO.setBillReceiverEmail(orderRequest.getBillReceiverEmail());
        productOrderDO.setBillContent(orderRequest.getBillContent());

        //实际支付总价 经过前面的验证已经可以设置金额了
        productOrderDO.setProductAmount(orderRequest.getPayAmount());
        //总价，没有使用优惠券的
        productOrderDO.setTotalAmount(orderRequest.getTotalAmount());
        //订单状态
        productOrderDO.setState(ProductOrderStateEnum.NEW.name());
        //支付类型
        productOrderDO.setPayType(ProductOrderPayTypeEnum.valueOf(orderRequest.getPayType()).name());

        //插入数据库
        productOrderManager.add(productOrderDO);
        return productOrderDO;
    }


    private void checkPrice(ProductDO productDO, ConfirmOrderRequest orderRequest) {
        //后台计算价格
        BigDecimal bizTotal = BigDecimal.valueOf(orderRequest.getBuyNum()).multiply(productDO.getAmount());

        //前端传递总价和后端计算总价格是否一致.如果有优惠券也在这边进行计算
        if (bizTotal.compareTo(orderRequest.getPayAmount()) !=0){
            log.error("验证价格失败:{}",orderRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_PRICE_FAIL);
        }
    }


}
