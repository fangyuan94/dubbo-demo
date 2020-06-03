package com.fc.dubbo.demo.provide.impl;

import com.fc.dubbo.demo.api.service.HelloService;
import org.apache.dubbo.config.annotation.Service;

@Service(group = "dogHelloServiceImpl")
public class DogHelloServiceImpl implements HelloService {

    public String sayHello() {
        return "汪汪";
    }
}
