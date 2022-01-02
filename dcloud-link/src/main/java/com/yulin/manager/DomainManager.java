package com.yulin.manager;

import com.yulin.enums.DomainTypeEnum;
import com.yulin.model.DomainDO;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
public interface DomainManager {

    /**
     * 查找详情
     * @param id
     * @param accountNo
     * @return
     */
    DomainDO findById(Long id,Long accountNo);

    /**
     * 查找详情
     * @param id
     * @param domainTypeEnum
     * @return
     */
    DomainDO findByDomainTypeAndId(Long id, DomainTypeEnum domainTypeEnum);

    /**
     * 新增
     * @param domainDO
     * @return
     */
    int addDomain(DomainDO domainDO);

    /**
     * 列举全部官方域名
     * @return
     */
    List<DomainDO> listOfficialDomain();

    /**
     * 列举全部自定义域名
     * @return
     */
    List<DomainDO> listCustomDomain(Long accounNo);
}
