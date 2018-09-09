package com.jd.learn.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by qiushengsen on 2017/9/29.
 */
@Component
public class ConfigUpdater {

    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigUpdater.class);

    public static final String PATH = "/config";

    @Autowired
    private ConfigValueStore configValueStore;

    private Random random = new Random();

    public void update() throws KeeperException, InterruptedException {
        while(true) {
            Integer value = random.nextInt();
            configValueStore.write(PATH, value.toString());
            LOGGER.info("generate random config value {}", value);
            TimeUnit.SECONDS.sleep(5);
        }
    }

}
