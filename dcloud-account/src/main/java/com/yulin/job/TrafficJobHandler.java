package com.yulin.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yulin.service.TrafficService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/5
 * @Description:
 */
@Component
@Slf4j
public class TrafficJobHandler {

    @Autowired
    private TrafficService trafficService;

    /**
     * 过期流量包执行
     * @param param
     * @return
     */
    @XxlJob(value = "trafficExpireHandler",init = "init",destroy = "destory")
    public ReturnT<String> excute(String param){
        trafficService.deleteExpireTraffic();
        log.info("触发执行");
        return ReturnT.SUCCESS;
    }

    private void init(){
        log.info("myjobhandler init >>>>>>");
    }

    private void destory(){
        log.info("MyjobHandler destory >>>>>>>");
    }

}
