package com.fc.dubbo.demo.provide.impl;

import com.fc.dubbo.demo.api.service.MonitorTestService;
import org.apache.dubbo.config.annotation.Service;

import java.util.Random;

@Service(group = "monitorTestServiceImpl")
public class MonitorTestServiceImpl implements MonitorTestService {



    private Random random = new Random();
    @Override
    public void methodA() {

        try {
            int time = random.nextInt(100);
            Thread.sleep(time);
            System.out.println("A被服务:"+time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void methodB() {
        try {

            int time = random.nextInt(100);
            Thread.sleep(time);
            System.out.println("B被服务:"+time);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void methodC() {
        try {
            int time = random.nextInt(100);
            Thread.sleep(time);
            System.out.println("C被服务:"+time);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
