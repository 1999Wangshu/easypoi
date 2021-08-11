package com.example.easypoi.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.easypoi.pojo.Customer;
import com.example.easypoi.service.CustomerService;
import com.example.easypoi.utils.RedisService;
import com.example.easypoi.vo.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "Redis缓存数据")
@RequestMapping("redis")
public class RedisController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RedisService redisService;

    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping("/setString")
    @ApiOperation("存入String")
    public ResultBody saveString (){

        IPage<Customer> page = customerService.selectByPage("小", 1, 2);
        List<Customer> list = page.getRecords();

//        boolean b = redisService.set("customerstring", JSON.toJSONString(list));
        boolean b = redisService.set("customerstring", list);
        return ResultBody.ok().message("存入成功");
    }
    @GetMapping("/getString")
    @ApiOperation("获取String")
    public Object selString(){
        //获取数据并转成用户实体
        return redisService.get("customerstring");
    }


    @PostMapping("/setList")
    @ApiOperation("存入List")
    public ResultBody saveList (){

        IPage<Customer> page = customerService.selectByPage("小", 1, 2);
        List<Customer> list = page.getRecords();

        boolean b = redisService.lSet("customer", list);
        if (!b){
            throw new RuntimeException("存入失败");
        }
        return ResultBody.ok().message("存入成功");
    }

    @GetMapping(value = "/getList",produces = "application/json; charset=utf-8")
    @ApiOperation("获取List")
    public List<Object> selList(){

        List<Object> list = redisService.lGet("customer", 0, -1);

        String json = StringUtils.join(",",list);
        String s = StringEscapeUtils.unescapeJava(json);//去除转义字符
        //List<Customer> customers = JSON.parseObject(json, new TypeReference<ArrayList<Customer>>() {});


//        List<Customer> customers = JSONArray.parseArray(json, Customer.class);
//        Map<String,Customer> maps = customers.stream().collect(Collectors.toMap(Customer::getCustName, Function.identity()));

//        for (Map.Entry<String, Customer> entry : maps.entrySet()) {
//            System.out.println(entry.getKey()+">>>"+entry.getValue());
//        }

        return list;
    }

    @GetMapping("/getList2")
    @ApiOperation("获取List2")
    public List<Object> selList2(){

        long customer = redisService.lGetListSize("customer");
        List<Object> list = redisService.lGet("customer", 0, -1);
        return list;
    }

    @PostMapping("/delData")
    @ApiOperation("删除")
    @ApiImplicitParam(
            paramType = "query", name = "key", value = "数据的key", required = true, dataType = "String"
    )
    public ResultBody delList (@RequestParam("key") String key){

        redisService.del(key);
        return ResultBody.ok().message("删除成功");
    }

    @PostMapping("/setSet")
    @ApiOperation("存入Set")
    public ResultBody saveSet (){

        IPage<Customer> page = customerService.selectByPage("小", 3, 2);
        List<Customer> list = page.getRecords();

        long l = redisService.sSet("customerSet", list);

        return ResultBody.ok().message("存入成功");
    }

    @GetMapping("/getSet")
    @ApiOperation("获取Set")
    public Set<Object> selSet(){
        Set<Object> set = redisService.sGet("customerSet");
        return set;
    }

    @PostMapping("/setHash")
    @ApiOperation("存入Hash")
    public ResultBody saveHash (){

        IPage<Customer> page = customerService.selectByPage("小", 2, 2);
        List<Customer> list = page.getRecords();

        boolean b = redisService.hset("customerHash", "customer", list);

        return ResultBody.ok().message("存入成功");
    }

    @GetMapping("/getHash")
    @ApiOperation("获取Hash")
    public Object selHash(){

        return redisService.hget("customerHash", "customer");
    }


    @PostMapping("/setZset")
    @ApiOperation("存入Zset")
    public ResultBody saveZset (){

        IPage<Customer> page = customerService.selectByPage("小", 2, 10);
        List<Customer> list = page.getRecords();

        boolean b = redisService.zadd("customerZset", list,60,-1);

        return ResultBody.ok().message("存入成功");
    }

    @GetMapping("/getZset")
    @ApiOperation("获取Zset")
    public Set<Object> selZset(){

        return redisService.zRangeByScore("customerZset", 60,80);
    }


}
