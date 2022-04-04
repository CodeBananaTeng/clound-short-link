package com.yulin.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yulin.controller.request.TrafficPageRequest;
import com.yulin.enums.EventMessageType;
import com.yulin.feign.ProductFeignService;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manager.TrafficManager;
import com.yulin.model.EventMessage;
import com.yulin.model.LoginUser;
import com.yulin.model.TrafficDO;
import com.yulin.service.TrafficService;
import com.yulin.utils.JsonData;
import com.yulin.utils.JsonUtil;
import com.yulin.utils.TimeUtil;
import com.yulin.vo.ProductVO;
import com.yulin.vo.TrafficVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/1
 * @Description:
 */
@Service
@Slf4j
public class TrafficServiceImpl implements TrafficService {

    @Autowired
    private TrafficManager trafficManager;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void handleTrafficMessage(EventMessage eventMessage) {
        Long accountNo =  eventMessage.getAccountNo();
        String messageType = eventMessage.getEventMessageType();
        if (EventMessageType.PRODUCT_ORDER_PAY.name().equalsIgnoreCase(messageType)){
            
            //订单已经支付新增流量包
            String content = eventMessage.getContent();
            Map<String,Object> orderInfoMap = JsonUtil.json2Obj(content, Map.class);
            //还原订单商品信息
            String outTradeNo = (String) orderInfoMap.get("outTradeNo");
            Integer buyNum = (Integer) orderInfoMap.get("buyNum");
            String productStr = (String) orderInfoMap.get("product");
            ProductVO productVO = JsonUtil.json2Obj(productStr, ProductVO.class);
            log.info("商品信息:{}",productVO);

            //流量包有效期
            LocalDateTime expiredDateTime = LocalDateTime.now().plusDays(productVO.getValidDay());
            Date date = Date.from(expiredDateTime.atZone(ZoneId.systemDefault()).toInstant());

            //构建流量包
            TrafficDO trafficDO = TrafficDO.builder().accountNo(accountNo)
                    .dayLimit(productVO.getDayTimes() * buyNum)
                    .dayUsed(0)
                    .totalLimit(productVO.getTotalTimes())
                    .pluginType(productVO.getPluginType())
                    .level(productVO.getLevel())
                    .productId(productVO.getId())
                    .outTradeNo(outTradeNo)
                    .expiredDate(date)
                    .build();
            int rows = trafficManager.add(trafficDO);
            log.info("消费消息->新增流量包rows=:{}",rows);
        }else if (EventMessageType.TRAFFIC_FREE_INIT.name().equalsIgnoreCase(messageType)){
            //免费发放流量包的业务逻辑
            Long bizId = Long.valueOf(eventMessage.getBizId());
            JsonData jsonData = productFeignService.detail(bizId);

            ProductVO productVO = jsonData.getData(new TypeReference<ProductVO>() {
            });
            //构建流量包
            TrafficDO trafficDO = TrafficDO.builder().accountNo(accountNo)
                    .dayLimit(productVO.getDayTimes())
                    .dayUsed(0)
                    .totalLimit(productVO.getTotalTimes())
                    .pluginType(productVO.getPluginType())
                    .level(productVO.getLevel())
                    .productId(productVO.getId())
                    .outTradeNo("free_init")
                    .expiredDate(new Date())
                    .build();
            trafficManager.add(trafficDO);
        }
        
    }

    @Override
    public Map<String, Object> pageAvailable(TrafficPageRequest request) {
        int size = request.getSize();
        int page = request.getPage();
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        IPage<TrafficDO> trafficDOIPage = trafficManager.pageAvailable(page, size, loginUser.getAccountNo());
        //获取流量包列表
        List<TrafficDO> records = trafficDOIPage.getRecords();
        List<TrafficVO> trafficVOList = records.stream().map(obj -> beanProcess(obj)).collect(Collectors.toList());
        Map<String,Object> pageMap = new HashMap<>();
        pageMap.put("total_record",trafficDOIPage.getTotal());
        pageMap.put("total_page",trafficDOIPage.getPages());
        pageMap .put("current_data",trafficVOList);
        return pageMap;
    }

    /**
     * 查找详情
     * @param trafficId
     * @return
     */
    @Override
    public TrafficVO detail(Long trafficId) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        TrafficDO byIdAndAccountNo = trafficManager.findByIdAndAccountNo(trafficId, loginUser.getAccountNo());
        return beanProcess(byIdAndAccountNo);
    }

    private TrafficVO beanProcess(TrafficDO obj) {
        TrafficVO trafficVO = new TrafficVO();
        BeanUtils.copyProperties(obj,trafficVO);
        return trafficVO;
    }

}
