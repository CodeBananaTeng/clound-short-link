package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yulin.manager.TrafficManager;
import com.yulin.mapper.TrafficMapper;
import com.yulin.model.TrafficDO;
import com.yulin.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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

    /**
     * 给某个流量包增加天使用次数
     * @param currentTrafficId
     * @param accountNo
     * @param daUseTimes
     * @return
     */
    @Override
    public int addDayUsedTimes(Long currentTrafficId, Long accountNo, int daUseTimes) {
        return trafficMapper.update(null,new UpdateWrapper<TrafficDO>()
                .eq("account_no",accountNo)
                .eq("id",currentTrafficId)
                .set("day_used",daUseTimes));
    }
}
