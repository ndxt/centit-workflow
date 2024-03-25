package com.centit.workflow.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.security.CentitUserDetailsService;
import com.centit.framework.security.StandardPasswordEncoderImpl;
import com.centit.framework.security.UserDetailsServiceImpl;
import com.centit.msgpusher.plugins.EMailMsgPusher;
import com.centit.msgpusher.plugins.SystemUserEmailSupport;
import com.centit.search.service.ESServerConfig;
import com.centit.search.service.IndexerSearcherFactory;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.security.SecurityOptUtils;
import com.centit.workflow.service.impl.SystemUserUnitCalcContextFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


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

    @Autowired
    Environment env;

    @Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }


    @Bean
    public CentitUserDetailsService centitUserDetailsService(@Autowired PlatformEnvironment platformEnvironment) {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPlatformEnvironment(platformEnvironment);
        return userDetailsService;
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("classpath:i18n/messages");
        ms.setDefaultEncoding("UTF-8");
        return ms;
    }

    @Bean
    public LocalValidatorFactoryBean validatorFactory() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public NotificationCenter notificationCenter(@Autowired PlatformEnvironment platformEnvironment) {
        EMailMsgPusher messageManager = new EMailMsgPusher();
        messageManager.setEmailServerHost("mail.centit.com");
        messageManager.setEmailServerPort(25);
        messageManager.setEmailServerUser("alertmail2@centit.com");
        messageManager.setEmailServerPwd(SecurityOptUtils.decodeSecurityString("cipher:o6YOHiUOg8jBZFkQtGW/9Q=="));
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
        /*if ("external".equalsIgnoreCase(engineType)) { //jdbc
            ExtFrameworkContextCacheBean contextCacheBean = new ExtFrameworkContextCacheBean();
            contextCacheBean.setDatabaseSource(externalJdbcUrl, externalJdbcUser, externalJdbcPassword);
            JdbcUserUnitCalcContextFactoryImpl factoryBean = new JdbcUserUnitCalcContextFactoryImpl();
            factoryBean.setExtFrameworkContextCacheBean(contextCacheBean);
            return factoryBean;
        } else {*/
            return new SystemUserUnitCalcContextFactoryImpl();
    }

    @Bean
    public ESServerConfig esServerConfig() {
        ESServerConfig config = new ESServerConfig();
        config.setServerHostIp(env.getProperty("elasticsearch.server.ip"));
        config.setServerHostPort(env.getProperty("elasticsearch.server.port"));
        config.setClusterName(env.getProperty("elasticsearch.server.cluster"));
        config.setOsId(env.getProperty("elasticsearch.osId"));
        config.setUsername(env.getProperty("elasticsearch.server.username"));
        config.setPassword(env.getProperty("elasticsearch.server.password"));
        config.setMinScore(NumberBaseOpt.parseFloat(env.getProperty("elasticsearch.filter.minScore"), 0.5F));
        return config;
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

}
