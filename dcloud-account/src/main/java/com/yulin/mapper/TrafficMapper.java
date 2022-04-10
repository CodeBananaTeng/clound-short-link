package com.yulin.mapper;

import com.yulin.model.TrafficDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yulin
 * @since 2021-12-15
 */
public interface TrafficMapper extends BaseMapper<TrafficDO> {

    /**
     * 给某个流量包增加天使用次数
     * @param accountNo
     * @param trafficId
     * @param usedTimes
     * @return
     */
    int addDayUsedTimes(@Param("accountNo") Long accountNo,
                        @Param("trafficId") Long trafficId,
                        @Param("usedTimes") Integer usedTimes);

    /**
     * 恢复某个流量包使用次数
     * @param accountNo
     * @param trafficId
     * @param useTimes
     */
    void releaseUsedTimes(@Param("accountNo") long accountNo,@Param("trafficId") Long trafficId,@Param("usedTimes") Integer useTimes);
}
