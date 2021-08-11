package com.example.easypoi.schedule.quartz;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequestMapping("quartz")
@RestController
@Slf4j
@Api(tags = "quartz定时任务")
public class QuartzController {

    @Autowired
    private Scheduler scheduler;

    //如果是普通的类，则需通过工厂创建
    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    //Scheduler scheduler = schedulerFactory.getScheduler();

    @PostMapping("/add")
    @ApiOperation(value = "创建定时任务")
    public Object add(@RequestParam("orderNo") String orderNo) throws SchedulerException {

        //创建任务明细
        /**通过JobBuilder.newJob()方法获取到当前Job的具体实现(以下均为链式调用)
         * 这里是固定Job创建，所以代码写死XXX.class
         * 如果是动态的，根据不同的类来创建Job，则 ((Job)Class.forName("com.zy.job.TestJob").newInstance()).getClass()
         * */
        JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class)
                /* 给当前JobDetail添加参数，K V形式 */
                .usingJobData("name","zs")
                /* 给当前JobDetail添加参数 ,链式调用，可以传入多个参数,在Job实现类中，可以通过jobExecutionContext.getJobDetail().getJobDataMap().get("age")获取值*/
                .usingJobData("age",23)
                /* 添加认证信息，有3种重写的方法，我这里是其中一种，可以查看源码看其余2种*/
                .withIdentity("name","group")
                .build();//执行

        //创建触发器有SimpleTrigger、CronTrigger两种
//        CronTrigger trigger =
                TriggerBuilder.newTrigger()
                /* 给当前JobDetail添加参数，K V形式，链式调用，可以传入多个参数 */
                .usingJobData("orderNo","123456")
                /* 添加认证信息，有3种重写的方法 */
                .withIdentity("我是name","我是group")
                /* 立即生效 */
                .startNow()
                /* 开始执行时间 */
                .startAt(new Date(java.sql.Date.valueOf("2021-05-05").getTime()))
                /* 结束执行时间,不写永久执行 */
                .endAt(new Date(Timestamp.valueOf("2021-12-12 08:08:09").getTime()))
                /* 添加执行规则，SimpleTrigger、CronTrigger的区别主要就在这里*/
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                        /*每隔3s执行一次 */
                        .withIntervalInSeconds(3)
                        /* 一直执行,如果不写,定时任务就执行一次*/
                        .repeatForever()
                ).build();


        CronTrigger trigger = TriggerBuilder.newTrigger()
                /* 给当前JobDetail添加参数，K V形式，链式调用，可以传入多个参数，在Job实现类中，可以通过jobExecutionContext.getTrigger().getJobDataMap().get("orderNo")获取值*/
                .usingJobData("orderNo", "123456")
                /* 添加认证信息，有3种重写的方法，我这里是其中一种，可以查看源码看其余2种*/
                .withIdentity(orderNo)
                /* 立即生效*/
                .startNow()
                /* 开始执行时间*/
                .startAt(new Date(java.sql.Date.valueOf("2021-05-05").getTime()))
                /*结束执行时间,不写永久执行*/
                .endAt(new Date(Timestamp.valueOf("2021-12-12 08:08:09").getTime()))
                /* 添加规则 */
                .withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?"))
                .build();

                scheduler.scheduleJob(jobDetail,trigger);
        if (!scheduler.isShutdown()){
            scheduler.start();
        }
        System.err.println("--------定时任务启动成功 "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        return "OK";

    }


    @PostMapping("/shutdown")
    @ApiOperation(value = "停止定时任务",notes = "停止")
    public Object stop(@RequestParam("orderNo") String orderNo) throws SchedulerException {
        scheduler.pauseTrigger(TriggerKey.triggerKey(orderNo));
//        scheduler.pauseJob(JobKey.jobKey(orderNo,"我是group"));//停止触发器
        return "is_stop";
    }

    @PostMapping("/resume")
    @ApiOperation(value = "重启定时任务",notes = "启动")
    public Object resume(@RequestParam("orderNo") String orderNo) throws SchedulerException {

        scheduler.resumeTrigger(TriggerKey.triggerKey(orderNo));
        //scheduler.resumeJob(JobKey jobKey)则可恢复一个具体的job,
        return "is_reStart";
    }
    @PostMapping("/del")
    @ApiOperation(value = "删除定时任务",notes = "删除")
    public Object del(@RequestParam("orderNo") String orderNo) throws SchedulerException {

        scheduler.pauseTrigger(TriggerKey.triggerKey(orderNo));//暂停
        scheduler.unscheduleJob(TriggerKey.triggerKey(orderNo));//移除
        //没有deleteTrigger的方法
        scheduler.deleteJob(JobKey.jobKey(orderNo));//删除job

        return "is_delete";
    }




}
