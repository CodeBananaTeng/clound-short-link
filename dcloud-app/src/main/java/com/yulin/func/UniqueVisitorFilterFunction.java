package com.yulin.func;

import com.alibaba.fastjson.JSONObject;
import com.yulin.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/16
 * @Description:
 */
public class UniqueVisitorFilterFunction extends RichFilterFunction<JSONObject> {

    private ValueState<String> lastVisitDateState = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<String> visitDateDes = new ValueStateDescriptor<>("visitDate", String.class);
        //统计UV
        StateTtlConfig stateTtlConfig = StateTtlConfig.newBuilder(Time.days(1)).build();
        visitDateDes.enableTimeToLive(stateTtlConfig);
        this.lastVisitDateState = getRuntimeContext().getState(visitDateDes);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public boolean filter(JSONObject jsonObject) throws Exception {
        //获取当前访问时间
        Long visitTime = jsonObject.getLong("visitTime");
        String udid = jsonObject.getString("udid");
        //当前访问时间
        String currentVisitDate = TimeUtil.formatWithTime(visitTime);
        //获取状态存储的上次访问时间
        String lastVisitDate = lastVisitDateState.value();
        if (StringUtils.isNotBlank(lastVisitDate) && currentVisitDate.equalsIgnoreCase(lastVisitDate)){
            //已经访问过的，就是return false过滤掉
            System.out.println(udid + "已经在" + currentVisitDate+"时间访问过");
            return false;
        }else {
            //这个是初次访问，存储下方便下次处理
            System.out.println(udid + "已经在" + currentVisitDate+"时间初次访问");
            lastVisitDateState.update(currentVisitDate);
            return true;
        }
    }
}
