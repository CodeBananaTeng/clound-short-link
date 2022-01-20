package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yulin.manager.ShortLinkManager;
import com.yulin.mapper.ShortLinkMapper;
import com.yulin.model.ShortLinkDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
@Component
@Slf4j
public class ShortLinkManagerImpl implements ShortLinkManager {

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Override
    public int addShortLink(ShortLinkDO shortLinkDO) {
        return shortLinkMapper.insert(shortLinkDO);
    }

    @Override
    public ShortLinkDO findByShortLinkCode(String shortLinkCode) {
        return shortLinkMapper.selectOne(new QueryWrapper<ShortLinkDO>().eq("code",shortLinkCode));
    }

    @Override
    public int del(ShortLinkDO shortLinkDO) {

        int rows = shortLinkMapper.update(null, new UpdateWrapper<ShortLinkDO>()
                .eq("code", shortLinkDO.getCode())
                .set("del",1));
        return rows;
    }

    @Override
    public int update(ShortLinkDO shortLinkDO) {
        int rows = shortLinkMapper.update(null, new UpdateWrapper<ShortLinkDO>()
                .eq("code", shortLinkDO.getCode())
                .eq("del", 0)

                .set("title", shortLinkDO.getTitle())
                .set("domain", shortLinkDO.getDomain()));
        return rows;
    }
}
