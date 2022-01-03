package com.yulin.service;

import com.yulin.controller.request.ShortLinkAddRequest;
import com.yulin.utils.JsonData;
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

    /**
     * 创建短链
     * @param request
     * @return
     */
    JsonData createShortLink(ShortLinkAddRequest request);

}
