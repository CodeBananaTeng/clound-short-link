package com.yulin.func;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yulin.model.ShortLinkWideAppDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/12
 * @Description:
 */
@Slf4j
public class LocationMapFunction extends RichMapFunction<ShortLinkWideAppDO,String> {

    private static final String IP_PARSE_URL = "https://restapi.amap.com/v3/ip?ip=%s&output=json&key=7e1acfb62c0c9ef42ff5e5e641bf264b";

    private CloseableHttpClient httpClient;

    @Override
    public void open(Configuration parameters) throws Exception {

        this.httpClient = createHttpClient();

    }

    @Override
    public void close() throws Exception {
        if (httpClient != null){
            httpClient.close();
        }
    }

    @Override
    public String map(ShortLinkWideAppDO value) throws Exception {

        String ip = value.getIp();
        String url = String.format(IP_PARSE_URL,ip);
        HttpGet httpGet = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(httpGet);){

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                JSONObject locationObject = JSON.parseObject(result);
                String province = locationObject.getString("province");
                String city = locationObject.getString("city");
                value.setProvince(province);
                value.setCity(city);
            }

        }catch (Exception e){
            log.error("ip解析错误,value={},msg={}",value,e.getMessage());
        }
        return JSON.toJSONString(value);
    }


    public CloseableHttpClient createHttpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

        //maxTotal的戏份，每个主机的最大并发量是300，route是指域名
        connectionManager.setDefaultMaxPerRoute(300);
        //设置连接池最大是500个并发
        connectionManager.setMaxTotal(500);

        //MaxPerRoute是对maxTotal的戏份，每个主机的并发最大是300，route是指域名
        /**
         * 类比请求 只请求yulin.net，最大并发300
         *
         * 请求   yulin.net，最大并发300
         * 请求   yulin1.com，最大并发300 但是这个和上面的请求数不能超过500（上面设置的）所以这个只能是200
         *
         *
         *
         * //MaxtTotal=400 DefaultMaxPerRoute=200
         * 只连接到http://yulin.net时，到这个主机的并发最多只有200；而不是400；
         * 而连接到http://yulin.net 和 http://yulin123.com时，到每个主机的并发最多只有200；
         * 即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute。
         */
        connectionManager.setDefaultMaxPerRoute(300);

        RequestConfig requestConfig = RequestConfig.custom()
                //返回数据的超时时间
                .setSocketTimeout(20000)
                //连接超时时间
                .setConnectTimeout(10000)
                //从连接池中获取连接的超时时间
                .setConnectionRequestTimeout(1000)
                .build();

        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
        return closeableHttpClient;
    }

}
