package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yulin.enums.ShortLinkStateEnum;
import com.yulin.manager.GroupCodeMappingManager;
import com.yulin.mapper.GroupCodeMappingMapper;
import com.yulin.model.GroupCodeMappingDO;
import com.yulin.vo.GroupCodeMappingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        Page<GroupCodeMappingDO> pageInfo = new Page<>(page,size);
        Page<GroupCodeMappingDO> groupCodeMappingDOPage = groupCodeMappingMapper.selectPage(pageInfo, new QueryWrapper<GroupCodeMappingDO>()
                //分片键
                .eq("account_no", accountNo)
                //分表键
                .eq("group_id", groupId));
        Map<String,Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record",groupCodeMappingDOPage.getTotal());
        pageMap.put("total_page",groupCodeMappingDOPage.getPages());
        pageMap.put("current_data",groupCodeMappingDOPage.getRecords()
                .stream().map(obj->beaProcess(obj)).collect(Collectors.toList()));
        return pageMap;
    }



    @Override
    public int updateGroupCodeMappingState(Long accountNo, Long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum) {
        int rows = groupCodeMappingMapper.update(null, new UpdateWrapper<GroupCodeMappingDO>()
                .eq("code", shortLinkCode)
                .eq("group_id", groupId)
                .set("status", shortLinkStateEnum.name()));
        return rows;
    }

    private GroupCodeMappingVO beaProcess(GroupCodeMappingDO groupCodeMappingDO) {
        GroupCodeMappingVO groupCodeMappingVO = new GroupCodeMappingVO();
        BeanUtils.copyProperties(groupCodeMappingDO,groupCodeMappingVO);
        return groupCodeMappingVO;
    }
}