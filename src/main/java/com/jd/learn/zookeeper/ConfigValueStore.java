package com.jd.learn.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * Created by qiushengsen on 2017/9/29.
 */
@Component
public class ConfigValueStore extends ConnectionWatcher {

    public static final Charset CHARSET = Charset.forName("utf-8");

    public void write(String path, String value) throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(path, false);
        if(stat == null) {
            zooKeeper.create(path, value.getBytes(CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            zooKeeper.setData(path, value.getBytes(CHARSET), -1);
        }
    }

    public String read(String path, Watcher watcher) throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData(path, watcher, null);
        return new String(data, CHARSET);
    }
}
