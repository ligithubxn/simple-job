package com.simple.scheduler;


import com.simple.zk.ZKRegisterCenter;

/**
 * Created by liws on 2017/11/24.
 */
public class JobConfig {

    private ZKRegisterCenter center;

    private String jobName;

    private String cron;

    public ZKRegisterCenter getCenter() {
        return center;
    }

    public void setCenter(ZKRegisterCenter center) {
        this.center = center;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}
