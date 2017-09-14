package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.support.network.HttpExecutor;
import com.centit.workflow.client.service.FlowManagerClient;
import com.centit.workflow.po.NodeInstance;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017/7/28.
 */
@Service
@Transactional
public class FlowManagerClientImpl implements FlowManagerClient {
    public FlowManagerClientImpl() {

    }
    private AppSession appSession;



    public CloseableHttpClient getHttpClient() throws Exception {
        return appSession.getHttpClient();
    }

    public void releaseHttpClient(CloseableHttpClient httpClient) {
        appSession.releaseHttpClient(httpClient);
    }

    public void setWorkFlowServerUrl(String workFlowServerUrl) {
        appSession = new AppSession(workFlowServerUrl,false,null,null);
    }

    @PostConstruct
    public void init(){
        this.setWorkFlowServerUrl("http://localhost:8080/workflow/service");
    }
    @Override
    public List<NodeInstance> listFlowInstNodes(Long wfinstid) throws Exception{
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",String.valueOf(wfinstid));
        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String result =  HttpExecutor.simpleGet(httpClient,appSession.completeQueryUrl("/flow/engine/listFlowInstNodes"),paramMap);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String dataStr = jsonObject.get("data").toString();
        List<NodeInstance> nodeInstances = JSONObject.parseArray(dataStr,NodeInstance.class);
        return nodeInstances;
    }

}
