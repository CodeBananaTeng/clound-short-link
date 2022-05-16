package com.yulin.dwm;

import com.alibaba.fastjson.JSONObject;
import com.yulin.func.UniqueVisitorFilterFunction;
import com.yulin.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/16
 * @Description:
 */
@Slf4j
public class DwmUniqueVisitorApp {

    /**
     * 定义source topic
     */
    public static final String SOURCE_TOPIC = "dwm_link_visit_topic";
    /**
     * 定义消费者组
     */
    public static final String GROUP_ID = "dwm_unique_visitor_group";
    /**
     * 定义输出
     */
    public static final String SINK_TOPIC = "dwm_unique_visitor_topic";

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //DataStream<String> ds =
        //env.socketTextStream("127.0.0.1",8888);
        // 1、获取流
        FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC,GROUP_ID);

        DataStreamSource<String> ds = env.addSource(kafkaConsumer);

        //2,数据转换
        SingleOutputStreamOperator<JSONObject> jsonDS = ds.map(jsonstr -> JSONObject.parseObject(jsonstr));


        //3,分组
        KeyedStream<JSONObject, String> keyedStream = jsonDS.keyBy(new KeySelector<JSONObject, String>() {

            @Override
            public String getKey(JSONObject value) throws Exception {
                return value.getString("udid");
            }

        });

        //4,排重过滤
        SingleOutputStreamOperator<JSONObject> filter = keyedStream.filter(new UniqueVisitorFilterFunction());
        filter.print("独立访客");
        //5,转成字符串写入kafka
        SingleOutputStreamOperator<String> uniqueVisitorDS = filter.map(obj -> obj.toJSONString());
        FlinkKafkaProducer<String> kafkaProducer = KafkaUtil.getKafkaProducer(SINK_TOPIC);

        uniqueVisitorDS.addSink(kafkaProducer);
        env.execute();

    }

}
