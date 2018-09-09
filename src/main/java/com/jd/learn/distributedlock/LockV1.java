package com.jd.learn.distributedlock;

import com.jd.learn.zookeeper.ConnectionWatcher;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by qiushengsen on 2017/10/30.
 * 分布式锁，使用观察父节点的方式，存在羊群效应
 */
public class LockV1 extends ConnectionWatcher implements Watcher{

    public static final Logger LOGGER = LoggerFactory.getLogger(LockV1.class);

    public static final String PARENT_NODE = "/DS_LOCK";

    public static final String CHILDRIN_NODE = "/child@";

    public static final String NODE_DELEMITER = "@";

    private Integer currentNodeIndex;

    private volatile boolean holdLock = false;

    private volatile boolean created = false;

    public void createParentNode() throws KeeperException, InterruptedException {
        if(zooKeeper.exists(PARENT_NODE, false) == null) {
            LOGGER.info("创建锁节点");
            zooKeeper.create(PARENT_NODE, PARENT_NODE.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public boolean tryGetLock() throws KeeperException, InterruptedException {
        if(holdLock) {
            return true;
        }
        if(created) {
            return false;
        }
        if(zooKeeper.exists(PARENT_NODE, false) != null) {
            String nodePath = zooKeeper.create(
                    PARENT_NODE + CHILDRIN_NODE, CHILDRIN_NODE.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.info("创建锁节点的等待节点：" + nodePath);
            created = true;
            currentNodeIndex = Integer.valueOf(nodePath.split(NODE_DELEMITER)[1]);
            if(isBiggest()) {
                dowork();
            }
            return holdLock;
        }
        throw new RuntimeException("不存在锁");
    }

    private boolean isBiggest() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren(PARENT_NODE, this);
        int maxIndex = children.stream().mapToInt((a) -> Integer.valueOf(a.split(NODE_DELEMITER)[1])).max().getAsInt();
        if(maxIndex == currentNodeIndex) {
            holdLock = true;
            LOGGER.info("节点：" + PARENT_NODE + CHILDRIN_NODE + currentNodeIndex + "获得锁");
        }
        return holdLock;
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getType() == Event.EventType.NodeChildrenChanged) {
            try {
                if(!holdLock && isBiggest()) {
                    holdLock = true;
                    dowork();
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void dowork() {
        LOGGER.info("oh ye, i got the distributed lock");
        try {
            TimeUnit.SECONDS.sleep(5);
            LOGGER.info("work done, release lock");
            releaseLock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void releaseLock() throws InterruptedException {
        if(holdLock) {
            LOGGER.info(PARENT_NODE + CHILDRIN_NODE + currentNodeIndex + "释放锁");
            zooKeeper.close();
        }
    }


}
