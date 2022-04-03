package com.yulin.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrafficVO implements Serializable {


    private Long id;

    /**
     * 每天限制多少
条，短链
     */
    private Integer dayLimit;

    /**
     * 当天⽤了多少条，
短链
     */
    private Integer dayUsed;

    /**
     * 总次数，活码
才⽤
     */
    private Integer totalLimit;

    /**
     * 账号
     */
    private Long accountNo;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 产品层级：FIRST⻘铜、SECOND⻩⾦、THIRD钻⽯
     */
    private String level;

    /**
     * 过期⽇期
     */
    private Date expiredDate;

    /**
     * 插件类型
     */
    private String pluginType;

    /**
     * 商品主键
     */
    private Long productId;




}
