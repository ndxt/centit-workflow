package com.centit.workflow.config;

import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.UserUnitFilterCalcContext;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.core.config.DataSourceConfig;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.system.config.SystemBeanConfig;
import com.centit.workflow.external.JdbcUserUnitFilterCalcContext;
import org.springframework.context.annotation.*;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;


/**
 * Created by codefan on 17-7-18.
 */
@Configuration
@Import({DataSourceConfig.class,
        JdbcConfig.class,
        SpringSecurityDaoConfig.class,
        SystemBeanConfig.class})
@ComponentScan(basePackages = "com.centit",
        excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceConfig {

    @Bean("passwordEncoder")
    public CentitPasswordEncoder passwordEncoder(){
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        //notificationCenter.initMsgSenders();
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
    public UserUnitFilterCalcContext userUnitFilterCalcContext() {
        if ("external".equalsIgnoreCase(
            SysParametersUtils.getStringValue("wf.userunit.engine.type"))){
            JdbcUserUnitFilterCalcContext uufcc = new JdbcUserUnitFilterCalcContext();
            /* 这个应该可以自动注入
                @Value("${wf.external.system.jdbc.url:}")
                protected String externalJdbcUrl;
                @Value("${wf.external.system.jdbc.user:}")
                protected String externalJdbcUser;
                @Value("${wf.external.system.jdbc.password:}")
                protected String externalJdbcPassword;
             */
            return uufcc;
        } else {
            return new SystemUserUnitFilterCalcContext();
        }
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

}
