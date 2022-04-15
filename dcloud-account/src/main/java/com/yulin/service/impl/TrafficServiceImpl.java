package com.yulin.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yulin.constant.RedisKey;
import com.yulin.controller.request.TrafficPageRequest;
import com.yulin.controller.request.UseTrafficRequest;
import com.yulin.enums.BizCodeEnum;
import com.yulin.enums.EventMessageType;
import com.yulin.exception.BizException;
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
import com.yulin.vo.UserTrafficVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

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
            //新增流量包应该删除这个key，买了之后会更多，删除的话就可以触发重新计算
            String totalTrafficTimesKey = String.format(RedisKey.DAY_TOTAL_TRAFFIC,accountNo);
            redisTemplate.delete(totalTrafficTimesKey);
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

    /**
     * 删除过期流量包
     * @return
     */
    @Override
    public boolean deleteExpireTraffic() {
        return trafficManager.deleteExpireTraffic();
    }

    /**
     * * 查询用户全部可用流量包
     * * 遍历用户可用流量包
     *   * 判断是否更新-用日期判断
     *     * 没更新的流量包后加入【待更新集合】中
     *       * 增加【今天剩余可用总次数】
     *     * 已经更新的判断是否超过当天使用次数
     *       * 如果没超过则增加【今天剩余可用总次数】
     *       * 超过则忽略
     *
     * * 更新用户今日流量包相关数据
     * * 扣减使用的某个流量包使用次数
     * @param trafficRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public JsonData reduce(UseTrafficRequest trafficRequest) {
        Long accountNo = trafficRequest.getAccountNo();
        //处理流量包，筛选出未更新的流量包，当前使用的流量包
        UserTrafficVO userTrafficVO = processTrafficList(accountNo);
        log.info("今天可用总次数:{},当前使用流量包:{}",userTrafficVO.getDayTotalLeftTimes(),userTrafficVO.getCurrentTrafficDO());
        if (userTrafficVO.getCurrentTrafficDO() == null){
            //没有可用的流量包
            return JsonData.buildSuccess(BizCodeEnum.TRAFFIC_REDUCE_FAIL);
        }
        log.info("待更新流量包列表:{}",userTrafficVO.getUnUpdatedTrafficIds());

        if (userTrafficVO.getUnUpdatedTrafficIds().size() > 0){
            //更新进入流量包
            trafficManager.batchUpdateUsedTimes(accountNo,userTrafficVO.getUnUpdatedTrafficIds());
        }
        //先更新再扣减当前使用的流量包
        int rows = trafficManager.addDayUsedTimes(accountNo, userTrafficVO.getCurrentTrafficDO().getId(), 1);
        if (rows != 1){
            throw new BizException(BizCodeEnum.TRAFFIC_REDUCE_FAIL);
        }
        //往redis设置下总流量包次数，短链服务那边递减即可 如果有新增流量包，则删除这个key
        long leftSeconds = TimeUtil.getRemainSecondsOneDay(new Date());
        String totalTrafficTimesKey = String.format(RedisKey.DAY_TOTAL_TRAFFIC,accountNo);
        redisTemplate.opsForValue().set(totalTrafficTimesKey,userTrafficVO.getDayTotalLeftTimes()-1,leftSeconds, TimeUnit.SECONDS);

        return JsonData.buildSuccess();
    }


    private UserTrafficVO processTrafficList(Long accountNo) {
        //全部流量包
        List<TrafficDO> list = trafficManager.selectAvailableTraffics(accountNo);
        if (list == null || list.size() == 0){
            throw new BizException(BizCodeEnum.TRAFFIC_EXCEPTION);
        }
        //天剩余可用总次数
        Integer dayTotalLeftTimes = 0;
        //当前使用
        TrafficDO currentTrafficDO = null;

        //没过期但是今天没更新的流量包id列表
        List<Long> unUpdatedTrafficIds = new ArrayList<>();
        //今天日期
        String todatStr = TimeUtil.format(new Date(),"yyyy-MM-dd");
        for (TrafficDO trafficDO : list) {
            String trafficUpdateDate = TimeUtil.format(trafficDO.getGmtModified(), "yyyy-MM-dd");
            if (todatStr.equalsIgnoreCase(trafficUpdateDate)){
                //已经更新
                int dayLeftTimes = trafficDO.getDayLimit() - trafficDO.getDayUsed();
                //天可用总次数 = 总次数 - 已用
                dayTotalLeftTimes = dayTotalLeftTimes + dayLeftTimes;

                //选取当次使用的流量包 currentTrafficDO == null
                if (dayLeftTimes >0 && currentTrafficDO == null){
                    currentTrafficDO = trafficDO;
                }

            }else {
                //未更新
                dayTotalLeftTimes = dayTotalLeftTimes + trafficDO.getDayLimit();
                //记录未更新的流量包
                unUpdatedTrafficIds.add(trafficDO.getId());
                //选取档次使用流量包
                if (currentTrafficDO == null){
                    currentTrafficDO = trafficDO;
                }

            }
        }
        UserTrafficVO userTrafficVO = new UserTrafficVO(dayTotalLeftTimes, currentTrafficDO, unUpdatedTrafficIds);
        return userTrafficVO;
    }

    private TrafficVO beanProcess(TrafficDO obj) {
        TrafficVO trafficVO = new TrafficVO();
        BeanUtils.copyProperties(obj,trafficVO);
        return trafficVO;
    }

}
