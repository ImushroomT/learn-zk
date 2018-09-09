package com.jd.learn.zookeeper;

import com.jd.learn.distributedlock.LockV1;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * Created by qiushengsen on 2017/10/30.
 */
@RestController
@RequestMapping("/lock")
public class LockController {
    @Value("${zookeeper.hosts}")
    private String hosts;

    @Value("${zookeeper.session.timeout}")
    private int sessionTimeout;

    @GetMapping("/testv1")
    public void testLockV1() throws InterruptedException {

        CyclicBarrier barrier = new CyclicBarrier(2);

        new Thread(() -> {
            LockV1 l1 = new LockV1();
            try {
                barrier.await();
                l1.initialConn(sessionTimeout, hosts);
                l1.createParentNode();
                l1.tryGetLock();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            LockV1 l2 = new LockV1();
            try {
                barrier.await();
                l2.initialConn(sessionTimeout, hosts);
                l2.createParentNode();
                l2.tryGetLock();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
