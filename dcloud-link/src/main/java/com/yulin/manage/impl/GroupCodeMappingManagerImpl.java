package com.yulin.manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yulin.enums.ShortLinkStateEnum;
import com.yulin.manage.GroupCodeMappingManager;
import com.yulin.mapper.GroupCodeMappingMapper;
import com.yulin.model.GroupCodeMappingDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/2
 * @Description:
 */
@Component
@Slf4j
public class GroupCodeMappingManagerImpl implements GroupCodeMappingManager {

    @Autowired
    private GroupCodeMappingMapper groupCodeMappingMapper;

    @Override
    public GroupCodeMappingDO findByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId) {
        GroupCodeMappingDO groupCodeMappingDO = groupCodeMappingMapper.selectOne(new QueryWrapper<GroupCodeMappingDO>()
                .eq("id", mappingId)
                //分片键
                .eq("account_no", accountNo)
                //分表键
                .eq("group_id", groupId));
        return groupCodeMappingDO;
    }

    @Override
    public int add(GroupCodeMappingDO groupCodeMappingDO) {
        return groupCodeMappingMapper.insert(groupCodeMappingDO);
    }

    @Override
    public int del(String shortLinkedCode, Long accountNo, Long groupId) {
        int rows = groupCodeMappingMapper.update(null, new UpdateWrapper<GroupCodeMappingDO>()
                .eq("code", shortLinkedCode)
                .eq("group_id", groupId)
                .set("del", 1));
        return rows;
    }

    @Override
    public Map<String, Object> pageShortLinkByGroupId(Integer page, Integer size, Long accountNo, Long groupId) {
        return null;
    }

    @Override
    public int updateGroupCodeMappingState(Long accountNo, Long groupId, String shortLinkcODE, ShortLinkStateEnum shortLinkStateEnum) {
        return 0;
    }
}
