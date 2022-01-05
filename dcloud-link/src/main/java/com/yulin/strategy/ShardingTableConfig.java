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


    //配置启用哪些表的前缀
    static {
        tablePrefix.add("0");
        tablePrefix.add("a");
    }

    /**
     * 获取随机的前缀
     * @return
     */
    public static String getRandomTablePrefix(String code){
        int hashCode = code.hashCode();
        int index = Math.abs(hashCode) % tablePrefix.size();
        return tablePrefix.get(index);
    }
}
