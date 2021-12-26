package com.yulin.service.impl;

import com.yulin.controller.request.LinkGroupAddRequest;
import com.yulin.controller.request.LinkGroupUpdateRequest;
import com.yulin.interceptor.LoginInterceptor;
import com.yulin.manage.LinkGroupManager;
import com.yulin.model.LinkGroupDO;
import com.yulin.service.LinkGroupService;
import com.yulin.vo.LinkGroupVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public LinkGroupVO detail(Long groupId) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        LinkGroupDO linkGroupDO = linkGroupManager.detail(groupId,accountNo);
        LinkGroupVO linkGroupVO = new LinkGroupVO();
        //项目比较大的时候map-struct 对象的转换使用
        BeanUtils.copyProperties(linkGroupDO,linkGroupVO);
        return null;
    }

    @Override
    public List<LinkGroupVO> listAllGroup() {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        List<LinkGroupDO> linkGroupDOList = linkGroupManager.listAllGroup(accountNo);
        List<LinkGroupVO> groupVOList = linkGroupDOList.stream().map(obj -> {

            LinkGroupVO linkGroupVO = new LinkGroupVO();
            BeanUtils.copyProperties(obj, linkGroupVO);
            return linkGroupVO;
        }).collect(Collectors.toList());
        return groupVOList;
    }

    @Override
    public int updateById(LinkGroupUpdateRequest request) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        LinkGroupDO linkGroupDO = new LinkGroupDO();
        linkGroupDO.setTitle(request.getTitle());
        linkGroupDO.setId(request.getId());
        linkGroupDO.setAccountNo(accountNo);

        int rows = linkGroupManager.updateById(linkGroupDO);
        return rows;
    }

}
