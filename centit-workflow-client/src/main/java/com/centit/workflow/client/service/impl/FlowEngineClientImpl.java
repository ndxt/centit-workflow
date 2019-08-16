package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.client.service.FlowEngineClient;
import com.centit.workflow.commons.WorkflowException;
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

    @Value("${workflow.server:}")
    private String workFlowServerUrl;
    @Value("${workflow.server.login:}")
    private String workFlowServerLoginUrl;

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
        appSession.setAppLoginUrl(workFlowServerLoginUrl);
    }


    @PostConstruct
    public void init() {
        //this.setWorkFlowServerUrl(workFlowServerUrl);
        makeAppSession();
    }

    private FlowInstance jsonToFlowInstance(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String flowStr = jsonObject.getString("data");
        return JSONObject.parseObject(flowStr, FlowInstance.class);
    }

    @Override
    public FlowInstance createMetaFormFlowAndSubmit(String modelId, String flowOptName, String flowOptTag, String userCode, String unitCode) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("modelId", modelId);
        jsonObject.put("flowOptName", flowOptName);
        jsonObject.put("flowOptTag", flowOptTag);
        jsonObject.put("userCode", userCode);
        jsonObject.put("unitCode", unitCode);
        String flowJson = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createMetaFormFlowAndSubmit", jsonObject);
        return jsonToFlowInstance(flowJson);

    }

    @Override
    public FlowInstance createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flowCode", flowCode);
        jsonObject.put("flowOptName", flowOptName);
        jsonObject.put("flowOptTag", flowOptTag);
        jsonObject.put("userCode", userCode);
        jsonObject.put("unitCode", unitCode);
        String flowJson = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createFlowInstDefault", jsonObject);
        return jsonToFlowInstance(flowJson);

    }


    /**
     * @param flowCode     流程编码
     * @param flowOptName  这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag   这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode     创建用户
     * @param unitCode     将流程指定一个所属机构
     * @param timeLimitStr 流程计时 默认单位为天，也可以手动设定为d\h\m
     * @return
     * @throws Exception
     */
    @Override
    public FlowInstance createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode, String timeLimitStr) throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("flowCode", flowCode);
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        paramMap.put("timeLimitStr", timeLimitStr);
        String flowJson = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createFlowInstDefault", paramMap);
        return jsonToFlowInstance(flowJson);
    }

    @Override
    public FlowInstance createInstance(String flowCode, long version, String flowOptName, String flowOptTag,
                                 String userCode, String unitCode) throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("flowCode", flowCode);
        paramMap.put("version", String.valueOf(version));
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        String flowJson = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createFlowInstDefault", paramMap);
        return jsonToFlowInstance(flowJson);
    }

    @Override
    public FlowInstance createInstanceLockFirstNode(String flowCode, String flowOptName, String flowOptTag,
                                              String userCode, String unitCode) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flowCode", flowCode);
        jsonObject.put("flowOptName", flowOptName);
        jsonObject.put("flowOptTag", flowOptTag);
        jsonObject.put("userCode", userCode);
        jsonObject.put("unitCode", unitCode);
        String flowJson = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createInstanceLockFirstNode", jsonObject);
        return jsonToFlowInstance(flowJson);
    }

    @Override
    public void saveFlowVariable(String flowInstId, String varName, String varValue) throws Exception {
        Set<String> vars = new HashSet<>(Arrays.asList(varValue));
        saveFlowVariable(flowInstId, varName, vars);
    }

    @Override
    public void saveFlowVariable(String flowInstId, String varName, Set<String> varValue) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("varName", varName);
        paramMap.put("varValue", varValue);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/saveFlowVariable", paramMap);
    }

    /**
     * @param nodeInstId
     * @param varName
     * @param varValue   SET &lt;String&gt;
     * @throws Exception
     */
    @Override
    public void saveFlowNodeVariable(String nodeInstId, String varName, String varValue) throws Exception {
        Set<String> vars = new HashSet<>(Arrays.asList(varValue));
        saveFlowNodeVariable(nodeInstId, varName, vars);
    }

    /**
     * @param nodeInstId
     * @param varName
     * @param varValue   SET &lt;String&gt;
     * @throws Exception
     */
    @Override
    public void saveFlowNodeVariable(String nodeInstId, String varName, Set<String> varValue) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("varName", varName);
        paramMap.put("varValue", varValue);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/saveFlowNodeVariable", paramMap);
    }

    @Override
    public void assignFlowWorkTeam(String flowInstId, String roleCode, List<String> userCodes) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("userCode", userCodes);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/assignFlowWorkTeam", paramMap);
    }

    @Override
    public void addFlowWorkTeam(String flowInstId, String roleCode, String userCode) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("userCode", userCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/addFlowWorkTeam", paramMap);
    }

    @Override
    public void assignFlowOrganize(String flowInstId, String roleCode,
                                   List<String> orgCodeSet) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("orgCodeSet", orgCodeSet);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/assignFlowOrganize", paramMap);
    }

    @Override
    public void addFlowOrganize(String flowInstId, String roleCode,
                                String unitCode) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("unitCode", unitCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/addFlowOrganize", paramMap);
    }

    @Override
    public Map<String,Object> submitOpt(String nodeInstId, String userCode,
                                         String unitCode, String varTrans,
                                         ServletContext application) throws WorkflowException {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        paramMap.put("varTrans", varTrans);
        String returnJson = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/submitOpt", paramMap);
        Map<String,Object> jsonObject=JSONObject.parseObject(returnJson);
        return jsonObject;

    }

    @Override
    public List<FlowVariable> viewFlowVariablesByVarname(String flowInstId, String varName) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("varName", varName);
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/viewFlowVariablesByVarname",
            paramMap, FlowVariable.class);
    }

    @Override
    public void deleteFlowWorkTeam(String flowInstId, String roleCode) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/deleteFlowWorkTeam", paramMap);
    }

    @Override
    public void deleteFlowOrganize(String flowInstId, String roleCode) throws Exception {
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
    public List<UserTask> listNodeTaskUsers(String nodeInstId) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/listNodeTaskUsers", paramMap, UserTask.class);
    }

    @Override
    public List<FlowInstance> listAllFlowInstByOptTag(String optTag) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowOptTag", optTag);
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/listAllFlowInstByOptTag", paramMap, FlowInstance.class);
    }

    @Override
    public void updateFlowInstOptInfo(String flowInstId, String flowOptName, String flowOptTag) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("flowOptName", flowOptName);
        paramMap.put("flowOptTag", flowOptTag);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/updateFlowInstOptInfo", paramMap);
    }

    public List<String> viewFlowWorkTeam(String flowInstId, String roleCode) {
        HashMap<java.lang.String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/viewFlowWorkTeam", paramMap, String.class);
    }

    public List<String> viewFlowOrganize(String flowInstId, String roleCode) {
        HashMap<java.lang.String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        return RestfulHttpRequest.getResponseObjectList(appSession, "/flow/engine/viewFlowOrganize", paramMap, String.class);
    }


    @Override
    public void createNodeInst(String flowInstId, String createUser,
                               String nodeId, List<String> userCodes, String unitCode) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("createUser", createUser);
        paramMap.put("userCodes", userCodes);
        paramMap.put("nodeId", nodeId);
        paramMap.put("unitCode", unitCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createNodeInst", paramMap);
    }


    public void deleteFlowVariable(String flowInstId, String runToken, String varName) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("runToken", runToken);
        paramMap.put("varName", varName);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/deleteFlowVariable", paramMap);
    }

    public void rollBackNode(String nodeInstId,String managerUserCode){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("managerUserCode", managerUserCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/rollBackNode", paramMap);
    }
}
