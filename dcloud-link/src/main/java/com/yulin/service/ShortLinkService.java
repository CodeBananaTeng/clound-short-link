package com.yulin.service;

import com.yulin.vo.ShortLinkVO;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/27
 * @Description:
 */
public interface ShortLinkService {

    /**
     * 解析短链
     * @param shortLinkCode
     * @return
     */
    ShortLinkVO parseShortLinkCode(String shortLinkCode);

}
