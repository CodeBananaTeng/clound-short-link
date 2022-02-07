package com.yulin.service.impl;

import com.yulin.constant.TimeConstant;
import com.yulin.enums.BillTypeEnum;
import com.yulin.enums.BizCodeEnum;
import com.yulin.enums.ProductOrderPayTypeEnum;
import com.yulin.enums.ProductOrderStateEnum;
import com.yulin.exception.BizException;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manager.ProductManager;
import com.yulin.manager.ProductOrderManager;
import com.yulin.model.LoginUser;
import com.yulin.model.ProductDO;
import com.yulin.model.ProductOrderDO;
import com.yulin.request.ConfirmOrderRequest;
import com.yulin.service.ProductOrderService;
import com.yulin.utils.CommonUtil;
import com.yulin.utils.JsonData;
import com.yulin.utils.JsonUtil;
import com.yulin.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
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

    @Override
    public Map<String, Object> page(int page, int size, String state) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        Map<String, Object> pageResult = productOrderManager.page(page, size, accountNo, state);
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
     * ⽀付成功创建流ᰁ包（TODO）
     * @param orderRequest
     * @return
     */
    @Override
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
        //调用支付信息 TODO

        //发送关单延迟消息 TODO
        return null;
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
