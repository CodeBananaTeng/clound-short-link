package com.yulin.utils;

import com.yulin.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/20
 * @Description:
 */
@Slf4j
public class JWTUtil {

    /**
     * 主题
     */
    private static final String SUBJECT = "yulin";

    /**
     * 加密秘钥
     */
    private static final String SECRET = "com.yulin";

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "tcloud-link";

    /**
     * 过期时间,时间是7天
     */
    private static final long EXPIRE = 1000 * 60 * 60 *24 * 7;

    /**
     * 生成token
     * @return
     */
    public static String geneJsonWebToken(LoginUser loginUser){
        if (loginUser == null){
            throw new NullPointerException("对象为空");
        }
        String token = Jwts.builder().setSubject(SUBJECT)
                //配置payload
                .claim("head_img", loginUser.getHeadImg())
                .claim("account_no", loginUser.getAccountNo())
                .claim("username", loginUser.getUsername())
                .claim("mail", loginUser.getMail())
                .claim("phone", loginUser.getPhone())
                .claim("auth", loginUser.getAuth())
                //发布时间
                .setIssuedAt(new Date())
                //设置过期时间
                .setExpiration(new Date(CommonUtil.getCurrentTimestamp() + EXPIRE))
                //设置签名算法
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
        //拼接token
        token = TOKEN_PREFIX + token;
        return token;
    }

    /**
     * 解密token
     * @param token
     * @return
     */
    public static Claims checkJWT(String token){
        try{
            final Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJwt(token.replace(TOKEN_PREFIX, "")).getBody();
            return claims;
        }catch (Exception e){
            log.error("JWT解密失败");
            return null;
        }

    }

}
