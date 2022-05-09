package com.yulin.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/9
 * @Description:
 */
@Slf4j
public class KafkaUtil {

    private static String KAFKA_SERVER = null;
    static {
        Properties properties = new Properties();
        InputStream in = KafkaUtil.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("加载kafka配置文件失败");
        }
        //获取key配置对应的value
        KAFKA_SERVER = properties.getProperty("kafka.servers");
    }

    /**
     * 获取flink kafka消费者
     * @param topic
     * @param groupId
     * @return
     */
    public static FlinkKafkaConsumer<String> getKafkaConsumer(String topic, String groupId){
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,KAFKA_SERVER);
        return new FlinkKafkaConsumer<String>(topic,new SimpleStringSchema(),properties);
    }

    /**
     * 获取flink kafka生产者
     * @param topic
     * @return
     */
    public static FlinkKafkaProducer<String> getKafkaProducer(String topic){
        return new FlinkKafkaProducer<String>(KAFKA_SERVER,topic,new SimpleStringSchema());
    }

}
