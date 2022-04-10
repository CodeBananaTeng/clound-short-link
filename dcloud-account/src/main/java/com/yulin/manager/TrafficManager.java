package com.yulin.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yulin.model.TrafficDO;

import java.util.List;

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
     * 删除过期流量包
     * @return
     */
    boolean deleteExpireTraffic();

    /**
     * 查找可⽤的短链流量包(未过期),包括免费流ᰁ包
     * @param accountNo
     * @return
     */
    List<TrafficDO> selectAvailableTraffics(Long accountNo);

    /**
     * 给某个流量包增加使⽤次数
     *
     * @param currentTrafficId
     * @param accountNo
     * @param usedTimes
     * @return
     */
    int addDayUsedTimes(Long accountNo, Long trafficId, Integer usedTimes);

    /**
     * 恢复流量包使⽤当天次数
     * @param accountNo
     * @param trafficId
     * @param useTimes
     */
    int releaseUsedTimes(long accountNo, Long trafficId, Integer useTimes);

    /**
     * 批量更新流量包使⽤次数为0
     * @param accountNo
     * @param unUpdatedTrafficIds
     */
    int batchUpdateUsedTimes(Long accountNo, List<Long> unUpdatedTrafficIds);

}
