package com.simple.scheduler;

import com.simple.job.SimpleJob;
import com.simple.job.ExecuteJob;
import com.simple.storge.StorgeInfo;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by liws on 2017/11/28.
 */
public class SchedulerOperator {

    private StorgeInfo info = StorgeInfo.instance();

    //创建scheduler
    public Scheduler createScheduler(String jobName) {
        final StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            factory.initialize(getBaseQuartzProperties(jobName));
            scheduler = factory.getScheduler();
            if(scheduler != null) {
                info.registerScheduler(jobName, scheduler);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return scheduler;
    }

    //启动job
    public void startJob(Scheduler scheduler, SimpleJob job, JobConfig config) {
        String jobName = config.getJobName();
        String cron = config.getCron();
        JobDetail jobDetail= JobBuilder.newJob(ExecuteJob.class).withIdentity(jobName, "group1").build();
        jobDetail.getJobDataMap().put("job", job);
        info.registerJobkey(jobName, jobDetail.getKey());
        // 触发器
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        // 触发器名,触发器组
        triggerBuilder.withIdentity(jobName + "_cron", "cron_1");
        // 触发器时间设定
        triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
        // 创建Trigger对象
        CronTrigger trigger = (CronTrigger) triggerBuilder.build();
        try {
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    //关闭定时任务
    public void shutdown(String jobName) {
        Scheduler scheduler = info.getScheduler(jobName);
        try {
            info.removeScheduler(jobName);
            if(scheduler.isStarted()) {
                scheduler.shutdown();
                scheduler = null;
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    //关闭定时任务
    public void shutdownAll() {
        Enumeration<String> jobNames = info.getAllJobName();
        while(jobNames.hasMoreElements()) {
            String jobName = jobNames.nextElement();
            shutdown(jobName);
        }
    }

    //暂停定时任务
    public void pauseJob(String jobName) {
        Scheduler scheduler = info.getScheduler(jobName);
        JobKey key = info.getJobKey(jobName);
        try {
            scheduler.pauseJob(key);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    //暂停所有定时任务
    public void pauseAllJob() {
        Collection<Scheduler> schedulers = info.getAllScheduler();
        for(Scheduler scheduler : schedulers){
            try {
                scheduler.pauseAll();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    //恢复定时任务
    public void resumeJob(String jobName) {
        Scheduler scheduler = info.getScheduler(jobName);
        JobKey key = info.getJobKey(jobName);
        try {
            scheduler.resumeJob(key);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    //暂停所有定时任务
    public void resumeAllJob() {
        Collection<Scheduler> schedulers = info.getAllScheduler();
        for(Scheduler scheduler : schedulers){
            try {
                scheduler.resumeAll();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    //触发任务定时任务
    public void triggerJob(String jobName) {
        Scheduler scheduler = info.getScheduler(jobName);
        JobKey key = info.getJobKey(jobName);
        try {
            scheduler.triggerJob(key);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private Properties getBaseQuartzProperties(String JobName) {
        Properties result = new Properties();
        result.put("org.quartz.threadPool.class", org.quartz.simpl.SimpleThreadPool.class.getName());
        result.put("org.quartz.threadPool.threadCount", "1");
        result.put("org.quartz.scheduler.instanceName", JobName);
        result.put("org.quartz.jobStore.misfireThreshold", "1");
        return result;
    }
}
