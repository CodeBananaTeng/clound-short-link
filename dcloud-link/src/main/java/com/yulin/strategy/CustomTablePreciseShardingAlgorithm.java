package com.yulin.strategy;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
public class CustomTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    /**
     * @param availableTargetNames 数据源集合
     *          在分库时值为所有分⽚库的集合 databaseNames
     *          分表时为对应分⽚库中所有分⽚表的集合 tablesNames
     * @param shardingValue 分⽚属性，包括
     *          logicTableName 为逻辑表，
     *          columnName 分⽚健（字段），
     *          value 为从 SQL 中解析出的分⽚健的值
     * @return
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        //获取逻辑表名
        String targetName = availableTargetNames.iterator().next();

        //短链码 A23Ad1
        String value = shardingValue.getValue();

        //短链码最后一位 根据上面的就是1
        String codeSuffix = value.substring(value.length() - 1);

        //拼接Actual table
        return targetName+"_"+codeSuffix;
    }

}
