package com.yulin.biz;

import com.yulin.ShopApplication;
import com.yulin.manager.ProductOrderManager;
import com.yulin.model.ProductOrderDO;
import com.yulin.utils.CommonUtil;
import groovy.util.logging.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/1
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class)
@Slf4j
public class ProductOrderTest {

    @Autowired
    private ProductOrderManager productOrderManager;

    @Test
    public void testAdd(){
        ProductOrderDO productOrderDO = ProductOrderDO.builder()
                .outTradeNo(CommonUtil.generateUUID())
                .payAmount(new BigDecimal(11))
                .state("NEW")
                .nickname("LinuxTYL")
                .accountNo(10L)
                .del(0)
                .productId(2L)
                .build();
        productOrderManager.add(productOrderDO);
    }

}
