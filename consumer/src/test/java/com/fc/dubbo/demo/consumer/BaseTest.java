package com.fc.dubbo.demo.consumer;

import com.fc.dubbo.demo.api.service.MonitorTestService;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsumerApplication.class)
public class BaseTest {

    @Reference(group = "monitorTestServiceImpl")
    private MonitorTestService monitorTestService;


    @Test
    public void test(){

        //构建线程池调用
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        for(int i=3;i<3000;i++){

            if(i/3==0){
                executorService.submit(()->{
                    monitorTestService.methodA();
                });
            }else if(i/3==1){
                executorService.submit(()->{
                    monitorTestService.methodB();
                });
            }else {
                executorService.submit(()->{
                    monitorTestService.methodC();
                });
            }

        }

    }
}
