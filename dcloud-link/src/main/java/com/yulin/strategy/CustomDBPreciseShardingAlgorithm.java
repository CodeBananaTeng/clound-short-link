package com.yulin.strategy;

import com.yulin.enums.BizCodeEnum;
import com.yulin.exception.BizException;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
public class CustomDBPreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {

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

        //获取短链码第一位，即库位进行判断
        String codePrefix = shardingValue.getValue().substring(0,1);

        for (String targetName: availableTargetNames){
            //获取库名的最后一位，真实配置的ds
            String targetNameSuffix = targetName.substring(targetName.length() - 1);
            //如果一致就返回
            if (codePrefix.equals(targetNameSuffix)){
                return targetName;
            }
        }

        //抛异常
        throw new BizException(BizCodeEnum.DB_ROUTE_NOT_FOUND);
    }

}
