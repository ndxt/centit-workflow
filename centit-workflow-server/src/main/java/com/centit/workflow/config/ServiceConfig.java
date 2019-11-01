package com.centit.workflow.config;

import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.ip.app.config.IPOrStaticAppSystemBeanConfig;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.framework.ip.service.impl.JsonIntegrationEnvironment;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.support.file.FileSystemOpt;
import com.centit.workflow.external.ExtFrameworkContextCacheBean;
import com.centit.workflow.service.UserUnitFilterCalcContextFactory;
import com.centit.workflow.service.impl.UserUnitFilterCalcContextFactoryImpl;
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
    public IntegrationEnvironment integrationEnvironment(){
        return new JsonIntegrationEnvironment();
    }

    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        //notificationCenter.initMsgSenders();
        //notificationCenter.registerMessageSender("innerMsg",innerMessageManager);
        return notificationCenter;
    }

    @Bean
    public ExtFrameworkContextCacheBean extFrameworkContextCacheBean(){
        ExtFrameworkContextCacheBean contextCacheBean = new ExtFrameworkContextCacheBean();
        contextCacheBean.setDatabaseSource(externalJdbcUrl, externalJdbcUser, externalJdbcPassword);
        return contextCacheBean;
    }

    @Bean
    public UserUnitFilterCalcContextFactory userUnitFilterFactory(@Autowired ExtFrameworkContextCacheBean extFrameworkContextCacheBean){
        UserUnitFilterCalcContextFactoryImpl factoryBean = new UserUnitFilterCalcContextFactoryImpl();
        factoryBean.setEngineType(engineType);
        factoryBean.setExtFrameworkContextCacheBean(extFrameworkContextCacheBean);
        return factoryBean;
    }

    @Bean
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl operationLog = new TextOperationLogWriterImpl();
        operationLog.setOptLogHomePath(
            FileSystemOpt.appendPath(appHome , "logs"));
        operationLog.init();
        return operationLog;
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

}
