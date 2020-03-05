package com.simple.job;

import com.simple.storge.StorgeInfo;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务的基础类
 * Created by liws on 2017/11/28.
 */
public class ExecuteJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //instance有值，说明该节点服务器获得执行权限
        String instance = StorgeInfo.instance().getInstanceValue();
        if(StringUtils.isBlank(instance)) {
            return;
        }
        SimpleJob job = (SimpleJob)jobExecutionContext.getJobDetail().getJobDataMap().get("job");
        job.execute();
    }
}
