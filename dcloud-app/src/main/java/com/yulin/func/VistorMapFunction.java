package com.yulin.func;

import com.alibaba.fastjson.JSONObject;
import com.yulin.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/10
 * @Description:
 */
@Slf4j
public class VistorMapFunction extends RichMapFunction<JSONObject,String> {

    //记录用户的udid访问
    private ValueState<String> newDayVistorState;

    @Override
    public void open(Configuration parameters) throws Exception {
        //对状态做初始化
        newDayVistorState = getRuntimeContext().getState(new ValueStateDescriptor<String>("newDayVistorState",String.class));
    }

    @Override
    public String map(JSONObject value) throws Exception {
        //获取之前是否有访问日期
        String beforeDateState  = newDayVistorState.value();

        //获取当前时间戳
        Long ts = value.getLong("ts");
        String currentDateStr = TimeUtil.formatWithTime(ts);

        //判断日期是否为空进行新老访客识别
        if (StringUtils.isNotBlank(beforeDateState)){
            //如果不为空那就不是新访客
            //如果存在，如果是同一天的数据，那就是老访客，如果不是同一天的数据就不是新访客
            if (beforeDateState.equalsIgnoreCase(currentDateStr)){
                //一样是老访客
                value.put("is_new",0);
            }else {
                //日期不一样，则是新访客
                value.put("is_new",1);
                //类似于数据库中插入一个新的记录
                newDayVistorState.update(currentDateStr);
                log.info("新访客:{}",currentDateStr);
            }
        }else {
            //如果为空那就是新访客 如果状态为空就是新用户，标记1 老访客标记0
            value.put("is_new",1);
            //类似于数据库中插入一个新的记录
            newDayVistorState.update(currentDateStr);
            log.info("新访客:{}",currentDateStr);
        }

        return value.toJSONString();
    }

}
