package com.simple.scheduler;

import com.simple.job.SimpleJob;
import com.simple.listener.ListenerManager;
import com.simple.storge.StorgeInfo;
import com.simple.zk.ZKOperator;
import org.quartz.Scheduler;

/**
 * Created by liws on 2017/11/24.
 */
public class JobScheduler {

    private JobConfig config;

    private SimpleJob job;

    public final StorgeInfo storgeInfo = StorgeInfo.instance();

    public JobScheduler(SimpleJob job) {
        this.job = job;
    }

    public void init() {
        //创建zk操作类
        ZKOperator zkOpertaor = new ZKOperator(config.getCenter().getClient(), config.getJobName());

        //创建quartz操作类
        SchedulerOperator quartzOperator = new SchedulerOperator();

        //创建scheduler
        Scheduler scheduler = quartzOperator.createScheduler(config.getJobName());

        //zk选举
        zkOpertaor.election();

        //启动任务
        quartzOperator.startJob(scheduler, job, config);

        //启动监听
        new ListenerManager(zkOpertaor, quartzOperator).start();
    }

    public void setConfig(JobConfig config) {
        this.config = config;
    }
}
