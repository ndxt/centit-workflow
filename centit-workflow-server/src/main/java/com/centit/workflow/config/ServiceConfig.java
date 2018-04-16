package com.centit.workflow.config;

import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.core.config.DataSourceConfig;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.system.config.SystemBeanConfig;
import com.centit.workflow.listener.InstantiationServiceBeanPostProcessor;
import org.springframework.context.annotation.*;

/**
 * Created by codefan on 17-7-18.
 */
@Configuration
@Import({DataSourceConfig.class,
        JdbcConfig.class,
        SpringSecurityDaoConfig.class,
        SystemBeanConfig.class})
@ComponentScan(basePackages = {"com.centit.*"})
@PropertySource("classpath:/system.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceConfig {

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
        TextOperationLogWriterImpl  operationLog =  new TextOperationLogWriterImpl();
        operationLog.init();
        return operationLog;
    }

    @Bean

    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

}
