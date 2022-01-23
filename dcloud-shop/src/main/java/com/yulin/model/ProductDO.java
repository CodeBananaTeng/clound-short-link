package com.yulin.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yulin
 * @since 2022-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product")
public class ProductDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 详情
     */
    private String detail;

    /**
     * 图⽚
     */
    private String img;

    /**
     * 产品层级：FIRST⻘铜、
SECOND⻩⾦、THIRD钻⽯
     */
    private String level;

    /**
     * 原 价
     */
    private BigDecimal oldAmount;

    /**
     * 现价
     */
    private BigDecimal amount;

    /**
     * ⼯具类型
short_link、qrcode
     */
    private String pluginType;

    /**
     * ⽇次数：短链类
型
     */
    private Integer dayTimes;

    /**
     * 总次数：活码
才有
     */
    private Integer totalTimes;

    /**
     * 有效天数
     */
    private Integer validDay;

    private Date gmtModified;

    private Date gmtCreate;


}
