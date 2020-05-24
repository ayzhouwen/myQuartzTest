package com.zw.quartz.quartzTest.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 任务调度处理
 *
 * @author yvan
 */
@Configuration("CollectQuartzScheduler")
@PropertySource("classpath:quartz.properties")
public class QuartzScheduler {
    private final String group = "collector-group";

    public QuartzScheduler() {
    }

    /**
     * 初始注入scheduler
     *
     * @return
     * @throws SchedulerException
     */
    @Bean
    public Scheduler scheduler() throws SchedulerException {
        SchedulerFactory schedulerFactoryBean = new StdSchedulerFactory();
        return schedulerFactoryBean.getScheduler();
    }

    /**
     * 获取Job信息
     *
     * @param name
     * @return
     * @throws SchedulerException
     */
    public String getJobInfo(String name) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler().getTrigger(triggerKey);
        return String.format("time:%s,state:%s", cronTrigger.getCronExpression(),
                scheduler().getTriggerState(triggerKey).name());
    }

    /**
     * 指定组获取Job信息
     *
     * @param name
     * @return
     * @throws SchedulerException
     */
    public String getJobInfo(String name, String mgroup) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(name, mgroup);
        CronTrigger cronTrigger = (CronTrigger) scheduler().getTrigger(triggerKey);
        return String.format("time:%s,state:%s", cronTrigger.getCronExpression(),
                scheduler().getTriggerState(triggerKey).name());
    }


    /**
     * 修改某个任务的执行时间
     *
     * @param name
     * @param time
     * @return
     * @throws SchedulerException
     */
    public boolean modifyJob(String name, String time) throws SchedulerException {
        Date date = null;
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler().getTrigger(triggerKey);
        String oldTime = cronTrigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(time)) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(time);
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                    .withSchedule(cronScheduleBuilder).build();
            date = scheduler().rescheduleJob(triggerKey, trigger);
        }
        return date != null;
    }

    /**
     * 暂停所有任务
     *
     * @throws SchedulerException
     */
    public void pauseAllJob() throws SchedulerException {
        scheduler().pauseAll();
    }

    /**
     * 暂停某个任务
     *
     * @param name
     * @throws SchedulerException
     */
    public void pauseJob(String name) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler().getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler().pauseJob(jobKey);
    }

    public void pauseJob(String name, String mgroup) throws SchedulerException {
        JobKey jobKey = new JobKey(name, mgroup);
        JobDetail jobDetail = scheduler().getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler().pauseJob(jobKey);
    }

    /**
     * 恢复所有任务
     *
     * @throws SchedulerException
     */
    public void resumeAllJob() throws SchedulerException {
        scheduler().resumeAll();
    }

    /**
     * 恢复某个任务
     *
     * @param name
     * @throws SchedulerException
     */
    public void resumeJob(String name) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler().getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler().resumeJob(jobKey);
    }

    /**
     * 指定组恢复某个任务
     *
     * @param name
     * @throws SchedulerException
     */
    public void resumeJob(String name, String mgroup) throws SchedulerException {
        JobKey jobKey = new JobKey(name, mgroup);
        JobDetail jobDetail = scheduler().getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler().resumeJob(jobKey);
    }


    /**
     * 删除某个任务
     *
     * @param name
     * @throws SchedulerException
     */
    public void deleteJob(String name) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler().getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler().deleteJob(jobKey);

    }

    /**
     * 删除所有任务
     *
     * @throws SchedulerException
     */
    public void deleteAllJobs() throws SchedulerException {
        List<JobKey> jobKeys = new ArrayList<>(scheduler().getJobKeys(GroupMatcher.groupContains(group)));
        scheduler().deleteJobs(jobKeys);
    }

    public void startJob(String taskName, String cron) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(SchedulerJob.class)
                .withIdentity(taskName, group)
                .usingJobData("taskName", taskName)
                .build();
        // 基于表达式构建触发器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        // CronTrigger表达式触发器 继承于Trigger
        // TriggerBuilder 用于构建触发器实例
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(taskName, group)
                .withSchedule(cronScheduleBuilder).build();
        scheduler().scheduleJob(jobDetail, cronTrigger);

        //定时器运行中添加新的任务让其执行
        if (scheduler().isStarted()) {
            this.resumeJob(taskName);
        }

    }

    @SuppressWarnings("unchecked")
    public void startSystemJob(String cron, Class cls, String jobname) throws SchedulerException {
        String taskName = jobname;
        JobDetail jobDetail = JobBuilder.newJob(cls)
                .withIdentity(taskName, "system-group")
                .usingJobData("taskName", taskName)
                .build();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(taskName, "system-group")
                .withSchedule(cronScheduleBuilder).build();
        scheduler().scheduleJob(jobDetail, cronTrigger);
    }

}

