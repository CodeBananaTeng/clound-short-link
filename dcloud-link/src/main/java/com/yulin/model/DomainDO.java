package com.yulin.model;

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
 * @since 2022-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("domain")
public class DomainDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
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
