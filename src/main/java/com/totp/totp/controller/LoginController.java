package com.totp.totp.controller;

import com.alibaba.fastjson.JSONObject;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author machao
 * @date 2019/7/9
 * @time 14:47
 * @description
 **/
@RestController
public class LoginController {
    @Autowired
    private RestTemplate restTemplate;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static  String pass = "FPK3NGDG55PM6SD5W4OJBTMVMUWSSGL62W2PLJH2PMEICBCCZNVQ" ;
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public Object testRequest(@RequestBody Map map) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mfb", "root", "root");
        PreparedStatement preparedStatement = connection.prepareStatement("select 1 from dual");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            System.out.println(resultSet.getInt("1"));
        }
        System.currentTimeMillis();
        return null;
    }



    @RequestMapping("/login")
    public Object login() throws InterruptedException {
        Clock clock  = new Clock(180);
        Totp totp = new Totp(pass,clock);
        String now = totp.now();


        String format = sdf.format(new Date());
        String now1 = totp.now();
        System.out.println("当前时间"+format+",当前值:"+now1+",旧值验证结果:"+totp.verify(now));



        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();

        map.add("authCode", now);
        map.add("authMode","3");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
     //JSONObject jsonObject =  restTemplate.postForObject("http://192.168.41.103:8002/v1/user/login", request, JSONObject.class);
        //JSONObject jsonObject =  restTemplate.postForObject("http://localhost:8002/v1/user/login", request, JSONObject.class);
        //JSONObject jsonObject =  restTemplate.postForObject("http://192.168.40.120:8080/v1/user/login", request, JSONObject.class);
        JSONObject jsonObject =  restTemplate.postForObject("http://192.168.40.4:8082/v1/user/login", request, JSONObject.class);

        return jsonObject;
    }
    @GetMapping("/getMap")
    public Object getMap(HttpServletRequest request, HttpServletResponse response){

        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return false;
        }

        Map<String,Object> returnData = new HashMap<>();
        returnData.put("status",200);
        Map<String,Object> datas1 = new HashMap<>();
        Map<String,Object> datas2 = new HashMap<>();
        BigInteger big = new BigInteger(Long.MAX_VALUE+1+"");
        datas1.put("BigInteger",big);
        datas1.put("BigDecimaInt", new BigDecimal(Integer.MAX_VALUE));
        datas1.put("BigDecimaLong", new BigDecimal(Long.MAX_VALUE));
        datas1.put("FLOAT",2.33333333333123123F);
        datas1.put("double",2.123123123123123123123123);
        /*datas1.put("long",9007199254740992300L);
        148004249825564012*/
        datas1.put("long",148004249825564012L);

        datas1.put("maxLong",Long.valueOf(Long.MAX_VALUE)+1L);
        datas2.put("integer",20660525);
        datas2.put("User",new User());
        List<Object> list = new ArrayList<>();
        list.add(datas1);
        list.add(datas2);
        returnData.put("data",list);
        Map<String,Object> datas3 = new HashMap<>();
        datas3.put("id",148004249825564012L);
        returnData.put("data3",datas3);
        return returnData;
    }

    public static void main(String[] args) throws InterruptedException {
        Clock clock  = new Clock(180);
        Totp totp = new Totp(pass,clock);
        String now = totp.now();
        System.out.println("生成旧值时间:"+sdf.format(new Date())+"旧值:"+now);
        while(true){
            String format = sdf.format(new Date());
            String now1 = totp.now();
            System.out.println("当前时间"+format+",当前值:"+now1+",旧值验证结果:"+totp.verify(now));
            Thread.sleep(10000);
        }
    }
    @RequestMapping("/get")
    public Object getObject() {
        List<String> data = Arrays.asList("tags/","tags/zs/","tags/beijing/","tags/guangzhou/","tags/beijing/xicheng/","tags/beijing/dongcheng/","tags/beijing/xicheng/yuanchenxin/","tags/guangzhou/yuexiu/");
//        Tag tags = getTagByTagName("tags", data, "");
//        System.out.println(tags);

        List<Tag> deviceTagList = new ArrayList<>();
        Set<String> set = new LinkedHashSet<>();
        List<String> list = new ArrayList<>();
        data.forEach(e -> {
            String substring = e.substring(e.indexOf("/") + 1, e.length());
            String substring1 = null;
            if (!"".equals(substring)) {
                substring1 = substring.substring(0, substring.indexOf("/"));
                set.add(substring1);
            }
            list.add(substring);
        });
        set.forEach(e -> {
            deviceTagList.add(getTagByTagName( e, list, "",e));
        });
        return deviceTagList;
    }

    /**
     *
     * @param prefix tags
     * @return
     */
    public  Tag getTagByTagName(String name,List<String> data,String parentName,String typeName){
        String prefix = parentName+name+"/";
        Tag tag = new Tag();
        Set<String> collect = data.stream().filter(s -> s.contains(prefix)&&!s.equals(prefix)).map(
                s->{
                    s = s.replaceFirst(prefix, "");
                    int index = s.indexOf("/");
                    s = s.substring(0, index);
                    return s;
                }
        ).collect(Collectors.toSet());
        tag.setName(typeName);
        if(!typeName.equals(name)){
            tag.setValue(name);
        }
        List<Tag> tags = new ArrayList<>();
        for(String str  : collect){
            Tag tag1 = getTagByTagName(str, data, prefix,typeName);
            tags.add(tag1);
        }
        tag.setTags(tags);
        System.out.println(collect);
        return tag;

    }


}
