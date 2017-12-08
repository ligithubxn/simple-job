package com.simple.zk;

/**
 * Created by liws on 2017/11/23.
 */
public class ZKConfig {
    private String serverList;

    private String namespace;

    public String getServerList() {
        return serverList;
    }

    public void setServerList(String serverList) {
        this.serverList = serverList;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
