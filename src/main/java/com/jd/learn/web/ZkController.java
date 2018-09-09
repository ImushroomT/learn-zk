package com.jd.learn.web;

import com.jd.learn.zookeeper.ConnectionWatcher;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by qiushengsen on 2017-09-17.
 */
@RestController
@RequestMapping("/zk")
public class ZkController {

    @Autowired
    private ConnectionWatcher connectionWatcher;

    @Value("${zookeeper.hosts}")
    private String hosts;

    @GetMapping("/conn")
    public String connect() throws IOException, InterruptedException {
        connectionWatcher.connect(hosts);
        return "connected";
    }

    @GetMapping("/createRootNode")
    public String createRoot(String rootName) throws KeeperException, InterruptedException {
        connectionWatcher.create(rootName, null);
        return "create root";
    }

    @GetMapping("/createNode")
    public String createNode(String groupName, String nodeName) throws KeeperException, InterruptedException {
        connectionWatcher.addNode(groupName, nodeName, null);
        return "";
    }

    @GetMapping("/listNodes")
    public void listNodes(String nodeName) throws KeeperException, InterruptedException {
        connectionWatcher.listNodes(nodeName);
    }

    @GetMapping("/deleteNode")
    public void deleteNode(String nodeName) throws KeeperException, InterruptedException {
        connectionWatcher.deleteNode(nodeName);
    }

    @GetMapping("/createSequentialNode")
    public void createSequentialNode(String groupName, String nodeName) throws KeeperException, InterruptedException {
        System.out.println(connectionWatcher.createSequentialNode(groupName, nodeName));
    }
}
