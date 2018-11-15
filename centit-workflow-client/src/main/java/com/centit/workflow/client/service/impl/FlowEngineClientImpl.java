package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.common.ResponseJSON;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.UrlOptUtils;
import com.centit.workflow.client.service.FlowEngineClient;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.UserTask;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.util.*;

/**
 * Created by chen_rj on 2017/7/28.
 */
@Service
public class FlowEngineClientImpl implements FlowEngineClient {

    @Value("${workflow.server}")
    private String workFlowServerUrl;

    public FlowEngineClientImpl() {

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
        appSession = new AppSession(workFlowServerUrl, false, null, null);
    }


    @PostConstruct
    public void init() {
        //this.setWorkFlowServerUrl(workFlowServerUrl);
        makeAppSession();
    }

    @Override
    public String createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode) throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("flowCode", flowCode);
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);

        return RestfulHttpRequest.formPost(appSession, "/flow/engine/createFlowInstDefault", paramMap);

    }

    @Override
    public String createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode, String timeLimitStr) throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("flowCode", flowCode);
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        paramMap.put("timeLimitStr", timeLimitStr);
        return RestfulHttpRequest.formPost(appSession,
            "/flow/engine/createTimeLimitFlowInstDefault", paramMap);
    }

    @Override
    public String createInstance(String flowCode, long version, String flowOptName, String flowOptTag,
                                 String userCode, String unitCode) throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("flowCode", flowCode);
        paramMap.put("version", String.valueOf(version));
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        return RestfulHttpRequest.formPost(appSession,
            "/flow/engine/createFlowInstWithVersion", paramMap);
    }

    @Override
    public FlowInstance createInstanceLockFirstNode(String flowCode, String flowOptName, String flowOptTag,
                                                    String userCode, String unitCode) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowCode", flowCode);
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        return RestfulHttpRequest.getResponseObject(appSession,
            "/flow/engine/createInstanceLockFirstNode", paramMap, FlowInstance.class);
    }

    @Override
    public void saveFlowVariable(long flowInstId, String varName, String varValue) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("varName", varName);
        paramMap.put("varValue", varValue);
        RestfulHttpRequest.formPost(appSession,
            "/flow/engine/saveFlowVariable", paramMap);
    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, List<String> userCodes) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("userCodeList", userCodes);
        RestfulHttpRequest.formPost(appSession,
            "/flow/engine/assignFlowWorkTeam", paramMap);
    }

    @Override
    public void assignFlowOrganize(long flowInstId, String roleCode,
                                   List<String> orgCodeSet) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("orgCodeSet", orgCodeSet);
        RestfulHttpRequest.formPost(appSession,
            "/flow/engine/assignFlowOrganize", paramMap);
    }

    @Override
    public void submitOpt(long nodeInstId, String userCode,
                               String unitCode, String varTrans,
                               ServletContext application) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        paramMap.put("varTrans", varTrans);
        RestfulHttpRequest.formPost(appSession,
            "/flow/engine/submitOpt", paramMap);
    }

    @Override
    public List<FlowVariable> viewFlowVariablesByVarname(long flowInstId, String varName) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("varName", varName);
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/viewFlowVariablesByVarname",
            paramMap, FlowVariable.class);
    }

    @Override
    public void deleteFlowWorkTeam(long flowInstId, String roleCode) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        RestfulHttpRequest.formPost(appSession,
            "/flow/engine/deleteFlowWorkTeam", paramMap);
    }

    @Override
    public List<UserTask> listUserTasks(String userCode, PageDesc pageDesc) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("userCode", userCode);
        paramMap.put("pageDesc", pageDesc);
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/listUserTasks", paramMap, UserTask.class);
    }

    @Override
    public List<FlowInstance> listAllFlowInstByOptTag(String optTag) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowOptTag", optTag);
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/listAllFlowInstByOptTag", paramMap, FlowInstance.class);
    }

    @Override
    public void updateFlowInstOptInfo(long flowInstId, String flowOptName, String flowOptTag) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        RestfulHttpRequest.formPost(appSession,
            "/flow/engine/updateFlowInstOptInfo", paramMap);
    }

    public List<String> viewFlowWorkTeam(long flowInstId, String roleCode) {
        HashMap<java.lang.String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/viewFlowWorkTeam", paramMap, String.class);
    }

    public List<String> viewFlowOrganize(long flowInstId, String roleCode) {
        HashMap<java.lang.String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        return RestfulHttpRequest.getResponseObjectList(appSession, "/flow/engine/viewFlowOrganize", paramMap, String.class);
    }
}
