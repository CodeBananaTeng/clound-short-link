package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yulin.enums.PluginTypeEnum;
import com.yulin.manager.TrafficManager;
import com.yulin.mapper.TrafficMapper;
import com.yulin.model.TrafficDO;
import com.yulin.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/3/30
 * @Description:
 */
@Component
@Slf4j
public class TrafficManangerImpl implements TrafficManager {

    @Autowired
    private TrafficMapper trafficMapper;

    @Override
    public int add(TrafficDO trafficDO) {
        return trafficMapper.insert(trafficDO);
    }

    @Override
    public IPage<TrafficDO> pageAvailable(int page, int size, long accountNo) {
        Page<TrafficDO> pageInfo = new Page<>(page,size);
        String today = TimeUtil.format(new Date(),"yyyy-MM-dd");
        Page<TrafficDO> trafficDOPage = trafficMapper.selectPage(pageInfo, new QueryWrapper<TrafficDO>()
                .eq("account_no", accountNo)
                .ge("expired_date", today).orderByDesc("gmt_create"));
        return trafficDOPage;
    }

    @Override
    public TrafficDO findByIdAndAccountNo(Long trafficId, Long accountNo) {
        return trafficMapper.selectOne(new QueryWrapper<TrafficDO>().eq("account_no",accountNo).eq("id",trafficId));
    }



    @Override
    public boolean deleteExpireTraffic() {
        int rows = trafficMapper.delete(new QueryWrapper<TrafficDO>().le("expired_date", new Date()));
        log.info("删除过期流量包行数:{}",rows);
        return true;
    }

    /**
     * 查找可用的流量包
     * select * from traffic where account_no = 111 and (expired_date >= ? OR out_trade_no = free_init)
     * @param accountNo
     * @return
     */
    @Override
    public List<TrafficDO> selectAvailableTraffics(Long accountNo) {
        String today = TimeUtil.format(new Date(),"yyyy-MM-dd");
        QueryWrapper<TrafficDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_no",accountNo);
        queryWrapper.and(wrapper -> wrapper.ge("expired_date",today).or().eq("out_trade_no","free_init"));
        List<TrafficDO> trafficDOS = trafficMapper.selectList(queryWrapper);
        return trafficDOS;
    }

    /**
     * 增加流量包使用次数
     * @param accountNo
     * @param trafficId
     * @param usedTimes
     * @return
     */
    @Override
    public int addDayUsedTimes(Long accountNo, Long trafficId, Integer usedTimes) {

        return trafficMapper.addDayUsedTimes(accountNo,trafficId,usedTimes);
    }

    /**
     * 恢复流量包
     * @param accountNo
     * @param trafficId
     * @param useTimes
     * @return
     */
    @Override
    public int releaseUsedTimes(long accountNo, Long trafficId, Integer useTimes) {
        trafficMapper.releaseUsedTimes(accountNo,trafficId,useTimes);
        return 0;
    }

    /**
     * 批量更新
     * @param accountNo
     * @param unUpdatedTrafficIds
     * @return
     */
    @Override
    public int batchUpdateUsedTimes(Long accountNo, List<Long> unUpdatedTrafficIds) {

        int rows = trafficMapper.update(null, new UpdateWrapper<TrafficDO>()
                .eq("account_no", accountNo)
                .in("id", unUpdatedTrafficIds)
                .set("day_used", 0));
        return rows;
    }
}
