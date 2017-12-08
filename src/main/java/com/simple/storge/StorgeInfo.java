package com.simple.storge;

import org.quartz.JobKey;
import org.quartz.Scheduler;

import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liws on 2017/11/28.
 */
public final class StorgeInfo {

    private String instanceValue = "";

    private final ConcurrentHashMap<String, JobKey> jobKeyMap = new ConcurrentHashMap();

    private final ConcurrentHashMap<String, Scheduler> schedulerMap = new ConcurrentHashMap();

    private StorgeInfo() {

    }

    public final static StorgeInfo instance() {
        return Singleton.info;
    }

    private final static class Singleton {
        public static final StorgeInfo info = new StorgeInfo();
    }

    public ConcurrentHashMap<String, JobKey> getJobKeyMap() {
        return jobKeyMap;
    }

    public final void registerJobkey(String jobName, JobKey key) {
        jobKeyMap.put(jobName, key);
    }

    public final void removeJobKey(String jobName) {
        jobKeyMap.remove(jobName);
    }

    public final JobKey getJobKey(String jobName) {
        return jobKeyMap.get(jobName);
    }

    public final Collection<JobKey> getAllJobKey() {
        return jobKeyMap.values();
    }

    public final void registerScheduler(String jobName, Scheduler scheduler) {
        schedulerMap.put(jobName, scheduler);
    }

    public final void removeScheduler(String jobName) {
        schedulerMap.remove(jobName);
    }

    public final Scheduler getScheduler(String jobName) {
        return schedulerMap.get(jobName);
    }

    public final Collection<Scheduler> getAllScheduler() {
        return schedulerMap.values();
    }

    public final Enumeration<String> getAllJobName() {
        return schedulerMap.keys();
    }

    public String getInstanceValue() {
        return instanceValue;
    }

    public void setInstanceValue(String instanceValue) {
        this.instanceValue = instanceValue;
    }
}
