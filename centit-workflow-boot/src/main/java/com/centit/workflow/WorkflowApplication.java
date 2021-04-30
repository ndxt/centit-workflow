package com.centit.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.centit")
public class WorkflowApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
        System.out.println("==============++++++++++++++++==============");
        System.out.println("==============++++++++++++++++==============");
        System.out.println("WorkflowApplication 启动成功！");
        System.out.println("==============++++++++++++++++==============");
        System.out.println("==============++++++++++++++++==============");
    }

}
