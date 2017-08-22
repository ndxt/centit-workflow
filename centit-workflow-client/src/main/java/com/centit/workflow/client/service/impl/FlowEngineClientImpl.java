package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.support.network.HttpExecutor;
import com.centit.workflow.client.po.FlowInstance;
import com.centit.workflow.client.po.FlowVariable;
import com.centit.workflow.client.service.FlowEngineClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chen_rj on 2017/7/28.
 */
@Service
@Transactional
public class FlowEngineClientImpl implements FlowEngineClient {
    public FlowEngineClientImpl() {

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
    public String createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode) throws Exception{
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("flowCode",flowCode);
        paramMap.put("flowOptName",flowOptName);
        paramMap.put("flowOptTag",flowOptTag);
        paramMap.put("userCode",userCode);
        paramMap.put("unitCode",unitCode);
        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String result = null;
        result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/createFlowInstDefault"),paramMap);
        appSession.releaseHttpClient(httpClient);
        return result;
    }

    @Override
    public String createInstance(String flowCode, long version, String flowOptName, String flowOptTag, String userCode, String unitCode) throws Exception{
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("flowCode",flowCode);
        paramMap.put("version",String.valueOf(version));
        paramMap.put("flowOptName",flowOptName);
        paramMap.put("flowOptTag",flowOptTag);
        paramMap.put("userCode",userCode);
        paramMap.put("unitCode",unitCode);
        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/createFlowInstWithVersion"),paramMap);
        appSession.releaseHttpClient(httpClient);
        return result;
    }

    @Override
    public String createInstance(String flowCode, long version, String flowOptName, String flowOptTag, String userCode, String unitCode, Map<String, Object> varTrans, ServletContext application) throws Exception{
        return null;
    }

    @Override
    public FlowInstance createInstanceLockFirstNode(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode) throws Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowCode",flowCode);
        paramMap.put("flowOptName",flowOptName);
        paramMap.put("flowOptTag",flowOptTag);
        paramMap.put("userCode",userCode);
        paramMap.put("unitCode",unitCode);
        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/createInstanceLockFirstNode"),paramMap);
        appSession.releaseHttpClient(httpClient);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String dataStr = jsonObject.get("data").toString();
        FlowInstance flowInstance= JSONObject.parseObject(dataStr,FlowInstance.class);
        return flowInstance;
    }

    @Override
    public void saveFlowVariable(long flowInstId,String varName,String varValue) throws Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",flowInstId);
        paramMap.put("varName",varName);
        paramMap.put("varValue",varValue);
        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/saveFlowVariable"),paramMap);
        appSession.releaseHttpClient(httpClient);
    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, List<String> userCodes) throws Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",flowInstId);
        paramMap.put("roleCode",roleCode);
        paramMap.put("userCodeList", userCodes);
        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/assignFlowWorkTeam"),paramMap);
        appSession.releaseHttpClient(httpClient);
    }

    @Override
    public Set<Long> submitOpt(long nodeInstId, String userCode,
                               String unitCode, Map<String,Object> varTrans,
                               ServletContext application) throws  Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId",nodeInstId);
        paramMap.put("userCode",userCode);
        paramMap.put("unitCode",unitCode);
        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/submitOpt"),paramMap);
        appSession.releaseHttpClient(httpClient);
        return  null;
    }

    @Override
    public List<FlowVariable> viewFlowVariablesByVarname(long flowInstId, String varName) throws Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",flowInstId);
        paramMap.put("varName",varName);
        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String result =  HttpExecutor.simpleGet(httpClient,appSession.completeQueryUrl("/flow/engine/viewFlowVariablesByVarname"),paramMap);
        appSession.releaseHttpClient(httpClient);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String dataStr = jsonObject.get("data").toString();
        List<FlowVariable> flowVariables= JSONObject.parseArray(dataStr,FlowVariable.class);
        return flowVariables;
    }
    @Override
    public void deleteFlowWorkTeam(long flowInstId,String roleCode) throws Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",flowInstId);
        paramMap.put("roleCode",roleCode);
        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String result =  HttpExecutor.simpleGet(httpClient,appSession.completeQueryUrl("/flow/engine/deleteFlowWorkTeam"),paramMap);
        appSession.releaseHttpClient(httpClient);
    }
}
