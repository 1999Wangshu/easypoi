package com.example.easypoi.utils;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.easypoi.pojo.Customer;
import com.example.easypoi.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
public class DataSourceUtils {


    @Autowired
    DataSource dataSource;

    @Autowired
    CustomerService service;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @Test
    void testCon() {

        Connection c = null;
        try {
            c = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(c);
    }

    @Test
    void testPage() {
        IPage<Customer> page = service.selectByPage("小", 2, 10);
//        List<Customer> records = page.getRecords();
//        System.out.println(list);
        System.out.println(page);

    }

    @Test
    void testA(){

        long l = System.currentTimeMillis();
        LocalDateTime dateTime = LocalDateTime.now();
        String h = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            id.append(random.nextInt(10));
        }
        System.out.println(id);
        System.out.println(l);
        System.out.println(h);

    }


    @Test
    public void set(){

        //配置序列化器
        redisTemplate.setKeySerializer(RedisSerializer.string());

        System.out.println(redisTemplate);
        redisTemplate.opsForValue().set("myKey","myValue");
        System.out.println(redisTemplate.opsForValue().get("myKey"));
    }


}
