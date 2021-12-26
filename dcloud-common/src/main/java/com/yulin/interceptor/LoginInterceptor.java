package com.yulin.interceptor;

import com.yulin.enums.BizCodeEnum;
import com.yulin.model.LoginUser;
import com.yulin.utils.CommonUtil;
import com.yulin.utils.JWTUtil;
import com.yulin.utils.JsonData;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.security.auth.Login;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/20
 * @Description:
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())){
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }
        String accessToken = request.getHeader("token");
        if (StringUtils.isBlank(accessToken)){
            accessToken = request.getParameter("token");
        }

        if (StringUtils.isNoneBlank(accessToken)){
            //解密token
            Claims claims = JWTUtil.checkJWT(accessToken);
            if (claims == null){
                //未登录
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
                return false;
            }
            Long account_no = Long.parseLong(claims.get("account_no").toString());
            String headImg = (String) claims.get("head_img");
            String username = (String) claims.get("username");
            String phone = (String) claims.get("phone");
            String mail = (String) claims.get("mail");
            String auth = (String) claims.get("auth");
            LoginUser loginUser = LoginUser.builder()
                    .accountNo(account_no)
                    .auth(auth).phone(phone)
                    .headImg(headImg)
                    .mail(mail)
                    .username(username)
                    .build();
//            request.setAttribute("loginUser",loginUser);
            //使用threadlocal进行存储
            threadLocal.set(loginUser);
            return true;
        }
        CommonUtil.sendJsonMessage(response,JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }
}
