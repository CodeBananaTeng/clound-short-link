package com.yulin.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author yulin
 * @since 2021-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LinkGroupVO implements Serializable {


    private Long id;

    /**
     * 组名
     */
    private String title;

    /**
     * 账号唯⼀编
号
     */
    private Long accountNo;

    private Date gmtCreate;

    private Date gmtModified;


}
