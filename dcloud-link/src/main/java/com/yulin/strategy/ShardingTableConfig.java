package com.yulin.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description: 短链码的后缀，用于存储在那个表中
 */
public class ShardingTableConfig {

    /**
     * 存储数据表位置编号
     */
    private static final List<String> tablePrefix = new ArrayList<>();

    private static final Random random = new Random();

    //配置启用哪些表的前缀
    static {
        tablePrefix.add("0");
        tablePrefix.add("a");
    }

    /**
     * 获取随机的前缀
     * @return
     */
    public static String getRandomTablePrefix(){
        int index = random.nextInt(tablePrefix.size());
        return tablePrefix.get(index);
    }
}
