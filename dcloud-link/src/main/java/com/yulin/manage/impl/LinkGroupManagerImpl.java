package com.yulin.manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yulin.manage.LinkGroupManager;
import com.yulin.mapper.LinkGroupMapper;
import com.yulin.model.LinkGroupDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
@Component
public class LinkGroupManagerImpl implements LinkGroupManager {

    @Autowired
    private LinkGroupMapper linkGroupMapper;

    @Override
    public int add(LinkGroupDO linkGroupDO) {
        return linkGroupMapper.insert(linkGroupDO);
    }

    @Override
    public int del(Long groupId, long accountNo) {

        return linkGroupMapper.delete(new QueryWrapper<LinkGroupDO>().eq("id",groupId).eq("account_no",accountNo));
    }
}
