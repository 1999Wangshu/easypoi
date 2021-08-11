package com.example.easypoi.schedule;


import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestTasks {

    public static void main(String[] args) {

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.err.println("task " +String.format("%tF %<tT", new Date()) + Thread.currentThread().getName());
            }

        };

        Timer timer = new Timer();
        timer.schedule(timerTask,10,3000);


        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 参数：1、任务体 2、首次执行的延时时间
        // 3、任务执行间隔 4、间隔时间单位
        service.scheduleAtFixedRate(()->System.out.println("task ScheduledExecutorService "+new Date()), 0, 3, TimeUnit.SECONDS);
    }


}
