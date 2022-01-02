package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    public int deli(String shortLinkCode, Long accountNo) {

        ShortLinkDO shortLinkDO = new ShortLinkDO();
        shortLinkDO.setDel(1);

        int rows = shortLinkMapper.update(shortLinkDO, new QueryWrapper<ShortLinkDO>().eq("code", shortLinkCode).eq("account_no", accountNo));
        return rows;
    }
}
