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
import java.util.*;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/4
 * @Description:
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService{

    private static final String TOPIC_NAME = "ods_link_visit_topic";

    /**
     * 此处是使用测试的不是线上内容
     */

    private static List<String> ipList = new ArrayList<>();
    static {
        //深圳
        ipList.add("14.197.9.110");
        //⼴州
        ipList.add("113.68.152.139");
    }

    private static List<String> refererList = new ArrayList<>();
    static {
        refererList.add("https://taobao.com");
        refererList.add("https://douyin.com");
    }

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void recordShortLinkLog(HttpServletRequest request, String shortLinkCode, Long accountNo) {
        //拿到用户的IP，浏览器头信息，短链码信息
        //String ip = CommonUtil.getIpAddr(request);
        Random random = new Random();
        String ip = ipList.get(random.nextInt(ipList.size())) ;
        //全部请求头
        Map<String, String> headerMap = CommonUtil.getAllRequestHeader(request);
        Map<String,String> avaliableMap = new HashMap<>();
        avaliableMap.put("user-agent",headerMap.get("user-agent"));
        //avaliableMap.put("referer",headerMap.get("referer"));
        String referer = refererList.get(random.nextInt(refererList.size()));
        //全部请求头
        //Map<String,String> headerMap = CommonUtil.getAllRequestHeader(request);
        Map<String,String> availableMap = new HashMap<>();
        //availableMap.put("user-agent",headerMap.get("user-agent"));
//availableMap.put("referer",headerMap.get("referer"));
        availableMap.put("referer",referer);
        availableMap.put("accountNo",accountNo.toString());


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
        log.info("发送访问日志:{}",jsonLog);
        //发送到kafka
        kafkaTemplate.send(TOPIC_NAME,jsonLog);
    }
}
