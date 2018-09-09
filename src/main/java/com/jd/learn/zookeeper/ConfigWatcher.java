package com.jd.learn.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by qiushengsen on 2017/9/29.
 */
@Component
public class ConfigWatcher implements Watcher {

    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigWatcher.class);

    public static final String PATH = "/config";

    @Autowired
    private ConfigValueStore configValueStore;

    @Override
    public void process(WatchedEvent event) {
        if(event.getType().equals(Event.EventType.NodeDataChanged)) {
            try {
                display();
            } catch (KeeperException e) {
                LOGGER.error("keeper exception:", e);
            } catch (InterruptedException e) {
                LOGGER.error("interrupted exception", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public void display() throws KeeperException, InterruptedException {
        String value = configValueStore.read(PATH, this);
        LOGGER.info("watch config value {}", value);
    }
}
