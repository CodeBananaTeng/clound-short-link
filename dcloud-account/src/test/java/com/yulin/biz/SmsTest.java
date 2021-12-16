package com.yulin.biz;

import com.yulin.AccountApplication;
import com.yulin.component.SmsComponent;
import com.yulin.config.SmsConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
public class SmsTest {

    @Autowired
    private SmsComponent smsComponent;

    @Autowired
    private SmsConfig smsConfig;

    @Test
    public void testSendSms(){
        for (int i = 0; i < 3; i++) {
            smsComponent.send("18684002281",smsConfig.getTemplateId(),"666888");

        }
    }

}
