package com.centit.workflow.config;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.json.JSONOpt;
import com.centit.support.quartz.JavaBeanJob;
import com.centit.support.quartz.QuartzJobUtils;
import com.centit.workflow.service.impl.FlowTaskImpl;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Random;

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

    @Autowired
    private FlowTaskImpl flowTaskImpl;

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

        // 创建定时任务
        try {
            Random random = new Random();
            int second = random.nextInt() % 60;
            int minute = random.nextInt() % 9 +1;
            String cornExpress = String.valueOf(second) + " " +String.valueOf(minute) + "/10 8-19 * * ? *";
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            QuartzJobUtils.registerJobType("bean", JavaBeanJob.class);
            QuartzJobUtils.createOrReplaceCronJob(scheduler, "flowTaskJob",
                "default", "bean", cornExpress, //"0 0/5 * * * ? *",
                CollectionsOpt.createHashMap("bean", flowTaskImpl,
                    "beanName", "flowTaskImpl",
                    "methodName", "doFlowTimerJob"));
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
