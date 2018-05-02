package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.HttpExecutor;
import com.centit.workflow.client.service.FlowEngineClient;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.UserTask;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${workflow.server}")
    private String workFlowServerUrl;

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
        this.setWorkFlowServerUrl(workFlowServerUrl);
    }

    @Override
    public String createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode) throws Exception{
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("flowCode",flowCode);
        paramMap.put("flowOptName",flowOptName);
        paramMap.put("flowOptTag",flowOptTag);
        paramMap.put("userCode",userCode);
        paramMap.put("unitCode",unitCode);
        String result = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/createFlowInstDefault"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
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
        String result = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/createFlowInstWithVersion"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
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
        CloseableHttpClient httpClient = null;
        String result = null;
        FlowInstance flowInstance = null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/createInstanceLockFirstNode"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String dataStr = jsonObject.get("data").toString();
            flowInstance= JSONObject.parseObject(dataStr,FlowInstance.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flowInstance;
    }

    @Override
    public void saveFlowVariable(long flowInstId,String varName,String varValue) throws Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",flowInstId);
        paramMap.put("varName",varName);
        paramMap.put("varValue",varValue);
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/saveFlowVariable"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, List<String> userCodes) throws Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",flowInstId);
        paramMap.put("roleCode",roleCode);
        paramMap.put("userCodeList", userCodes);
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/assignFlowWorkTeam"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
    }

    @Override
    public Set<Long> submitOpt(long nodeInstId, String userCode,
                               String unitCode, Map<String,Object> varTrans,
                               ServletContext application) throws  Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId",nodeInstId);
        paramMap.put("userCode",userCode);
        paramMap.put("unitCode",unitCode);
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/flow/engine/submitOpt"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
        return  null;
    }

    @Override
    public List<FlowVariable> viewFlowVariablesByVarname(long flowInstId, String varName) throws Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",flowInstId);
        paramMap.put("varName",varName);
        CloseableHttpClient httpClient = null;
        String result = null;
        List<FlowVariable> flowVariables= null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.simpleGet(httpClient,appSession.completeQueryUrl("/flow/engine/viewFlowVariablesByVarname"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String dataStr = jsonObject.get("data").toString();
            flowVariables= JSONObject.parseArray(dataStr,FlowVariable.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flowVariables;
    }
    @Override
    public void deleteFlowWorkTeam(long flowInstId,String roleCode) throws Exception{
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",flowInstId);
        paramMap.put("roleCode",roleCode);
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.simpleGet(httpClient,appSession.completeQueryUrl("/flow/engine/deleteFlowWorkTeam"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
    }

    @Override
    public List<UserTask> listUserTasks(String userCode, PageDesc pageDesc) {
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("userCode",userCode);
        paramMap.put("pageDesc",pageDesc);
        CloseableHttpClient httpClient = null;
        String result = null;
        List<UserTask> userTasks = null;
        try {
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.simpleGet(httpClient,appSession.completeQueryUrl("/flow/engine/listUserTasks"),paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.releaseHttpClient(httpClient);
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String dataStr = jsonObject.get("data").toString();
            userTasks= JSONObject.parseArray(dataStr,UserTask.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userTasks;
    }
}
