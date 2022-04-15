package com.yulin.feign;

import com.yulin.controller.request.UseTrafficRequest;
import com.yulin.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/11
 * @Description:
 */
@FeignClient(name = "dcloud-account")
public interface TrafficFeignService {

    /**
     * 使用流量包
     * @param request
     * @return
     */
    @PostMapping(value = "/api/traffic/v1/reduce",headers = {"rpc-token=${rpc.token}"})
    JsonData useTraffic(@RequestBody UseTrafficRequest request);

}
