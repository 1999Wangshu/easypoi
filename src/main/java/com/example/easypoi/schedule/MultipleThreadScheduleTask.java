package com.example.easypoi.schedule;



import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 基于注解设定多线程方式
 */
//@Component注解用于对那些比较中立的类进行注释；
//相对与在持久层、业务层和控制层分别采用 @Repository、@Service 和 @Controller 对分层中的类进行注释
@Component
//@EnableScheduling   // 1.开启定时任务
//@EnableAsync        // 2.开启多线程
@Slf4j
public class MultipleThreadScheduleTask {

    //由于开启了多线程，第一个任务的执行时间也不受其本身执行时间的限制(其睡眠时间影响,还是每秒执行一次fun)，
    //所以需要注意可能会出现重复操作导致数据异常


    @Async
    @Scheduled(fixedDelay = 1000)//间隔1s，fixedRate: 定时多久执行一次（上一次开始执行时间点后xx秒再次执行；）
                                        //fixedDelay: 上一次执行结束时间点后xx秒再次执行
    public void first() throws InterruptedException {

        System.out.println("第一个定时任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
        Thread.sleep(1000 * 10);
    }

    @Async
    @Scheduled(cron = "0/1 * * * * ?") //间隔1s
    public void second(){
        System.out.println("第二个定时任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
        System.out.println();
    }

    //定时任务常用表达式
    //0 0 10,14,16 * * ? 每天上午10点，下午2点，4点
    //0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时
    //0 0 12 ? * WED 表示每个星期三中午12点
    //0 0 12 * * ?" 每天中午12点触发
    //0 15 10 ? * *" 每天上午10:15触发
    //0 15 10 * * ?" 每天上午10:15触发
    //0 15 10 * * ? *" 每天上午10:15触发
    //0 15 10 * * ? 2005" 2005年的每天上午10:15触发
    //0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发
    //0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发
    //0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
    //0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
    //0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发
    //0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发
    //0 15 10 15 * ?" 每月15日上午10:15触发
    //0 15 10 L * ?" 每月最后一日的上午10:15触发
    //0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
    //0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发
    //0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发



}
