package com.jd.learn.zookeeper;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author qiushengsen
 * @company 京东成都研究院-供应链
 * @dateTime 2018/4/17 下午5:25
 * @descripiton
 **/
public class TestCalend {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i ++) {
            Calendar calendar = Calendar.getInstance();
            System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
            System.out.println(calendar.get(Calendar.MINUTE));
            System.out.println(calendar.get(Calendar.SECOND));
            TimeUnit.SECONDS.sleep(10);
        }

    }
}
