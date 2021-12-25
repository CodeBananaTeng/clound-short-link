package com.yulin.utils;

import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/22
 * @Description:
 */
public class IDUtil {

    private static SnowflakeShardingKeyGenerator shardingKeyGenerator = new SnowflakeShardingKeyGenerator();
    /**
     * 雪花算法⽣成器,配置workId，避免᯿复
     *
     * 10进制 654334919987691526
     * 64位
     0000100100010100101010100010010010010110000000000000
     000000000110
     *
     * {@link SnowFlakeWordIdConfig}
     *
     * @return
     */
    public static Comparable<?> geneSnowFlakeID(){
        return shardingKeyGenerator.generateKey();
    }
}
