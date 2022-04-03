package com.yulin.controller;

import com.yulin.controller.request.TrafficPageRequest;
import com.yulin.controller.request.UseTrafficRequest;
import com.yulin.service.TrafficService;
import com.yulin.utils.JsonData;
import com.yulin.vo.TrafficVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/3
 * @Description:
 */
@RestController
@RequestMapping("/api/traffic/v1")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    /**
     * 使用流量包API
     * @param useTrafficRequest
     * @param request
     * @return
     */
    @PostMapping("reduce")
    public JsonData useTraffic(@RequestBody UseTrafficRequest useTrafficRequest, HttpServletRequest request){

        //具体使用流量包逻辑 TODO
        return JsonData.buildSuccess();
    }

    /**
     * 分页查询流量包列表，查看可用的流量包
     * @param request
     * @return
     */
    @RequestMapping("page")
    public JsonData pageAvailable(@RequestBody TrafficPageRequest request){
        Map<String,Object> pageMap = trafficService.pageAvailable(request);
        return JsonData.buildSuccess(pageMap);
    }

    /**
     * 查找某个流量包详情
     * @param trafficId
     * @return
     */
    @GetMapping("detail/{trafficId}")
    public JsonData detail(@PathVariable("trafficId")Long trafficId){
        TrafficVO trafficVO = trafficService.detail(trafficId);
        return JsonData.buildSuccess(trafficVO);
    }


}
