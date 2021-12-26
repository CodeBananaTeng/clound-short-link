package com.yulin.manage;

import com.yulin.model.LinkGroupDO;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
public interface LinkGroupManager {
    int add(LinkGroupDO linkGroupDO);

    int del(Long groupId, long accountNo);
}
