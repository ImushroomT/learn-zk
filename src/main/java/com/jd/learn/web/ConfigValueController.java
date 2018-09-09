package com.jd.learn.web;

import com.jd.learn.zookeeper.ConfigUpdater;
import com.jd.learn.zookeeper.ConfigValueStore;
import com.jd.learn.zookeeper.ConfigWatcher;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by qiushengsen on 2017/9/29.
 */
@RestController
@RequestMapping("/cv")
public class ConfigValueController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigValueController.class);

    @Autowired
    private ConfigValueStore configValueStore;

    @Autowired
    private ConfigUpdater configUpdater;

    @Autowired
    private ConfigWatcher configWatcher;

    @Value("${zookeeper.hosts}")
    private String hosts;

    @GetMapping("/initStore")
    public String initStore() throws IOException, InterruptedException {
        configValueStore.connect(hosts);
        return "init success";
    }

    @GetMapping("/generate")
    public String generate() throws KeeperException, InterruptedException {
        new Thread(() -> {
            try {
                configUpdater.update();
            } catch (KeeperException e) {
                LOGGER.error("keeper exception", e);
            } catch (InterruptedException e) {
                LOGGER.error("interrupted exception", e);
            }
        }).start();
        return "success trigger generate";
    }

    @GetMapping("/watch")
    public String watch() throws KeeperException, InterruptedException {
        configWatcher.display();
        return "success trigger watch";
    }
}
