package com.zw.quartz.quartzTest.quartz;


import lombok.extern.log4j.Log4j2;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;

@Configuration
@Log4j2
public class ApplicationStartQuartzJobListener implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private Scheduler scheduler;




    @Resource(name = "CollectQuartzScheduler")
    private QuartzScheduler quartzScheduler;






    /**
     * 初始启动quartz，开始执行采集任务调度
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            quartzScheduler.startSystemJob("0/1 * * * * ? ", PingJob.class,"PING");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }









}

