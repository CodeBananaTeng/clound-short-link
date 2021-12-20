package com.yulin.controller;

import com.google.code.kaptcha.Producer;
import com.yulin.controller.request.SendCodeRequest;
import com.yulin.enums.BizCodeEnum;
import com.yulin.enums.SengCodeEnum;
import com.yulin.service.NotifyService;
import com.yulin.utils.CommonUtil;
import com.yulin.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
@RestController
@RequestMapping("/api/notify/v1")
@Slf4j
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 验证码过期时间
     */
    private static final long CAPTCHA_CODE_EXPIRED = 1000 * 10 * 60;

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response){
        String captchaText = captchaProducer.createText();
        log.info("验证码是 :{}",captchaText);
        //存储redis，配置过期时间 TODO
        stringRedisTemplate.opsForValue().set(getCaptchaKey(request),captchaText,CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);
        BufferedImage bufferedImage = captchaProducer.createImage(captchaText);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage,"jpg",outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("获取输出流出错 :{}",e.getMessage());
        }
    }

    /**
     * 发送短信接口
     * @return
     */
    @PostMapping("/send_code")
    public JsonData sendCode(@RequestBody SendCodeRequest sendCodeRequest,HttpServletRequest request){

        String key = getCaptchaKey(request);

        String cacheCaptcha = stringRedisTemplate.opsForValue().get(key);
        String captcha = sendCodeRequest.getCaptcha();
        if (captcha != null && cacheCaptcha != null && cacheCaptcha.equalsIgnoreCase(captcha)){
            //成功
            stringRedisTemplate.delete(key);
            JsonData jsonData = notifyService.sendCode(SengCodeEnum.USER_REGISTER,sendCodeRequest.getTo());
            return jsonData;
        }else {
            //失败
            return JsonData.buildResult(BizCodeEnum.CODE_CAPTCHA_ERROR);
        }

    }

    private String getCaptchaKey(HttpServletRequest request){
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String key = "account-service" + CommonUtil.MD5(ip+userAgent);
        log.info("验证码 :{}",key);
        return key;
    }

}
