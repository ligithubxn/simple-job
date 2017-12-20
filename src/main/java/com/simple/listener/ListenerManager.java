package com.simple.listener;

import com.simple.scheduler.SchedulerOperator;
import com.simple.storge.StorgeInfo;
import com.simple.utils.IPUtils;
import com.simple.zk.ZKOperator;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监听zk节点和连接状态的类
 * Created by liws on 2017/12/7.
 */
public class ListenerManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ZKOperator zkOpertaor;

    private SchedulerOperator quartzOperator;

    private StorgeInfo info = StorgeInfo.instance();

    public ListenerManager(ZKOperator zkOpertaor, SchedulerOperator quartzOperator) {
        this.zkOpertaor = zkOpertaor;
        this.quartzOperator = quartzOperator;
    }

    public void start() {
        zkOpertaor.addConnectionListener(new ConnectionListener());
        zkOpertaor.addInstanceNodeListener(new InstanceNodeListener());
        zkOpertaor.addTriggerNodeListener(new TriggerNodeListener());
    }

    private class ConnectionListener implements ConnectionStateListener {

        public void stateChanged(CuratorFramework curatorFramework, ConnectionState state) {

            /**
             * 如果zk连接中断或丢失，就暂停本机的定时任务
             * 并将本机缓存的instance的值，置为空
             */
            if(ConnectionState.SUSPENDED == state || ConnectionState.LOST == state) {
                if(logger.isDebugEnabled()) {
                    logger.debug("connection state listener trigger, current state = {}", state);
                }
                info.setInstanceValue(null);
                quartzOperator.pauseAllJob();

                if(logger.isDebugEnabled()) {
                    logger.debug("All job pause");
                }
            } else if(ConnectionState.RECONNECTED == state) {
                if(logger.isDebugEnabled()) {
                    logger.debug("connection state listener trigger, current state = {}", state);
                }

                /**
                 * 如果重连成功，就恢复定时任务
                 */
                String value = zkOpertaor.getInstanceDate();
                String currentIP = IPUtils.getIp();

                if(StringUtils.isNoneBlank(value) && value.equals(currentIP)) {
                    info.setInstanceValue(value);
                }
                quartzOperator.resumeAllJob();
                if(logger.isDebugEnabled()) {
                    logger.debug("All job resume");
                }
            }
        }
    }

    private class InstanceNodeListener implements TreeCacheListener {

        public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
            logger.info("out instance state = {}", event.getType());
            /**
             * 如果instance节点被删除，就重新进行选举
             */
            if(TreeCacheEvent.Type.NODE_REMOVED == event.getType()) {
                if(logger.isDebugEnabled()) {
                    String path = event.getData().getPath();
                    logger.debug("instance node listener trigger, node current state = {}, node path = {}", event.getType(), path);
                }
                zkOpertaor.election();
                if(logger.isDebugEnabled()) {
                    logger.debug("instance node listener trigger, zk election complete");
                }
            }
        }
    }

    private class TriggerNodeListener implements TreeCacheListener {

        public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
            if(TreeCacheEvent.Type.NODE_UPDATED == event.getType()) {
                String path = event.getData().getPath();
                if(logger.isDebugEnabled()) {
                    logger.debug("trigger node listener trigger, node current state = {}， node path = {}", event.getType(), path);
                }
                byte[] valArray = event.getData().getData();
                if(valArray != null || valArray.length != 0) {
                    String value = new String(event.getData().getData(), "UTF-8");
                    SchedulerOperator p = new SchedulerOperator();
                    p.triggerJob(value);
                }
            }
        }
    }
}
