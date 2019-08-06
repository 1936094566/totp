package com.totp.totp.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author machao
 * @date 2019/5/20
 * @time 19:10
 * @description
 * HttpClient配置
 **/
@Configuration
public class RestTemplateConfig {
    @Value("${http.maxTotal}")
    private String maxTotal;
    @Value("${http.defaultMaxPerRoute}")
    private String defaultMaxPerRoute;
    @Value("${http.connectTimeout}")
    private String connectTimeout;
    @Value("${http.connectionRequestTimeout}")
    private String connectionRequestTimeout;
    @Value("${http.socketTimeout}")
    private String socketTimeout;



    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();

        //重新设置StringHttpMessageConverter字符集为UTF-8，解决中文乱码问题
        HttpMessageConverter<?> converterTarget = null;
        for (HttpMessageConverter<?> item : converterList) {
            if (StringHttpMessageConverter.class == item.getClass()) {
                converterTarget = item;
                break;
            }
        }
        if (null != converterTarget) {
            converterList.remove(converterTarget);
        }


        converterList.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        /**
         * restTemplate 异常处理类 , 解决返回不了除了200之外的状态码
         */
        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return true;
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {

            }
        };
        restTemplate.setErrorHandler(responseErrorHandler);

        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(Integer.valueOf(this.maxTotal));
        connectionManager.setDefaultMaxPerRoute(Integer.valueOf(this.defaultMaxPerRoute));
        RequestConfig requestConfig = RequestConfig.custom()
                //服务器返回数据(response)的时间，超过抛出read timeout
                .setSocketTimeout(Integer.valueOf(this.socketTimeout))
                //连接上服务器(握手成功)的时间，超出抛出connect timeout
                .setConnectTimeout(Integer.valueOf(this.connectTimeout))
                //从连接池中获取连接的超时时间，超时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .setConnectionRequestTimeout(Integer.valueOf(this.connectionRequestTimeout))
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                .build();
    }




}
