package com.yulin.func;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yulin.model.DevicesInfoDO;
import com.yulin.model.ShortLinkWideAppDO;
import com.yulin.util.DeviceUtil;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/12
 * @Description:
 */
public class DeviceMapFunction implements MapFunction<String, ShortLinkWideAppDO> {
    @Override
    public ShortLinkWideAppDO map(String value) throws Exception {
        //还原JSON对象
        JSONObject jsonObject = JSON.parseObject(value);
        String userAgent = jsonObject.getJSONObject("data").getString("user-agent");

        DevicesInfoDO deviceInfo = DeviceUtil.getDeviceInfo(userAgent);


        //构造宽表对象
        ShortLinkWideAppDO shortLinkWideAppDO = ShortLinkWideAppDO.builder()
                .accountNo(jsonObject.getJSONObject("data").getLong("accountNo"))
                .visitTime(jsonObject.getLong("ts"))
                .code(jsonObject.getString("bizId"))
                .referer(jsonObject.getString("referer"))
                .isNew(jsonObject.getInteger("is_new"))
                .ip(jsonObject.getString("ip"))
                //设备信息补齐
                .browserName(deviceInfo.getBrowserName())
                .os(deviceInfo.getOs())
                .osVersion(deviceInfo.getOsVersion())
                .deviceType(deviceInfo.getDeviceType())
                .deviceManufacturer(deviceInfo.getDeviceManufacturer())
                .udid(jsonObject.getString("udid"))
                .build();
        return shortLinkWideAppDO;
    }
}
