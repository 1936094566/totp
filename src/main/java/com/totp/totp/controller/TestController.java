package com.totp.totp.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author machao
 * @date 2019/8/2
 * @time 15:07
 * @description
 **/
@RestController
@RequestMapping("/fm")
public class TestController {
    @PostMapping("/alarms/")
    public Object getAlarms(@RequestBody List<ChassisIp> chassis_ip,Integer pageSize,Integer pageNo){
        System.out.println(chassis_ip);
        Map<String,Object>  result = new HashMap<>();
        result.put("data",null);
        result.put("success",true);
        result.put("msg","fail");
        result.put("code",200);
        return result;
    }

    @DeleteMapping("/alarms/")
    public Object getAlarms1(@RequestBody List<Map<String,String>> params){
        System.out.println(params);
        Map<String,Object>  result = new HashMap<>();
        result.put("data",null);
        result.put("success",true);
        result.put("msg","fail");
        result.put("code",200);
        return result;
    }
}
