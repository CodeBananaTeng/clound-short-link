package com.yulin.manager;

import com.yulin.model.TrafficTaskDO;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/24
 * @Description:
 */
public interface TrafficTaskManager {

    /**
     * 添加
     */
    int add(TrafficTaskDO trafficTaskDO);

    TrafficTaskDO findByIdAndAccountNo(Long id,Long accountNo);

    int deleteByIdAndAccountNo(Long id,Long accountNo);


}
