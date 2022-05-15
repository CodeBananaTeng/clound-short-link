package com.yulin.func;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yulin.model.ShortLinkWideAppDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/15
 * @Description:
 */
@Slf4j
public class AsyncLocationRequestFunction extends RichAsyncFunction<ShortLinkWideAppDO,String> {

    private static final String IP_PARSE_URL = "https://restapi.amap.com/v3/ip?ip=%s&output=json&key=7e1acfb62c0c9ef42ff5e5e641bf264b";

    private CloseableHttpAsyncClient httpAsyncClient;

    @Override
    public void open(Configuration parameters) throws Exception {
        this.httpAsyncClient = createAsyncHttpClient();
    }

    @Override
    public void close() throws Exception {
        if (httpAsyncClient != null){
            httpAsyncClient.close();
        }
    }

    @Override
    public void asyncInvoke(ShortLinkWideAppDO shortLinkWideAppDO, ResultFuture<String> resultFuture) throws Exception {
        String ip = shortLinkWideAppDO.getIp();
        String url = String.format(IP_PARSE_URL,ip);

        HttpGet httpGet = new HttpGet(url);
        Future<HttpResponse> future = httpAsyncClient.execute(httpGet, null);


        CompletableFuture<ShortLinkWideAppDO> completableFuture = CompletableFuture.supplyAsync(new Supplier<ShortLinkWideAppDO>() {
            @Override
            public ShortLinkWideAppDO get() {
                try {
                    HttpResponse response = future.get();
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        String result = EntityUtils.toString(entity, "UTF-8");
                        JSONObject locationObject = JSON.parseObject(result);
                        String province = locationObject.getString("province");
                        String city = locationObject.getString("city");
                        shortLinkWideAppDO.setProvince(province);
                        shortLinkWideAppDO.setCity(city);
                        return shortLinkWideAppDO;
                    }
                } catch (InterruptedException | ExecutionException | IOException e) {
                    log.error("ip解析错误,value={},msg={}", shortLinkWideAppDO, e.getMessage());
                }
                return null;
            }
        });

        completableFuture.thenAccept(new Consumer<ShortLinkWideAppDO>() {

            @Override
            public void accept(ShortLinkWideAppDO shortLinkWideAppDO) {
                resultFuture.complete(Collections.singleton(JSON.toJSONString(shortLinkWideAppDO)));
            }

        });
//        completableFuture.thenAccept(obj->{
//            resultFuture.complete(Collections.singleton(JSON.toJSONString(shortLinkWideAppDO)));
//        });

    }


    /**
     * 创建异步客户端
     * @return
     */
    private CloseableHttpAsyncClient createAsyncHttpClient() {
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                            //返回数据的超时时间
                            .setSocketTimeout(20000)
                            //连接上服务器的超时时间
                            .setConnectTimeout(10000)
                            //从连接池中获取连接的超时时间
                            .setConnectionRequestTimeout(1000)
                            .build();
            ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
            PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
            //设置连接池最⼤是500个连接
            connManager.setMaxTotal(500);
            //MaxPerRoute是对maxtotal的细分，每个主机的并发最⼤是300，route是指域名
            connManager.setDefaultMaxPerRoute(300);
            CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom().setConnectionManager(connManager)
                            .setDefaultRequestConfig(requestConfig)
                            .build();
            httpClient.start();
            return httpClient;
        } catch (IOReactorException e) {
            log.error("初始化CloseableHttpAsyncClient异常:{}",e.getMessage());
            return null;
        }
    }

}
