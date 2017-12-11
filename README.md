# simple-job
基于spring标签的简单定时任务，采用zookeeper做调度中心，quartz做定时任务，curator连接zk。

使用方式：
1、将编译过后的jar包引入项目

2、在spring配置文件中，增加对标签的依赖
```
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:simpleReg="http://www.simplejob.com/schema/simple/reg"
	   xmlns:simpleJob="http://www.simplejob.com/schema/simple/job"
	   xsi:schemaLocation="
            http://www.springframework.org/schema/beans  
            http://www.springframework.org/schema/beans/spring-beans-3.0.xsd  
            http://www.simplejob.com/schema/simple/reg
	    http://www.simplejob.com/schema/simple/reg/reg.xsd
	    http://www.simplejob.com/schema/simple/job
	    http://www.simplejob.com/schema/simple/job/job.xsd
       ">
</beans>
```

3、配置zk注册标签
```
<!-- server-lists为zk地址，namespace为zk上的节点根目录 -->
<simpleReg:zk id="zkCenter" server-lists="1.1.1.1:2181,2.2.2.2:2181" namespace="job-test" />
```

4、创建作业类，需要继承SimpleJob
```
public class TestJob implements SimpleJob {
	//业务service
	@Resource
	private XXXService xxxService;

	@Override
	public void execute() {
	    //business code
	}
}
```

5、配置作业类
```
<!-- id用作job的名称，class为作业的包名+类名，registry-center为zk注册标签中的id， cron为时间表达式 -->
<simpleJob:job id="testJob" class="xx.xx.xx.TestJob" registry-center="zkCenter" cron="0 0/2 * * * ?" />
```

6、创建监听，解决tomcat进程因quartz执行任务不能关闭的问题。别忘记在web.xml中配置该监听
```
public class ShudownListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        SchedulerOperator operator = new SchedulerOperator();
        operator.shutdownAll();
    }
}
```