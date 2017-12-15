package com.simple.scheduler;

import com.simple.job.SimpleJob;
import com.simple.listener.ListenerManager;
import com.simple.storge.StorgeInfo;
import com.simple.zk.ZKOperator;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liws on 2017/11/24.
 */
public class JobScheduler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JobConfig config;

    private SimpleJob job;

    public final StorgeInfo storgeInfo = StorgeInfo.instance();

    public JobScheduler(SimpleJob job, JobConfig config) {
        this.job = job;
        this.config = config;
    }

    public void init() {

        if(logger.isInfoEnabled()) {
            logger.info("initialization JobScheduler， jobName is {}, cron is {}",config.getJobName(), config.getCron());
        }

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

        if(logger.isInfoEnabled()) {
            logger.info("zookeeper election complete");
        }

        //启动监听
        new ListenerManager(zkOpertaor, quartzOperator).start();

        if(logger.isInfoEnabled()) {
            logger.info("initialization JobScheduler complete");
        }
    }
}
