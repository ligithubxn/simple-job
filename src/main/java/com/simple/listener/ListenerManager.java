package com.simple.listener;

import com.simple.scheduler.SchedulerOperator;
import com.simple.storge.StorgeInfo;
import com.simple.zk.ZKOperator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * Created by liws on 2017/12/7.
 */
public class ListenerManager {

    private ZKOperator zkOpertaor;

    private SchedulerOperator quartzOperator;

    private StorgeInfo info = StorgeInfo.instance();

    public ListenerManager(ZKOperator zkOpertaor, SchedulerOperator quartzOperator) {
        this.zkOpertaor = zkOpertaor;
        this.quartzOperator = quartzOperator;
    }

    public void start() {
        zkOpertaor.addConnectionListener(new ConnectionListener());
        zkOpertaor.addInstanceNodeListener(new NodeListener());
    }

    private class ConnectionListener implements ConnectionStateListener {

        public void stateChanged(CuratorFramework curatorFramework, ConnectionState state) {
            if(ConnectionState.SUSPENDED == state || ConnectionState.LOST == state) {
                info.setInstanceValue(null);
                quartzOperator.pauseAllJob();
            } else if(ConnectionState.RECONNECTED == state) {
                quartzOperator.resumeAllJob();
            }
        }
    }

    private class NodeListener implements TreeCacheListener {

        public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
            if(TreeCacheEvent.Type.NODE_REMOVED == event.getType()) {
                zkOpertaor.election();
            }
        }
    }
}
