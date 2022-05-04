package com.yulin.service;

import com.yulin.utils.JsonData;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/4
 * @Description:
 */
public interface LogService {

    /**
     * 记录日志
     * @param request
     * @param shortLinkCode
     * @param accountNo
     * @return
     */
    void recordShortLinkLog(HttpServletRequest request,String shortLinkCode,Long accountNo);

}
