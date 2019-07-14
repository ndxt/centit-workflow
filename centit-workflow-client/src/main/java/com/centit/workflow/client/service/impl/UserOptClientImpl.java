package com.centit.workflow.client.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.workflow.client.service.UserOptClient;
import com.centit.workflow.po.FlowInstance;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by chen_rj on 2018-5-2.
 */
@Service
public class UserOptClientImpl implements UserOptClient {
    @Value("${workflow.server:}")
    private String workFlowServerUrl;

    public UserOptClientImpl() {

    }

    private AppSession appSession;

    @Override
    public CloseableHttpClient allocHttpClient() throws Exception {
        return appSession.allocHttpClient();
    }

    @Override
    public void releaseHttpClient(CloseableHttpClient httpClient) {
        appSession.releaseHttpClient(httpClient);
    }

    @Override
    public void setWorkFlowServerUrl(String workFlowServerUrl) {
        this.workFlowServerUrl = workFlowServerUrl;
    }

    @Override
    public void saveOptIdeaForAutoSubmit(Map<String, Object> paraMap) {
        RestfulHttpRequest.jsonPost(appSession,"/appr/saveOptIdeaForAutoSubmit",paraMap);
    }


    public void makeAppSession() {
        appSession = new AppSession(workFlowServerUrl, false, null, null);
    }


    @PostConstruct
    public void init() {
        //this.setWorkFlowServerUrl(workFlowServerUrl);
        makeAppSession();
    }


}
