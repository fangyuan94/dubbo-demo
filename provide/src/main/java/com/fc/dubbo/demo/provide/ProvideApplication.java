package com.fc.dubbo.demo.provide;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ProvideApplication {

    public static void main(String[] args) {

        SpringApplication.run(ProvideApplication.class,args);

    }
}
