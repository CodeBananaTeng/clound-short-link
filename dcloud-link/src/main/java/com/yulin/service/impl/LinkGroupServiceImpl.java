package com.yulin.service.impl;

import com.yulin.controller.request.LinkGroupAddRequest;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manage.LinkGroupManager;
import com.yulin.model.LinkGroupDO;
import com.yulin.service.LinkGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
@Service
@Slf4j
public class LinkGroupServiceImpl implements LinkGroupService {

    @Autowired
    private LinkGroupManager linkGroupManager;

    @Override
    public int add(LinkGroupAddRequest addRequest) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        LinkGroupDO linkGroupDO = new LinkGroupDO();
        linkGroupDO.setTitle(addRequest.getTitle());
        linkGroupDO.setAccountNo(accountNo);
        int rows = linkGroupManager.add(linkGroupDO);
        return rows;
    }

    @Override
    public int del(Long groupId) {

        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();


        return linkGroupManager.del(groupId,accountNo);
    }

}
