package com.yulin.service;

import com.yulin.controller.request.LinkGroupAddRequest;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
public interface LinkGroupService {

    /**
     * 新增分组
     * @param addRequest
     * @return
     */
    int add(LinkGroupAddRequest addRequest);

    /**
     * 删除分组
     * @param groupId
     * @return
     */
    int del(Long groupId);

}
