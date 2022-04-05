package com.yulin.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/5
 * @Description:
 */
@Component
@Slf4j
public class MyJobHandler {

    @XxlJob(value = "demoJobHandler",init = "",destroy = "")
    public ReturnT<String> excute(String param){
        log.info("execute 任务方法触发执行成功");
        return ReturnT.SUCCESS;
    }

    private void init(){
        log.info("myjobhandler init >>>>>>");
    }

    private void destory(){
        log.info("MyjobHandler destory >>>>>>>");
    }

}
