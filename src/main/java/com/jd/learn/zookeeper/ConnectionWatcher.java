package com.jd.learn.zookeeper;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by qiushengsen on 2017-09-17.
 */
@Component
public class ConnectionWatcher implements Watcher {

    public static final Logger LOGGER = LoggerFactory.getLogger(ConnectionWatcher.class);

    @Value("${zookeeper.session.timeout}")
    private int sessionTimeout;

    protected ZooKeeper zooKeeper;

    private CountDownLatch latch = new CountDownLatch(1);

    public void initialConn(int sessionTimeout, String hosts) throws IOException, InterruptedException {
        this.sessionTimeout = sessionTimeout;
        connect(hosts);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            latch.countDown();
        }
    }

    public void connect(String hosts) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(hosts, sessionTimeout, this);
        //latch.await();
    }

    public void create(String groupName, byte[] data) throws KeeperException, InterruptedException {
        String path = "/" + groupName;
        String createdPath = zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.PERSISTENT);
        LOGGER.info("created" + createdPath);
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    public void addNode(String groupName, String nodeName, byte[] data) throws KeeperException, InterruptedException {
        String path = "/" + groupName + "/" + nodeName;
        String createPath = zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL);
        LOGGER.info("add node " + createPath);
    }

    public void listNodes(String groupName) throws KeeperException, InterruptedException {
        String path = "/" + groupName;
        List<String> children = zooKeeper.getChildren(path, false);
        if(children.size() == 0) {
            LOGGER.info("group {} has no children", groupName);
            return;
        }
        for(String child : children) {
            LOGGER.info("child {}", child);
        }

    }

    public void deleteNode(String pathName) throws KeeperException, InterruptedException {
        String path = "/" + pathName;
        List<String> children = zooKeeper.getChildren(path, false);
        if(children.size() > 0) {
            for(String child : children) {
                deleteNode(pathName + "/" + child);
            }
        }
        zooKeeper.delete(path, -1);
    }

    public String createSequentialNode(String groupName, String nodeName) throws KeeperException, InterruptedException {
        String path = "/" + groupName + "/" + nodeName;
        return zooKeeper.create(path, nodeName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }




}
