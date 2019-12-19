package com.centit.workflow.client.service.impl;

import com.centit.framework.appclient.AppSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by codefan on 2019/12/19.
 */
@Service
public class WorkflowAppSession extends AppSession {
    @Value("${workflow.server.url:}")
    private String workflowUrl;

    @Value("${workflow.server.loginUrl:}")
    private String workflowLoginUrl;

    @Value("${workflow.server.username:}")
    private String workflowUser;

    @Value("${workflow.server.password:}")
    private String workflowPassword;


    @PostConstruct
    public void init(){
        //this.setWorkFlowServerUrl(workFlowServerUrl);
        super.setAppServerUrl(workflowUrl);
        if(StringUtils.isNotBlank(workflowLoginUrl)){
            super.setAppLoginUrl(workflowLoginUrl);
        }
        super.setNeedAuthenticated(StringUtils.isNotBlank(workflowUser)
            && StringUtils.isNotBlank(workflowPassword) );
        super.setUserCode(workflowUser);
        super.setPassword(workflowPassword);
    }
}
