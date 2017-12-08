package com.simple.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

/**
 * Created by liws on 2017/11/23.
 */
public class ZKRegisterCenter {

    private CuratorFramework client = null;

    private ZKConfig config;

    public ZKRegisterCenter(ZKConfig config) {
        this.config = config;
    }

    public void init() {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(config.getServerList()).retryPolicy(new RetryOneTime(1000))
                .sessionTimeoutMs(3000).namespace(config.getNamespace());
        client = builder.build();
        client.start();

    }

    public CuratorFramework getClient() {
        return client;
    }

    public ZKConfig getZKConfig() {
        return config;
    }
}
