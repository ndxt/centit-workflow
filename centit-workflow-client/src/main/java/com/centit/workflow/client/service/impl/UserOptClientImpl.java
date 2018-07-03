package com.centit.workflow.client.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.workflow.client.service.UserOptClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    public void makeAppSession() {
        appSession = new AppSession(workFlowServerUrl,false,null,null);
    }


    @PostConstruct
    public void init(){
        //this.setWorkFlowServerUrl(workFlowServerUrl);
        makeAppSession();
    }


}
