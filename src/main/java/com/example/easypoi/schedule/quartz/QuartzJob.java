package com.example.easypoi.schedule.quartz;

import cn.hutool.core.date.DateUtil;
import com.example.easypoi.pojo.User;
import com.example.easypoi.service.UserService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Quartz 的流程
 * 1.创建任务(Job)，比如取消订单、
 * 2.创建任务调度器(Scheduler)，这是用来调度任务的,主要用于启动、停止、暂停、恢复等操作
 * 3.创建任务明细(JobDetail)，最开始我们编写好任务(Job)后，只是写好业务代码，并没有触发，这里需要用JobDetail来和之前创建的任务(Job)关联起来，便于执行
 * 4.创建触发器(Trigger)，触发器是来定义任务的规则的，比如几点执行，几点结束
 * 5.根据Scheduler来启动JobDetail与Trigger
 */

@Component
@DisallowConcurrentExecution //job的任务可能并发执行，如任务的执行时间过长，而每次触发的时间间隔太短，则会导致任务会被并发执行。如果是并发执行，就需要一个数据库锁去避免一个数据被多次处理。
public class QuartzJob implements Job {

    @Autowired
    private UserService userService;

    //创建任务
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.err.println(jobExecutionContext.getJobDetail().getJobDataMap().get("name"));
        System.err.println(jobExecutionContext.getJobDetail().getJobDataMap().get("age"));
        System.err.println(jobExecutionContext.getTrigger().getJobDataMap().get("orderNo"));
        System.err.println(jobExecutionContext.getJobDetail().getJobDataMap().get("name"));
        System.err.println("定时任务执行，当前时间："+ DateUtil.formatDateTime(new Date()));

//        List<User> users = userService.selectUser();
//        System.out.println(users);

    }

}
