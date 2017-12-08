package com.simple.zk;

import com.simple.storge.StorgeInfo;
import com.simple.utils.IPUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * Created by liws on 2017/11/29.
 */
public class ZKOperator {

    private final static String LEADER = "leader";

    private final static String INSTANCE = "instance";

    private CuratorFramework client;

    private String jobName;

    private StorgeInfo storgeInfo = StorgeInfo.instance();

    public ZKOperator(CuratorFramework client, String jobName) {
        this.client = client;
        this.jobName = jobName;
    }

    //选举
    public void election() {
        LeaderLatch latch = new LeaderLatch(client, leadPath());

        try {
            latch.start();
            latch.await();
            //成为leader后判断是否有instance节点，如果没有就创建
            if(!hasInstance()) {
                createInstanceEphemeral();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                latch.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //添加zk连接监听事件
    public void addConnectionListener(ConnectionStateListener listener) {
        client.getConnectionStateListenable().addListener(listener);
    }

    //添加zk节点监听事件
    public void addInstanceNodeListener(TreeCacheListener listener) {
        TreeCache treeCache = new TreeCache(client, instancePath());
        treeCache.getListenable().addListener(listener);
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建instance
    private void createInstanceEphemeral() {
        String value = IPUtils.getIp();
        try {
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath(), value.getBytes("UTF-8"));
            storgeInfo.setInstanceValue(value);
        } catch (Exception e) {
            storgeInfo.setInstanceValue(null);
            e.printStackTrace();
        }
    }

    //判断是否有instance节点
    private boolean hasInstance() {
        Stat stat = null;
        try {
            stat = client.checkExists().forPath(instancePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat != null;
    }

    private String leadPath() {
        return String.format("/%s/%s", jobName, LEADER);
    }

    private String instancePath() {
        return String.format("/%s/%s", jobName, INSTANCE);
    }
}
