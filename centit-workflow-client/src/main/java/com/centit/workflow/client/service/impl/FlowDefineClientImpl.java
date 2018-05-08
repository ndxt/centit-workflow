package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.HttpExecutor;
import com.centit.workflow.client.service.FlowDefineClient;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.po.UserTask;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2018-5-2.
 */
@Service
@Transactional
public class FlowDefineClientImpl implements FlowDefineClient {
    @Value("${workflow.server}")
    private String workFlowServerUrl;

    public FlowDefineClientImpl() {

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

    @Override
    public List<FlowInfo> list() {
        Map<String,Object> paramMap = new HashMap<>();
        String result = null;
        CloseableHttpClient httpClient = null;
        List<FlowInfo> flowInfos = null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.simpleGet(httpClient,appSession.completeQueryUrl("/flow/define"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String dataStr = jsonObject.getJSONObject("data").get("objList").toString();
            flowInfos= JSONObject.parseArray(dataStr,FlowInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flowInfos;
    }
}
