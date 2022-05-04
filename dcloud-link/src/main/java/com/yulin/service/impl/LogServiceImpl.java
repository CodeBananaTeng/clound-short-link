package com.yulin.service.impl;

import com.yulin.enums.LogTypeEnum;
import com.yulin.model.LogRecord;
import com.yulin.service.LogService;
import com.yulin.utils.CommonUtil;
import com.yulin.utils.JsonData;
import com.yulin.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/4
 * @Description:
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService{

    private static final String TOPIC_NAME = "ods_link_visit_topic";

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void recordShortLinkLog(HttpServletRequest request, String shortLinkCode, Long accountNo) {
        //拿到用户的IP，浏览器头信息，短链码信息
        String ip = CommonUtil.getIpAddr(request);
        //全部请求头
        Map<String, String> headerMap = CommonUtil.getAllRequestHeader(request);
        Map<String,String> avaliableMap = new HashMap<>();
        avaliableMap.put("user_agent",headerMap.get("user_agent"));
        avaliableMap.put("referer",headerMap.get("referer"));
        avaliableMap.put("accountNo",accountNo.toString());
        LogRecord logRecord = LogRecord.builder()
                //日志类型
                .event(LogTypeEnum.SHORT_LINK_TYPE.name())

                //日志内容
                .data(avaliableMap)

                //客户端ip
                .ip(ip)

                //产生时间
                .ts(CommonUtil.getCurrentTimestamp())

                //业务唯一标识
                .bizId(shortLinkCode).build();
        String jsonLog = JsonUtil.obj2Json(logRecord);
        //打印到控制台方便排查
        log.info(jsonLog);
        //发送到kafka
        kafkaTemplate.send(TOPIC_NAME,jsonLog);
    }
}
