package com.yulin.controller;

import com.yulin.enums.ShortLinkStateEnum;
import com.yulin.service.ShortLinkService;
import com.yulin.utils.CommonUtil;
import com.yulin.vo.ShortLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * g1.fit/asd2A
 * @Auther:LinuxTYL
 * @Date:2021/12/27
 * @Description:
 */
@Controller
@Slf4j
public class LinkApiController {

    @Autowired
    private ShortLinkService shortLinkService;

    /**
     * 解析 301还是302，这边是返回http code是302
     * <p>
     * 知识点⼀，为什么要⽤ 301 跳转⽽不是 302 呐？
     * <p>
     * 301 是永久᯿定向，302 是临时᯿定向。
     * <p>
     * 短地址⼀经⽣成就不会变化，所以⽤ 301 是同时对服务器压
     ⼒也会有⼀定减少
     * <p>
     * 但是如果使⽤了 301，⽆法统计到短地址被点击的次数。
     * <p>
     * 所以选择302虽然会增加服务器压⼒，但是有很多数据可以获
     取进⾏分析
     *
     * @param shortLinkCode
     * @return
     */
    @GetMapping(path = "/{shortLinkCode}")
    public void dispatch(@PathVariable(name = "shortLinkCode") String shortLinkCode,
                         HttpServletRequest request, HttpServletResponse response){
        try {


            log.info("短链码:{}", shortLinkCode);
            //判断短链码是否合规
            if (isLetterDigit(shortLinkCode)) {
                //查找短链 TODO
                ShortLinkVO shortLinkVO = shortLinkService.parseShortLinkCode(shortLinkCode);
                //判断是否过期过着可用
                if (isVisitable(shortLinkVO)) {
                    String originalUrl = CommonUtil.removeUrlPrefix(shortLinkVO.getOriginalUrl());

                    //可用进行跳转
                    response.setHeader("Location", originalUrl);

                    //302跳转
                    response.setStatus(HttpStatus.FOUND.value());
                } else {
                    //不能跳转
                    response.setStatus(HttpStatus.NOT_FOUND.value());

                }
            }
        }catch (Exception e){
            //错误返回错误信息
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * 判断短链是否可用
     * @param shortLinkVO
     * @return
     */
    private static boolean isVisitable(ShortLinkVO shortLinkVO){
        if (shortLinkVO.getExpired().getTime() == -1) {
            if (ShortLinkStateEnum.ACTIVE.name().equalsIgnoreCase(shortLinkVO.getState())) {

            }
        } else if (shortLinkVO != null && shortLinkVO.getExpired().getTime() > CommonUtil.getCurrentTimestamp()) {
            if (ShortLinkStateEnum.ACTIVE.name().equalsIgnoreCase(shortLinkVO.getState())) {

            }
        }
        return false;
    }

    /**
     * 仅包括数字和字母
     * @param str
     * @return
     */
    private static boolean isLetterDigit(String str){
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }

}
