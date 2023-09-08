package com.centit.workflow.config;

import com.centit.framework.components.impl.Log4jOperationLogWriterImpl;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.core.service.DataScopePowerManager;
import com.centit.framework.core.service.impl.DataScopePowerManagerImpl;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.security.StandardPasswordEncoderImpl;
import com.centit.workflow.service.impl.SystemUserUnitCalcContextFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhf
 */
@Configuration
public class ServiceConfig {

    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        notificationCenter.initDummyMsgSenders();
        return notificationCenter;
    }
    @Bean
    public UserUnitFilterCalcContextFactory userUnitFilterFactory() {
        return new SystemUserUnitCalcContextFactoryImpl();
    }
    @Bean
    public OperationLogWriter operationLogWriter() {
        OperationLogWriter textOperationLogWriter
            = new Log4jOperationLogWriterImpl();
        //textOperationLogWriter.setOptLogHomePath(pathConfig.getLogs());
        return textOperationLogWriter;
    }

    @Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return  new StandardPasswordEncoderImpl();
    }

    @Bean
    public DataScopePowerManager queryDataScopeFilter() {
        return new DataScopePowerManagerImpl();
    }
}
