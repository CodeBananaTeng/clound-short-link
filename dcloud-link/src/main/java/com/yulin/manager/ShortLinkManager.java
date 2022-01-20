package com.yulin.manager;

import com.yulin.model.ShortLinkDO;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
public interface ShortLinkManager {

    /**
     * 新增短链码
     * @param shortLinkDO
     * @return
     */
    int addShortLink(ShortLinkDO shortLinkDO);

    /**
     * 根据短链码寻找短链
     * @param shortLinkCode
     * @return
     */
    ShortLinkDO findByShortLinkCode(String shortLinkCode);

    /**
     * 删除
     * @param shortLinkDO
     * @return
     */
    int del(ShortLinkDO shortLinkDO);

    /**
     * 更新
     * @param shortLinkDO
     * @return
     */
    int update(ShortLinkDO shortLinkDO);
}
