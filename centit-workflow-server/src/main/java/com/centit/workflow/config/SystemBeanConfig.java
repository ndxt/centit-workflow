package com.centit.workflow.config;

import com.centit.framework.hibernate.config.DataSourceConfig;
import com.centit.framework.ip.app.config.IPAppSystemBeanConfig;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)//启用注解事物管理
@ComponentScan(basePackages = {"com.centit.*"})
@PropertySource("classpath:/system.properties")
@Import({IPAppSystemBeanConfig.class, DataSourceConfig.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SystemBeanConfig {
/*
    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        notificationCenter.initMsgSenders();
        //notificationCenter.registerMessageSender("innerMsg",innerMessageManager);
        return notificationCenter;
    }

    @Bean
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl operationLog =  new TextOperationLogWriterImpl();
        operationLog.init();
        return operationLog;
    }*/

    /*@Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }*/

}
