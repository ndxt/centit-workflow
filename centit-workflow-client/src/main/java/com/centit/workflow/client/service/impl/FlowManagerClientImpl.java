package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import com.centit.workflow.client.service.FlowManagerClient;
import com.centit.workflow.po.NodeInstance;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017/7/28.
 */
@Service
public class FlowManagerClientImpl implements FlowManagerClient {

    @Value("${workflow.server:}")
    private String workFlowServerUrl;

    public FlowManagerClientImpl() {

    }
    private AppSession appSession;



    public CloseableHttpClient allocHttpClient() throws Exception {
        return appSession.allocHttpClient();
    }

    public void releaseHttpClient(CloseableHttpClient httpClient) {
        appSession.releaseHttpClient(httpClient);
    }

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
    public List<NodeInstance> listFlowInstNodes(Long wfinstid) throws Exception{
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",String.valueOf(wfinstid));
        CloseableHttpClient httpClient = appSession.allocHttpClient();
        appSession.checkAccessToken(httpClient);
        String result =  HttpExecutor.simpleGet(HttpExecutorContext.create(httpClient),
            appSession.completeQueryUrl("/flow/engine/listFlowInstNodes"),paramMap);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String dataStr = jsonObject.get("data").toString();
        List<NodeInstance> nodeInstances = JSONObject.parseArray(dataStr,NodeInstance.class);
        return nodeInstances;
    }

    @Override
    public void stopAndChangeInstance(long flowInstId,String userCode,String desc) throws Exception{
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",String.valueOf(flowInstId));
        paramMap.put("userCode",userCode);
        paramMap.put("desc",desc);
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.formPost(HttpExecutorContext.create(httpClient),
                appSession.completeQueryUrl("/flow/manager/stopAndChangeInstance"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
    }

}
