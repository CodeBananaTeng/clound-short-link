package com.yulin.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description: 短链码的前缀，用于查看是存储在那个库里面
 */
public class ShardingDBConfig {

    /**
     * 存储数据库位置编号
     */
    private static final List<String> dbPrefix = new ArrayList<>();

    private static final Random random = new Random();

    //配置启用哪些库的前缀
    static {
        dbPrefix.add("0");
        dbPrefix.add("1");
        dbPrefix.add("a");
    }

    /**
     * 获取随机的前缀
     * @return
     */
    public static String getRandomDBPrefix(){
        int index = random.nextInt(dbPrefix.size());
        return dbPrefix.get(index);
    }
}
