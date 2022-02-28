package com.yulin.biz;

import com.yulin.ShopApplication;
import com.yulin.config.PayBeanConfig;
import com.yulin.manager.ProductOrderManager;
import com.yulin.model.ProductOrderDO;
import com.yulin.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/1
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class)
@Slf4j
public class WechatPayTest {

    @Autowired
    private PayBeanConfig payBeanConfig;

    @Test
    public void testLoadPrivateKey() throws IOException {
        String algorithm = payBeanConfig.getPrivateKey().getAlgorithm();
        log.info(algorithm);
    }



}
