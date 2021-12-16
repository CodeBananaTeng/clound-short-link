package com.yulin.service.impl;

import com.yulin.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Async("threadPoolExecutor")
    public void testSend() {
//        try {
//            TimeUnit.MILLISECONDS.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://old.xdclass.net", String.class);
        String body = forEntity.getBody();
        log.info(body);
    }
}
