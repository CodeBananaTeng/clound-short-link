package com.yulin.service.impl;

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
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        return null;
    }


    private void checkPrice(ProductDO productDO, ConfirmOrderRequest orderRequest) {
        //后台计算价格
        BigDecimal bizTotal = BigDecimal.valueOf(orderRequest.getBuyNum()).multiply(productDO.getAmount());

        //前段传递总价和后端计算总价格是否一致
        if (bizTotal.compareTo(orderRequest.getPayAmount()) !=0){
        }
    }


}
