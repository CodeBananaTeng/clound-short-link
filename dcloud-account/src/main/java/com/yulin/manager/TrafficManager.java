package com.yulin.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yulin.model.TrafficDO;

/**
 * 流量包管理
 * @Auther:LinuxTYL
 * @Date:2022/3/30
 * @Description:
 */
public interface TrafficManager {

    /**
     * 新增流量包
     * @param trafficDO
     * @return
     */
    int add(TrafficDO trafficDO);

    /**
     * 分页查询可用的流量包
     * @param page
     * @param size
     * @return
     */
    IPage<TrafficDO> pageAvailable(int page, int size,long accountNo);

    /**
     * 根据id查找详情
     * @param trafficId
     * @param accountNo
     * @return
     */
    TrafficDO findByIdAndAccountNo(Long trafficId,Long accountNo);

    /**
     * 增加某个流量包的使用次数
     * @param currentTrafficId
     * @param accountNo
     * @param daUseTimes
     * @return
     */
    int addDayUsedTimes(Long currentTrafficId,Long accountNo,int daUseTimes);

}
