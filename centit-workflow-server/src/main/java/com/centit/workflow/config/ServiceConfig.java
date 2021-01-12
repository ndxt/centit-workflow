package com.centit.workflow.config;

import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.ip.app.config.IPOrStaticAppSystemBeanConfig;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.product.message.EmailMessageSenderImpl;
import com.centit.workflow.context.ExtFrameworkContextCacheBean;
import com.centit.workflow.context.JdbcUserUnitCalcContextFactoryImpl;
import com.centit.workflow.service.impl.SystemUserUnitCalcContextFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;


/**
 * Created by codefan on 17-7-18.
 */
@Configuration
@Import({IPOrStaticAppSystemBeanConfig.class,
        JdbcConfig.class,
        SpringSecurityDaoConfig.class,
    SpringSecurityCasConfig.class})
@ComponentScan(basePackages = "com.centit",
        excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceConfig {

    @Value("${wf.external.system.jdbc.url:}")
    protected String externalJdbcUrl;
    @Value("${wf.external.system.jdbc.user:}")
    protected String externalJdbcUser;
    @Value("${wf.external.system.jdbc.password:}")
    protected String externalJdbcPassword;
    @Value("${wf.userunit.engine.type:system}")
    protected String engineType;


    @Value("${app.home:/}")
    protected String appHome;

    @Bean("passwordEncoder")
    public CentitPasswordEncoder passwordEncoder(){
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    public NotificationCenter notificationCenter(@Autowired PlatformEnvironment platformEnvironment) {
        EmailMessageSenderImpl messageManager = new EmailMessageSenderImpl();
        messageManager.setHostName("mail.centit.com");
        messageManager.setSmtpPort(25);
        messageManager.setUserName("accounts");
        messageManager.setUserPassword("yhs@yhs1");
        messageManager.setServerEmail("noreplay@centit.com");

        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        //notificationCenter.initDummyMsgSenders();
        notificationCenter.setPlatformEnvironment(platformEnvironment);
        notificationCenter.registerMessageSender("email",messageManager);
        notificationCenter.appointDefaultSendType("email");
        return notificationCenter;
    }

    @Bean
    public UserUnitFilterCalcContextFactory userUnitFilterFactory() {
        if ("external".equalsIgnoreCase(engineType)) { //jdbc
            ExtFrameworkContextCacheBean contextCacheBean = new ExtFrameworkContextCacheBean();
            contextCacheBean.setDatabaseSource(externalJdbcUrl, externalJdbcUser, externalJdbcPassword);
            JdbcUserUnitCalcContextFactoryImpl factoryBean = new JdbcUserUnitCalcContextFactoryImpl();
            factoryBean.setExtFrameworkContextCacheBean(contextCacheBean);
            return factoryBean;
        } else{
            return new SystemUserUnitCalcContextFactoryImpl();
        }
    }

    /*@Bean 注入 opt-log-module 将操作日志持久化
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl operationLog = new TextOperationLogWriterImpl();
        operationLog.setOptLogHomePath(
            FileSystemOpt.appendPath(appHome , "logs"));
        operationLog.init();
        return operationLog;
    }*/

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

}
