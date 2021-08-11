package com.example.easypoi.schedule;


import com.example.easypoi.pojo.User;
import com.example.easypoi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态执行任务
 */
@Slf4j
//@Configuration
//@EnableScheduling
public class DynamicScheduleTask implements SchedulingConfigurer {

    //查询表达式
    @Mapper
    public interface CronMapper {
        @Select("select cron from cron limit 1")
        String getCron();
    }

    @Autowired
    CronMapper cronMapper;

    @Autowired
    UserService userService;


    //基于接口方式，动态执行
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar){

        taskRegistrar.addTriggerTask(
                //1.添加任务内容(Runnable)
                ()-> log.error("动态定时任务开启" + LocalDateTime.now().toLocalTime()),
                //2.设置执行周期(Trigger)
                triggerContext -> {
                    //查询
                    List<User> users = userService.selectUser();
                    log.debug(users.toString());
                    String cron = cronMapper.getCron();
                    if (StringUtils.isEmpty(cron)){
                    }
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );


    }


}
