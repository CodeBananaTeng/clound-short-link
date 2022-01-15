package com.yulin.service;

import com.yulin.controller.request.ShortLinkAddRequest;
import com.yulin.controller.request.ShortLinkDelRequest;
import com.yulin.controller.request.ShortLinkPageRequest;
import com.yulin.controller.request.ShortLinkUpdateRequest;
import com.yulin.model.EventMessage;
import com.yulin.utils.JsonData;
import com.yulin.vo.ShortLinkVO;

import java.util.Map;

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

    /**
     * 处理新增短链消息
     * @param eventMessage
     * @return
     */
    boolean handlerAddShortLink(EventMessage eventMessage);

    /**
     * 分页查找短链
     * @param request
     * @return
     */
    Map<String, Object> apgeByGroupId(ShortLinkPageRequest request);

    /**
     * 删除短链
     * @param request
     * @return
     */
    JsonData del(ShortLinkDelRequest request);

    /**
     * 更新短链信息
     * @param request
     * @return
     */
    JsonData update(ShortLinkUpdateRequest request);

}
