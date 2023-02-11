package com.centit.workflow.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;

import com.centit.msgpusher.plugins.EMailMsgPusher;
import com.centit.msgpusher.plugins.SystemUserEmailSupport;
import com.centit.support.security.AESSecurityUtils;
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
@Import({//IPOrStaticAppSystemBeanConfig.class,
    JdbcConfig.class,
    SpringSecurityDaoConfig.class,
    SpringSecurityCasConfig.class})
@ComponentScan(basePackages = "com.centit",
    excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.server-addr}"))
@NacosPropertySources({@NacosPropertySource(dataId = "${nacos.system-dataid}",groupId = "CENTIT", autoRefreshed = true)}
)
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

   /* @Bean("passwordEncoder")
    public CentitPasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }*/

    @Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    public NotificationCenter notificationCenter(@Autowired PlatformEnvironment platformEnvironment) {
        EMailMsgPusher messageManager = new EMailMsgPusher();
        messageManager.setEmailServerHost("mail.centit.com");
        messageManager.setEmailServerPort(25);
        messageManager.setEmailServerUser("alertmail2@centit.com");
        messageManager.setEmailServerPwd(AESSecurityUtils.decryptBase64String("LZhLhIlJ6gtIlUZ6/NassA==", ""));
        messageManager.setUserEmailSupport(new SystemUserEmailSupport());

        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        notificationCenter.setPlatformEnvironment(platformEnvironment);
        //禁用发送email
        notificationCenter.registerMessageSender("email", messageManager);
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
        } else {
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
