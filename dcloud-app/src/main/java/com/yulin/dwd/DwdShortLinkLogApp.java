package com.yulin.dwd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yulin.func.VistorMapFunction;
import com.yulin.util.DeviceUtil;
import com.yulin.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import javax.ws.rs.core.UriBuilder;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/9
 * @Description:
 */
@Slf4j
public class DwdShortLinkLogApp {

    /**
     * 定义SOURCE topic
     */
    public static final String SOURCE_TOPIC = "ods_link_visit_topic";

    /**
     * 定义SINK topic
     */
    public static final String SINK_TOPIC = "dwd_link_visit_topic";

    /**
     * 定义消费者组
     */
    public static final String GROUP_ID = "dwd_short_link_group";

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //并行度1
        env.setParallelism(1);

        DataStream<String> ds = env.socketTextStream("127.0.0.1", 8889);
        FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);
        //DataStreamSource<String> ds = env.addSource(kafkaConsumer);
        ds.print();

        //数据补齐，添加唯一标识，referer等
        SingleOutputStreamOperator<JSONObject> jsonDS = ds.flatMap(new FlatMapFunction<String, JSONObject>() {
            @Override
            public void flatMap(String value, Collector<JSONObject> out) throws Exception {
                JSONObject jsonObject = JSON.parseObject(value);
                //生成设备唯一ID
                String udid = getDeviceId(jsonObject);
                jsonObject.put("udid",udid);
                String referer = getReferer(jsonObject);
                jsonObject.put("referer",referer);
                out.collect(jsonObject);
            }
        });
        //分组（同一个用户进行分组 keyBy）
        KeyedStream<JSONObject, Object> keyedStream = jsonDS.keyBy(new KeySelector<JSONObject, Object>() {
            @Override
            public Object getKey(JSONObject value) throws Exception {
                return value.getString("udid");
            }
        });

        //识别 使用richMap open函数 做状态存储的初始化
        SingleOutputStreamOperator<String> jsonWithVistorState = keyedStream.map(new VistorMapFunction());
        jsonWithVistorState.print("ods新来客户");

        //存储到dwd这一层
        FlinkKafkaProducer<String> kafkaProducer = KafkaUtil.getKafkaProducer(SINK_TOPIC);
        jsonWithVistorState.addSink(kafkaProducer);


        env.execute();
    }

    /**
     * 提取referer
     * @param jsonObject
     * @return
     */
    private static String getReferer(JSONObject jsonObject){
        JSONObject dataJsonObj = jsonObject.getJSONObject("data");
        if (dataJsonObj.containsKey("referer")){
            String referer = dataJsonObj.getString("referer");
            if (StringUtils.isNotBlank(referer)){
                try {
                    URL url = new URL(referer);
                    return url.getHost();
                }catch (MalformedURLException e){
                    log.error("提取referer失败:{}",e);
                }
            }
        }
        return "";
    }

    /**
     * 生成设备唯一ID
     * @param jsonObject
     * @return
     */
    private static String getDeviceId(JSONObject jsonObject) {
        Map<String,String> map = new TreeMap<>();
        try {
            map.put("ip", jsonObject.getString("ip"));
            map.put("event",jsonObject.getString("event"));
            map.put("bizId", jsonObject.getString("bizId"));
            String userAgent = jsonObject.getJSONObject("data").getString("user-agent");
            map.put("userAgent",userAgent);
            String deviceId = DeviceUtil.geneWebUniqueDeviceId(map);
            return deviceId;
        }catch (Exception e){
            log.error("生成唯一DeviceId 异常");
            return null;
        }
    }

}
