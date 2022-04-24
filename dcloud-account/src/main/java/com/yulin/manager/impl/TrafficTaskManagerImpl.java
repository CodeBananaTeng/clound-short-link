package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yulin.manager.TrafficTaskManager;
import com.yulin.mapper.TrafficMapper;
import com.yulin.mapper.TrafficTaskMapper;
import com.yulin.model.TrafficTaskDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/24
 * @Description:
 */
@Component
@Slf4j
public class TrafficTaskManagerImpl implements TrafficTaskManager {

    @Autowired
    private TrafficTaskMapper trafficTaskMapper;

    @Override
    public int add(TrafficTaskDO trafficTaskDO) {
        return trafficTaskMapper.insert(trafficTaskDO);
    }

    @Override
    public TrafficTaskDO findByIdAndAccountNo(Long id, Long accountNo) {
        TrafficTaskDO trafficTaskDO = trafficTaskMapper.selectOne(new QueryWrapper<TrafficTaskDO>()
                .eq("id", id)
                .eq("account_no", accountNo));
        return trafficTaskDO;
    }

    @Override
    public int deleteByIdAndAccountNo(Long id, Long accountNo) {
        return trafficTaskMapper.delete(new QueryWrapper<TrafficTaskDO>()
                .eq("id", id)
                .eq("account_no", accountNo));
    }
}
