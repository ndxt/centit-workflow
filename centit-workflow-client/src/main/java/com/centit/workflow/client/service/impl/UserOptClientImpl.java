package com.centit.workflow.client.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.workflow.client.service.UserOptClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by chen_rj on 2018-5-2.
 */
@Service
@Transactional
public class UserOptClientImpl implements UserOptClient {
    @Value("${workflow.server}")
    private String workFlowServerUrl;

    public UserOptClientImpl() {

    }
    private AppSession appSession;

    @Override
    public CloseableHttpClient getHttpClient() throws Exception {
        return appSession.getHttpClient();
    }

    @Override
    public void releaseHttpClient(CloseableHttpClient httpClient) {
        appSession.releaseHttpClient(httpClient);
    }

    @Override
    public void setWorkFlowServerUrl(String workFlowServerUrl) {
        appSession = new AppSession(workFlowServerUrl,false,null,null);
    }
    @PostConstruct
    public void init(){
        this.setWorkFlowServerUrl(workFlowServerUrl);
    }
}
