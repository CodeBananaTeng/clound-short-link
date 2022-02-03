package com.yulin.service.impl;

import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manager.ProductOrderManager;
import com.yulin.model.ProductOrderDO;
import com.yulin.request.ConfirmOrderRequest;
import com.yulin.service.ProductOrderService;
import com.yulin.utils.JsonData;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {
        return null;
    }


}
