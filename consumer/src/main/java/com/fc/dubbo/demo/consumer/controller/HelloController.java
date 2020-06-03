package com.fc.dubbo.demo.consumer.controller;

import com.fc.dubbo.demo.api.service.HelloService;
import com.fc.dubbo.demo.api.service.MonitorTestService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("test")
public class HelloController {

    @Reference(group = "humanHelloServiceImpl")
    private HelloService humanHelloServiceImpl;

    @Reference(group = "dogHelloServiceImpl")
    private HelloService dogHelloServiceImpl;

    @Reference(group = "monitorTestServiceImpl",interfaceClass = MonitorTestService.class )
    private MonitorTestService monitorTestService;

    //构建线程池调用
    private ExecutorService executorService = Executors.newFixedThreadPool(100);

    @GetMapping("/test")
    public Map<String,Object> test(){

        Map<String,Object> datas = new HashMap<>();

        datas.put("success",true);
        datas.put("human",humanHelloServiceImpl.sayHello());
        datas.put("dog",dogHelloServiceImpl.sayHello());

        return datas;
    }


    @GetMapping("/test1")
    public Map<String,Object> test1(){

        Map<String,Object> datas = new HashMap<>();
        datas.put("success",true);

        for(int i=3;i<3000;i++){

            if(i%3==0){
                executorService.submit(()->{
                    monitorTestService.methodA();
                });
            }else if(i%3==1){
                executorService.submit(()->{
                    monitorTestService.methodB();
                });
            }else {
                executorService.submit(()->{
                    monitorTestService.methodC();
                });
            }
        }
        return datas;
    }



    @GetMapping("/test2")
    public Map<String,Object> test2(){

        Map<String,Object> datas = new HashMap<>();
        datas.put("success",true);
        monitorTestService.methodC();

        return datas;
    }

}
