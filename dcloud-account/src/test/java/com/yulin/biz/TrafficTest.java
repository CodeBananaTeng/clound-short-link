package com.yulin.biz;

import com.yulin.AccountApplication;
import com.yulin.component.SmsComponent;
import com.yulin.config.SmsConfig;
import com.yulin.mapper.TrafficMapper;
import com.yulin.model.TrafficDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
public class TrafficTest {

    @Autowired
    private TrafficMapper trafficMapper;

    @Test
    public void testSendSms(){

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            TrafficDO trafficDO = new TrafficDO();
            trafficDO.setAccountNo(Long.valueOf(random.nextInt(100)));
            trafficMapper.insert(trafficDO);
        }
    }

}
