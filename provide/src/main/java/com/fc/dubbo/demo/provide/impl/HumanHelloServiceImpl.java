package com.fc.dubbo.demo.provide.impl;

import com.fc.dubbo.demo.api.service.HelloService;
import org.apache.dubbo.config.annotation.Service;

@Service(group = "humanHelloServiceImpl")
public class HumanHelloServiceImpl implements HelloService {
    public String sayHello() {
        return "你好";
    }
}
