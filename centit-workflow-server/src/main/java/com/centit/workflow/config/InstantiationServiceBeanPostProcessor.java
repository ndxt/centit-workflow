package com.centit.workflow.config;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.support.json.JSONOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by codefan on 17-7-6.
 */
public class InstantiationServiceBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent>
{

    @Autowired
    protected NotificationCenter notificationCenter;

    @Autowired
    private OperationLogWriter optLogManager;

    @Autowired(required = false)
    private MessageSender innerMessageManager;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        JSONOpt.fastjsonGlobalConfig();
        CodeRepositoryCache.setPlatformEnvironment(platformEnvironment);
        CodeRepositoryCache.refreshAsyncCache();
        if(innerMessageManager!=null)
            notificationCenter.registerMessageSender("innerMsg", innerMessageManager);
        if(optLogManager!=null)
            OperationLogCenter.registerOperationLogWriter(optLogManager);
    }

}
