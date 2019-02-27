package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.client.service.FlowEngineClient;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.UserTask;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flowCode", flowCode);
        jsonObject.put("flowOptName", flowOptName);
        jsonObject.put("flowOptTag", flowOptTag);
        jsonObject.put("userCode", userCode);
        jsonObject.put("unitCode", unitCode);

        return RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createFlowInstDefault", jsonObject);

    }


    /**
     * @param flowCode 流程编码
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode 创建用户
     * @param unitCode 将流程指定一个所属机构
     * @param timeLimitStr 流程计时 默认单位为天，也可以手动设定为d\h\m
     * @return
     * @throws Exception
     */
    @Override
    public String createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode, String timeLimitStr) throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("flowCode", flowCode);
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        paramMap.put("timeLimitStr", timeLimitStr);
        return RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createFlowInstDefault", paramMap);
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
            "/flow/engine/createFlowInstDefault", paramMap);
    }

    @Override
    public String createInstanceLockFirstNode(String flowCode, String flowOptName, String flowOptTag,
                                                    String userCode, String unitCode) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowCode", flowCode);
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        return RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createInstanceLockFirstNode", paramMap);
    }

    @Override
    public void saveFlowVariable(long flowInstId, String varName, String varValue) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("varName", varName);
        paramMap.put("varValue", varValue);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/saveFlowVariable", paramMap);
    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, List<String> userCodes) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("userCode", userCodes);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/assignFlowWorkTeam", paramMap);
    }

    @Override
    public void addFlowWorkTeam(long flowInstId, String roleCode, String userCode) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("userCode", userCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/addFlowWorkTeam", paramMap);
    }

    @Override
    public void assignFlowOrganize(long flowInstId, String roleCode,
                                   List<String> orgCodeSet) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("orgCodeSet", orgCodeSet);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/assignFlowOrganize", paramMap);
    }

    @Override
    public void addFlowOrganize(long flowInstId, String roleCode,
                                   String unitCode) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("unitCode", unitCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/addFlowOrganize", paramMap);
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
        RestfulHttpRequest.jsonPost(appSession,
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
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/deleteFlowWorkTeam", paramMap);
    }

    @Override
    public void deleteFlowOrganize(long flowInstId, String roleCode) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/deleteFlowOrganize", paramMap);
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
        RestfulHttpRequest.jsonPost(appSession,
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


    @Override
    public void createNodeInst(long flowInstId, String createUser,
                                             String nodeId,List<String> userCodes, String unitCode) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("createUser", createUser);
        paramMap.put("userCodes", userCodes);
        paramMap.put("nodeId", nodeId);
        paramMap.put("unitCode", unitCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createNodeInst", paramMap);
    }
}
