package com.yulin.dws;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yulin.model.ShortLinkVisitStatsDO;
import com.yulin.util.KafkaUtil;
import com.yulin.util.TimeUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple9;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.time.Duration;


public class DwsShortLinkVisitStatsApp {

    /**
     * 宽表
     */
    public static final String SHORT_LINK_SOURCE_TOPIC = "dwm_link_visit_topic";

    public static final String SHORT_LINK_SOURCE_GROUP = "dws_link_visit_group";

    /**
     * 独立访客 UV的数据流
     */
    public static final String UNIQUE_VISITOR_SOURCE_TOPIC = "dwm_unique_visitor_topic";

    public static final String UNIQUE_VISITOR_SOURCE_GROUP = "dwm_unique_visitor_group";



    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        //1、获取多个数据
        FlinkKafkaConsumer<String> shortLinkSource = KafkaUtil.getKafkaConsumer(SHORT_LINK_SOURCE_TOPIC, SHORT_LINK_SOURCE_GROUP);
        DataStreamSource<String> shortLinkDS = env.addSource(shortLinkSource);

        FlinkKafkaConsumer<String> uniqueSource = KafkaUtil.getKafkaConsumer(UNIQUE_VISITOR_SOURCE_TOPIC, UNIQUE_VISITOR_SOURCE_GROUP);
        DataStreamSource<String> uniqueDS = env.addSource(uniqueSource);


        //2、结构转换 uniqueVisitorDS、shortLinkDS
        SingleOutputStreamOperator<ShortLinkVisitStatsDO> shortLinkMapDS = shortLinkDS.map(new MapFunction<String, ShortLinkVisitStatsDO>() {
            @Override
            public ShortLinkVisitStatsDO map(String s) throws Exception {
                ShortLinkVisitStatsDO shortLinkVisitStatsDO = parseVisitStats(s);
                shortLinkVisitStatsDO.setPv(1L);
                shortLinkVisitStatsDO.setUv(0L);
                return shortLinkVisitStatsDO;
            }
        });

        SingleOutputStreamOperator<ShortLinkVisitStatsDO> uniqueMapDS = uniqueDS.map(new MapFunction<String, ShortLinkVisitStatsDO>() {
            @Override
            public ShortLinkVisitStatsDO map(String s) throws Exception {
                ShortLinkVisitStatsDO shortLinkVisitStatsDO = parseVisitStats(s);
                shortLinkVisitStatsDO.setPv(0L);
                shortLinkVisitStatsDO.setUv(1L);
                return shortLinkVisitStatsDO;
            }
        });
        //3、多流合并（合并相同结构的流）
        DataStream<ShortLinkVisitStatsDO> unionDS = shortLinkMapDS.union(uniqueMapDS);

        //4、设置WaterMark
        SingleOutputStreamOperator<ShortLinkVisitStatsDO> watermarksDS = unionDS.assignTimestampsAndWatermarks(WatermarkStrategy.
                //制定允许乱序延迟最大三秒
                        <ShortLinkVisitStatsDO>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                //指定事件时间
                .withTimestampAssigner((event, timestamp) -> event.getVisitTime()));

        //5、多维度、多个字段分组
        // code、referer、isNew
        // province、city、ip
        // browserName、os、deviceType
        KeyedStream<ShortLinkVisitStatsDO, Tuple9<String, String, Integer, String, String, String, String, String, String>> keyedStream = watermarksDS.keyBy(new KeySelector<ShortLinkVisitStatsDO, Tuple9<String, String, Integer, String, String, String, String, String, String>>() {
            @Override
            public Tuple9<String, String, Integer, String, String, String, String, String, String> getKey(ShortLinkVisitStatsDO obj) throws Exception {
                return Tuple9.of(
                        obj.getCode(),
                        obj.getReferer(),
                        obj.getIsNew(),
                        obj.getProvince(),
                        obj.getCity(),
                        obj.getIp(),
                        obj.getBrowserName(),
                        obj.getOs(),
                        obj.getDeviceType()
                );
            }
        });

        //6、开窗 15秒⼀次数据插⼊到 ck
        WindowedStream<ShortLinkVisitStatsDO, Tuple9<String, String, Integer, String, String, String, String, String, String>, TimeWindow> windowStream =
                keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(10)));

        //7、聚合统计(补充统计起⽌时间)
        SingleOutputStreamOperator<Object> reduceDS = windowStream.reduce(new ReduceFunction<ShortLinkVisitStatsDO>() {
            @Override
            public ShortLinkVisitStatsDO reduce(ShortLinkVisitStatsDO value1, ShortLinkVisitStatsDO value2) throws Exception {
                value1.setPv(value1.getPv() + value2.getPv());

                value1.setUv(value1.getUv() + value2.getUv());
                return null;
            }
            //拿到窗口里面的数据进行处理
        }, new ProcessWindowFunction<ShortLinkVisitStatsDO, Object, Tuple9<String, String, Integer, String, String, String, String, String, String>, TimeWindow>() {

            @Override
            public void process(Tuple9<String, String, Integer, String, String, String, String, String, String> tuple,
                                Context context,
                                Iterable<ShortLinkVisitStatsDO> iterable,
                                Collector<Object> out) throws Exception {

                for (ShortLinkVisitStatsDO shortLinkVisitStatsDO : iterable) {

                    //窗口开始和结束时间
                    String startTime = TimeUtil.format(context.window().getStart(),"yyyy-MM-dd HH:mm:ss");
                    String endTime = TimeUtil.format(context.window().getEnd(),"yyyy-MM-dd HH:mm:ss");
                    shortLinkVisitStatsDO.setStartTime(startTime);
                    shortLinkVisitStatsDO.setEndTime(endTime);
                    out.collect(shortLinkVisitStatsDO);


                }

            }
        });
        reduceDS.print(">>>>>>>>>>");
        env.execute();
        //8、输出Clickhouse

    }

    private static ShortLinkVisitStatsDO parseVisitStats(String value) {
        JSONObject jsonObj = JSON.parseObject(value);
        ShortLinkVisitStatsDO visitStatsDO = ShortLinkVisitStatsDO.builder()
                .code(jsonObj.getString("code"))
                .accountNo(jsonObj.getLong("accountNo"))
                .visitTime(jsonObj.getLong("visitTime"))
                .referer(jsonObj.getString("referer"))
                .isNew(jsonObj.getInteger("isNew"))
                .udid(jsonObj.getString("udid"))
                //地理位置信息
                .province(jsonObj.getString("province"))
                .city(jsonObj.getString("city"))
                .isp(jsonObj.getString("isp"))
                .ip(jsonObj.getString("ip"))
                //设备信息
                .browserName(jsonObj.getString("browserName"))
                .os(jsonObj.getString("os"))
                .osVersion(jsonObj.getString("osVersion"))
                .deviceType(jsonObj.getString("deviceType"))
                .build();
        return visitStatsDO;
    }

}
