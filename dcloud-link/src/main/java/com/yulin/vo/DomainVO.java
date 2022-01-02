package com.yulin.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2022-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DomainVO implements Serializable {


    private Long id;

    /**
     * ⽤户⾃⼰绑
定的域名
     */
    private Long accountNo;

    /**
     * 域名类型，⾃
建custom, 官⽅offical
     */
    private String domainType;

    private String value;

    /**
     * 0是默认，1是禁⽤
     */
    private Integer del;

    private Date gmtCreate;

    private Date gmtModified;


}
