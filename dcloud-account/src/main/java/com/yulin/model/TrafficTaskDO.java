package com.yulin.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author yulin
 * @since 2021-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("traffic_task")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long accountNo;

    private Long trafficId;

    private Integer useTimes;

    /**
     * 锁定状态锁定
LOCK 完成FINISH-取消CANCEL
     */
    private String lockState;

    /**
     * 唯⼀标识
     */
    private String bizId;

    private Date gmtCreate;

    private Date gmtModified;


}
