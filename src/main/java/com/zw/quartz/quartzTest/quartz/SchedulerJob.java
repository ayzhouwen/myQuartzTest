package com.zw.quartz.quartzTest.quartz;


import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public class SchedulerJob implements Job {



    public SchedulerJob() {

    }



    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("SchedulerJob:"+System.currentTimeMillis());
    }
}
