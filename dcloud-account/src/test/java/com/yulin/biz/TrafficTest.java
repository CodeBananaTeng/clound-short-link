package com.yulin.biz;

import com.yulin.AccountApplication;
import com.yulin.component.SmsComponent;
import com.yulin.config.SmsConfig;
import com.yulin.manager.TrafficManager;
import com.yulin.mapper.TrafficMapper;
import com.yulin.model.TrafficDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@Slf4j
public class TrafficTest {

    @Autowired
    private TrafficMapper trafficMapper;

    @Autowired
    private TrafficManager trafficManager;

    @Test
    public void testSendSms(){

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            TrafficDO trafficDO = new TrafficDO();
            trafficDO.setAccountNo(Long.valueOf(random.nextInt(100)));
            trafficMapper.insert(trafficDO);
        }
    }

    @Test
    public void testDeleteExpireTraffic(){
        trafficManager.deleteExpireTraffic();
    }


    @Test
    public void testSelectAvaliableTraffics(){
        List<TrafficDO> trafficDOS = trafficManager.selectAvailableTraffics(1640008875462L);
        trafficDOS.stream().forEach(obj->{
            log.info(obj.toString());
        });
    }

    @Test
    public void testAddDayUsedTimes(){
        trafficManager.addDayUsedTimes(1640008875462L,1510957867340521473L,1);
    }

    @Test
    public void testReleaseDayUsedTimes(){
        int rows = trafficManager.releaseUsedTimes(1640008875462L, 1510957867340521473L, 1);
        log.info("rows = {}",rows);
    }

    @Test
    public void testBatchUpdateUsedTimes(){
        ArrayList<Long> list = new ArrayList<>();
        list.add(1510957867340521473L);

        int rows = trafficManager.batchUpdateUsedTimes(1640008875462L, list);
        log.info("rows = {}",rows);
    }

}
