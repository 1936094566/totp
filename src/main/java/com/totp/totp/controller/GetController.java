package com.totp.totp.controller;

import com.alibaba.fastjson.JSON;
import org.asynchttpclient.Dsl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author machao
 * @date 2019/8/5
 * @time 9:18
 * @description
 **/
@RestController
public class GetController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/data")
    public Object testReq(@RequestBody List<ChassisIp> chassisIps){

        Map<String,Object> result = new HashMap<>(3);
        result.put("data","request success");
        result.put("success",true);
        result.put("code",200);
        return result;
    }

    @GetMapping("/send")
    public Object testSend() throws ExecutionException, InterruptedException, IOException {
        ChassisIp  chassisIp = new ChassisIp();
        chassisIp.setChassis_ip("127.0.0.1");
        ChassisIp  chassisIp1 = new ChassisIp();
        chassisIp1.setChassis_ip("127.0.0.1");

        List<ChassisIp> list = new ArrayList<>();
        list.add(chassisIp);
        list.add(chassisIp1);
        String url = "http://localhost:8088/data";
        String get_body = Dsl.asyncHttpClient()
                .prepareGet("http://localhost:8088/data")
                .addHeader("Content-Type","application/json")
                .setBody(JSON.toJSONString(list))
                .execute()
                .toCompletableFuture().get().getResponseBody();

        System.out.println(get_body);
        return null;
    }
    @GetMapping("/get")
    public  Object testGet(@RequestBody String str){
        return str;
    }


}
