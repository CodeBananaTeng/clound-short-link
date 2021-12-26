package com.yulin.service;

import com.yulin.controller.request.LinkGroupAddRequest;
import com.yulin.controller.request.LinkGroupUpdateRequest;
import com.yulin.vo.LinkGroupVO;

import java.util.List;

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

    /**
     * 详情
     * @param groupId
     * @return
     */
    LinkGroupVO detail(Long groupId);

    /**
     * 列出用户全部分组
     * @return
     */
    List<LinkGroupVO> listAllGroup();

    /**
     * 更新组名id
     * @param request
     * @return
     */
    int updateById(LinkGroupUpdateRequest request);

}
