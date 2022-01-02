package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yulin.enums.DomainTypeEnum;
import com.yulin.manager.DomainManager;
import com.yulin.mapper.DomainMapper;
import com.yulin.model.DomainDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
@Component
@Slf4j
public class DomainManagerImpl implements DomainManager {

    @Autowired
    private DomainMapper domainMapper;

    @Override
    public DomainDO findById(Long id, Long accountNo) {
        return domainMapper.selectOne(new QueryWrapper<DomainDO>().eq("id",id).eq("account_no",accountNo));
    }

    @Override
    public DomainDO findByDomainTypeAndId(Long id, DomainTypeEnum domainTypeEnum) {
        return domainMapper.selectOne(new QueryWrapper<DomainDO>().eq("id",id).eq("domain_type",domainTypeEnum.name()));
    }

    @Override
    public int addDomain(DomainDO domainDO) {
        return domainMapper.insert(domainDO);
    }

    @Override
    public List<DomainDO> listOfficialDomain() {
        return domainMapper.selectList(new QueryWrapper<DomainDO>().eq("domain_type",DomainTypeEnum.OFFICIAL.name()));
    }

    @Override
    public List<DomainDO> listCustomDomain(Long accounNo) {
        return domainMapper.selectList(new QueryWrapper<DomainDO>()
                .eq("domain_type",DomainTypeEnum.CUSTOM.name())
                .eq("account_no",accounNo));
    }
}
