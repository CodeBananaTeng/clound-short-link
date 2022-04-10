package com.yulin.vo;

import com.yulin.model.TrafficDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/10
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTrafficVO {

    /**
     * 天可用总次数 = 总次数 - 已用
     */
    private Integer dayTotalLeftTimes;

    /**
     * 当前使用的流量包
     */
    private TrafficDO currentTrafficDO;

    /**
     * 记录没过期但是今天没更新的流量包id
     */
    private List<Long> unUpdatedTrafficIds;


}
