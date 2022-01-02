package com.yulin.manager;

import com.yulin.model.LinkGroupDO;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
public interface LinkGroupManager {
    int add(LinkGroupDO linkGroupDO);

    int del(Long groupId, long accountNo);

    LinkGroupDO detail(Long groupId, long accountNo);

    List<LinkGroupDO> listAllGroup(long accountNo);

    int updateById(LinkGroupDO linkGroupDO);
}
