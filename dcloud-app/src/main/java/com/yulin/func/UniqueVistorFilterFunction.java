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

public class UniqueVistorFilterFunction extends RichFilterFunction<JSONObject> {

    private ValueState<String> lastVistDateState = null;

    @Override
    public void open(Configuration parameters) throws Exception {

        ValueStateDescriptor<String> visitDateStateDes = new ValueStateDescriptor<>("visitDateState", String.class);
        //统计UV
        StateTtlConfig stateTtlConfig = StateTtlConfig.newBuilder(Time.days(1)).build();
        visitDateStateDes.enableTimeToLive(stateTtlConfig);

        this.lastVistDateState = getRuntimeContext().getState(visitDateStateDes);

    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    /**
     * 初次访问的时候就是为true
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @Override
    public boolean filter(JSONObject jsonObject) throws Exception {
        //获取当前访问时间
        Long visitTime = jsonObject.getLong("visitTime");
        String udid = jsonObject.getString("udid");
        //当前访问时间
        String currentVisitDate = TimeUtil.format(visitTime);

        //获取上次状态存储的访问时间
        String lastVisitDate = lastVistDateState.value();
        if (StringUtils.isNoneBlank(lastVisitDate) && currentVisitDate.equalsIgnoreCase(lastVisitDate)){
            System.out.println("udid 已经在 "+ currentVisitDate + "时间访问过");
            return false;
        }else {
            System.out.println(udid + "在" + currentVisitDate + "初次访问");
            lastVistDateState.update(currentVisitDate);
        }
        return true;
    }
}
