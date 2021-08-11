package com.example.easypoi.schedule;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

//@Configuration
//@EnableScheduling
@Slf4j
public class SpringStartSchedule {

    //实现定时任务三种方式：
    /*
        1.java的Timer类
        2.springbootTasks
        3.springboot+quartz
     */

    private static final Logger logger = LoggerFactory.getLogger(SpringStartSchedule.class);
    //基于注解(@Scheduled)方式,静态执行定时任务
//    @Scheduled(cron = "0/1 * * * * ?")
    private void staticTasks(){
        log.error("执行静态任务的时间："+ LocalDateTime.now());
    }

}
