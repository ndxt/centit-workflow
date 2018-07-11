package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import com.centit.workflow.client.service.FlowExtendClient;
import com.centit.workflow.po.FlowInstance;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen_rj on 2018-7-11.
 */
@Service
public class FlowExtendClientImpl implements FlowExtendClient{
    @Value("${workflow.server}")
    private String workFlowServerUrl;

    public FlowExtendClientImpl() {

    }
    private AppSession appSession;


    @Override
    public CloseableHttpClient getHttpClient() throws Exception {
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

    @Override
    public void updateFlowInstance(FlowInstance flowInstance) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            HttpExecutor.jsonPost(HttpExecutorContext.create(httpClient),
                appSession.completeQueryUrl("/flowExtend/updateFlowInstance"), flowInstance);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
    }
}
