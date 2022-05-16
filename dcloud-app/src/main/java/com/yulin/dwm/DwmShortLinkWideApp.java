package com.yulin.dwm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yulin.func.AsyncLocationRequestFunction;
import com.yulin.func.DeviceMapFunction;
import com.yulin.func.LocationMapFunction;
import com.yulin.model.DevicesInfoDO;
import com.yulin.model.ShortLinkWideAppDO;
import com.yulin.util.DeviceUtil;
import com.yulin.util.KafkaUtil;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/12
 * @Description:
 */
public class DwmShortLinkWideApp {

    /**
     * 定义SOURCE topic
     */
    public static final String SOURCE_TOPIC = "dwd_link_visit_topic";

    /**
     * 定义SINK topic
     */
    public static final String SINK_TOPIC = "dwm_link_visit_topic";

    /**
     * 定义消费者组
     */
    public static final String GROUP_ID = "dwm_short_link_group";

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //并行度1
        env.setParallelism(1);

        //1，获取流
        //DataStream<String> ds = env.socketTextStream("127.0.0.1", 8887); //此处为联通测试使用，需要使用时候放开
        FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);
        
        DataStreamSource<String> ds = env.addSource(kafkaConsumer);

        //2，格式转换，补齐设备信息 进来是字符串出的就是一个宽表
        SingleOutputStreamOperator<ShortLinkWideAppDO> devicesWideDS = ds.map(new DeviceMapFunction());
        devicesWideDS.print("设备信息宽表补齐");

        //3，补齐地理位置信息
        //SingleOutputStreamOperator<String> shortLinkWideDS = devicesWideDS.map(new LocationMapFunction());
        SingleOutputStreamOperator<String> shortLinkWideDS = AsyncDataStream.unorderedWait(devicesWideDS,new AsyncLocationRequestFunction(),1000, TimeUnit.SECONDS,100);
        shortLinkWideDS.print("地理位置信息补齐");
        //定义输出
        FlinkKafkaProducer<String> kafkaProducer = KafkaUtil.getKafkaProducer(SINK_TOPIC);
        //将sink写到写到dwm层，kafka存储
        shortLinkWideDS.addSink(kafkaProducer);
        ds.print();
        env.execute();
    }
}
