package com.yulin.service.impl;

import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manager.DomainManager;
import com.yulin.model.DomainDO;
import com.yulin.service.DomainService;
import com.yulin.vo.DomainVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
@Service
@Slf4j
public class DomainServiceImpl implements DomainService {

    @Autowired
    private DomainManager domainManager;

    @Override
    public List<DomainVO> listAll() {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        List<DomainDO> customDomainList = domainManager.listCustomDomain(accountNo);
        List<DomainDO> officialDomainList = domainManager.listOfficialDomain();
        customDomainList.addAll(officialDomainList);
        return customDomainList.stream().map(obj->beanProcess(obj)).collect(Collectors.toList());
    }


    private DomainVO beanProcess(DomainDO domainDO){
        DomainVO domainVO = new DomainVO();
        BeanUtils.copyProperties(domainDO,domainVO);
        return domainVO;
    }
}
