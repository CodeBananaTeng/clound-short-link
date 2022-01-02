package com.yulin.manage;

import com.yulin.enums.ShortLinkStateEnum;
import com.yulin.model.GroupCodeMappingDO;

import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/2
 * @Description:
 */
public interface GroupCodeMappingManager {

    /**
     * 查找详情
     * @param mappingId
     * @param accountNo
     * @param groupId
     * @return
     */
    GroupCodeMappingDO findByGroupIdAndMappingId(Long mappingId,Long accountNo,Long groupId);

    /**
     * 新增
     * @param groupCodeMappingDO
     * @return
     */
    int add(GroupCodeMappingDO groupCodeMappingDO);

    /**
     * 根据短链码删除
     * @param shortLinkedCode
     * @param accountNo
     * @param groupId
     * @return
     */
    int del(String shortLinkedCode,Long accountNo,Long groupId);

    /**
     * 分页查找
     * @param page
     * @param size
     * @param accountNo
     * @param groupId
     * @return
     */
    Map<String,Object> pageShortLinkByGroupId(Integer page,Integer size,Long accountNo,Long groupId);

    /**
     * 更新短链码状态
     * @param accountNo
     * @param groupId
     * @param shortLinkcODE
     * @param shortLinkStateEnum
     * @return
     */
    int updateGroupCodeMappingState(Long accountNo, Long groupId, String shortLinkcODE, ShortLinkStateEnum shortLinkStateEnum);

}
