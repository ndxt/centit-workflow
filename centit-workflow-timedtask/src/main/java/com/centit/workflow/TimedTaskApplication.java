package com.centit.workflow;

import com.centit.support.json.JSONOpt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhf
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"com.centit"},
    excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
public class TimedTaskApplication {
    /**
     * 运行一个后台进程，最好作为服务启动
     *
     * @param args 为 任务分组号
     */
    public static void main(String[] args) {
        JSONOpt.fastjsonGlobalConfig();
        SpringApplication.run(TimedTaskApplication.class, args);
    }

}
