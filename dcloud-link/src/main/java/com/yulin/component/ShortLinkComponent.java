package com.yulin.component;

import com.yulin.strategy.ShardingDBConfig;
import com.yulin.strategy.ShardingTableConfig;
import com.yulin.utils.CommonUtil;
import org.springframework.stereotype.Component;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
@Component
public class ShortLinkComponent {

    //62个字符
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 生成短链码
     * @param param
     * @return
     */
    public String createShortLinkCode(String param){
        long murmurHash32 = CommonUtil.murmurHash32(param);
        //进行进制转换
        String code = encodeToBase62(murmurHash32);
        String shortLinkCode = ShardingDBConfig.getRandomDBPrefix(code) + code + ShardingTableConfig.getRandomTablePrefix(code);
        return shortLinkCode;
    }

    /**
     * 10进制转62进制
     * @param num
     * @return
     */
    private String encodeToBase62(long num){

        //线程安全的
        StringBuffer sb = new StringBuffer();
        do {
            int i = (int)(num%62);
            sb.append(CHARS.charAt(i));
            num  = num / 62;
        }while (num>0);
        String value = sb.reverse().toString();
        return value;
    }

}
