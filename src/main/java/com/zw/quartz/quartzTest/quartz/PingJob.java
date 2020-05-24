package com.zw.quartz.quartzTest.quartz;


import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
@Slf4j
public class PingJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        log.info("PingJob:"+System.currentTimeMillis());
    }
}
