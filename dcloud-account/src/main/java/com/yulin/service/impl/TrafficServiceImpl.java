package com.yulin.service.impl;

import com.yulin.enums.EventMessageType;
import com.yulin.manager.TrafficManager;
import com.yulin.model.EventMessage;
import com.yulin.model.TrafficDO;
import com.yulin.service.TrafficService;
import com.yulin.utils.JsonUtil;
import com.yulin.utils.TimeUtil;
import com.yulin.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

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

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void handleTrafficMessage(EventMessage eventMessage) {
        
        String messageType = eventMessage.getEventMessageType();
        if (EventMessageType.PRODUCT_ORDER_PAY.name().equalsIgnoreCase(messageType)){
            
            //订单已经支付新增流量包
            String content = eventMessage.getContent();
            Map<String,Object> orderInfoMap = JsonUtil.json2Obj(content, Map.class);

            //还原订单商品信息
            Long accountNo = (Long) orderInfoMap.get("accountNo");
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
        }
        
    }

}
