package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.UrlOptUtils;
import com.centit.workflow.client.service.FlowEngineClient;
import com.centit.workflow.commons.CreateFlowOptions;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

    public CloseableHttpClient getHttpClient()  throws Exception{
        return appSession.allocHttpClient();
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

    @Override
    public FlowInstance createInstance(CreateFlowOptions options) {
        //JSONObject jsonObject = (JSONObject)JSON.toJSON(options);
        String flowJson = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/createInstance", options);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(flowJson);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getDataAsObject(FlowInstance.class);
    }



    @Override
    public void saveFlowVariable(String flowInstId, String varName, String varValue)  {
        Set<String> vars = new HashSet<>(Arrays.asList(varValue));
        saveFlowVariable(flowInstId, varName, vars);
    }

    @Override
    public void saveFlowVariable(String flowInstId, String varName, Set<String> varValue)  {
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
     * @
     */
    @Override
    public void saveFlowNodeVariable(String nodeInstId, String varName, String varValue)  {
        Set<String> vars = new HashSet<>(Arrays.asList(varValue));
        saveFlowNodeVariable(nodeInstId, varName, vars);
    }

    /**
     * @param nodeInstId
     * @param varName
     * @param varValue   SET &lt;String&gt;
     * @
     */
    @Override
    public void saveFlowNodeVariable(String nodeInstId, String varName, Set<String> varValue)  {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("varName", varName);
        paramMap.put("varValue", varValue);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/saveFlowNodeVariable", paramMap);
    }

    @Override
    public void assignFlowWorkTeam(String flowInstId, String roleCode, List<String> userCodes)  {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("userCode", userCodes);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/assignFlowWorkTeam", paramMap);
    }

    @Override
    public void addFlowWorkTeam(String flowInstId, String roleCode, String userCode)  {
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
    public Map<String, Object> submitOpt(SubmitOptOptions options) throws WorkflowException {
        String returnJson = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/submitOpt", options);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(returnJson);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getJSONObject();
    }

    /**
     * 获取流程实例信息
     *
     * @param flowInstId 实例id
     * @return 实例信息
     */
    @Override
    public FlowInstance getFlowInstance(String flowInstId) {
        return RestfulHttpRequest.getResponseObject(appSession,
            "/flow/engine/inst/"+flowInstId, FlowInstance.class);
    }

    /**
     * 获取流程定义信息
     *
     * @param flowInstId 实例id
     * @return 流程定义信息
     */
    @Override
    public FlowInfo getFlowDefine(String flowInstId) {
        return RestfulHttpRequest.getResponseObject(appSession,
            "/flow/engine/instDef/"+flowInstId, FlowInfo.class);
    }

    /**
     * 获取流程业务信息
     * @param flowInstId 实例id
     * @return 流程业务信息
     */
    @Override
    public FlowOptInfo getFlowOptInfo(String flowInstId){
        return RestfulHttpRequest.getResponseObject(appSession,
            "/flow/engine/optInfo/"+flowInstId, FlowOptInfo.class);
    }
    /**
     * 获取节点实例 Id
     *
     * @param nodeInstId 节点实例id
     * @return 节点实例信息
     */
    @Override
    public NodeInstance getNodeInstance(String nodeInstId) {
        return RestfulHttpRequest.getResponseObject(appSession,
            "/flow/engine/nodeInst/"+nodeInstId, NodeInstance.class);
    }

    /**
     * 获取节点定义信息
     *
     * @param nodeInstId 节点实例id
     * @return 节点实例信息
     */
    @Override
    public NodeInfo getNodeInfo(String nodeInstId) {
        return RestfulHttpRequest.getResponseObject(appSession,
            "/flow/engine/nodeDef/"+nodeInstId, NodeInfo.class);
    }

    @Override
    public List<FlowVariable> viewFlowVariablesByVarname(String flowInstId, String varName)  {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("varName", varName);
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/viewFlowVariablesByVarname",
            paramMap, FlowVariable.class);
    }

    @Override
    public void deleteFlowWorkTeam(String flowInstId, String roleCode)  {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/deleteFlowWorkTeam", paramMap);
    }

    @Override
    public void deleteFlowOrganize(String flowInstId, String roleCode)  {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/deleteFlowOrganize", paramMap);
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
            "/flow/engine/updateFlowOptInfo", paramMap);
    }

    @Override
    public void updateNodeInstanceParam(String nodeInstId, String nodeParam) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("nodeParam", nodeParam);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/updateNodeParam", paramMap);
    }

    /**
     * 针对 抢先类别的 节点， 锁定任务，这个任务后续只能由 他来做
     * @param nodeInstId 节点实例id
     * @param userCode  用户
     */
    @Override
    public void lockNodeTask(String nodeInstId, String userCode){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("userCode", userCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/lockTask", paramMap);
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
        return RestfulHttpRequest.getResponseObjectList(appSession,
            "/flow/engine/viewFlowOrganize", paramMap, String.class);
    }


    public void deleteFlowVariable(String flowInstId, String runToken, String varName) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("runToken", runToken);
        paramMap.put("varName", varName);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/deleteFlowVariable", paramMap);
    }

    /**
     * 检查后续的节点是否被操作过，包括更新和提交
     * 只有后续节点没有处理的才可以收回。true表示可以撤回，false表示不可以撤回，
     *
     * @param nodeInstId 流程实例id
     * @return 是否可以回收
     */
    @Override
    public boolean nodeCanBeReclaim(String nodeInstId) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/engine/nodeCanBeReclaim/"+nodeInstId);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return BooleanBaseOpt.castObjectToBoolean(receiveJSON.getData(), false);
    }

    public void rollBackNode(String nodeInstId,String managerUserCode){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("managerUserCode", managerUserCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/rollBackNode", paramMap);
    }

    /**
     * 列举当前流程可以创建的所有节点
     * @param flowInstId 流程实例代码
     * @return Map 节点代码， 节点名称
     */
    @Override
    public Map<String, String> listFlowNodeForCreate(String flowInstId){
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/engine/nodeForCreate/"+flowInstId);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getDataAsMap(String.class);
    }
    /**
     * 创建孤立节点  知会、关注
     * <p>
     * 用户手动创建一个节点实例，不影响当前节点实例的执行,当前节点实例Id也可以为空
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode      节点 的环节代码
     * @param createUser    创建用户
     * @param userCode      指定用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    @Override
    public NodeInstance createIsolatedNodeInst(String flowInstId, String curNodeInstId,
                                               String nodeCode, String createUser,
                                               String userCode, String unitCode) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("curNodeInstId", curNodeInstId);
        paramMap.put("createUser", createUser);
        paramMap.put("nodeCode", nodeCode);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        String json =  RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/isolatedNode", paramMap);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(json);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getDataAsObject(NodeInstance.class);
    }

    /**
     * 加签,并指定到人
     * <p>
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode      节点环节代码，这个节点在这个流程中必需唯一
     * @param createUser    当前创建用户
     * @param userCode      指定操作用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    @Override
    public NodeInstance createPrepNodeInst(String flowInstId, String curNodeInstId,
                                           String nodeCode, String createUser,
                                           String userCode, String unitCode) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("curNodeInstId", curNodeInstId);
        paramMap.put("createUser", createUser);
        paramMap.put("nodeCode", nodeCode);
        paramMap.put("userCode", userCode);
        paramMap.put("unitCode", unitCode);
        String json =  RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/prepNode", paramMap);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(json);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getDataAsObject(NodeInstance.class);
    }

    /**
     * 创建 流程分组
     *
     * @param name 分组名称
     * @param desc 分组描述
     * @return 流程分组
     */
    @Override
    public FlowInstanceGroup createFlowInstGroup(String name, String desc) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowGroupName", name);
        paramMap.put("flowGroupDesc", desc);
        String json =  RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/flowGroup", paramMap);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(json);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getDataAsObject(FlowInstanceGroup.class);
    }

    /**
     * 查询流程组信息
     *
     * @param paramMap 查询参数
     * @param pageDesc 分页信息
     * @return 流程分组
     */
    @Override
    public JSONArray listFlowInstGroup(Map<String, Object> paramMap, PageDesc pageDesc) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl(
                UrlOptUtils.appendParamsToUrl("/flow/engine/flowGroup",
                    paramMap), (JSONObject) JSON.toJSON(pageDesc)));
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getJSONArray("objList"/*, FlowInstanceGroup.class*/);
    }

    /**
     * 根据条件查询待办，包括flowInstId，flowOptTag
     *
     * @param paramMap 查询参数
     * @param pageDesc 分页信息
     * @return 获取待办列表 这里指静态代办
     */
    @Override
    public JSONArray listTasks(Map<String, Object> paramMap, PageDesc pageDesc) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl(
                UrlOptUtils.appendParamsToUrl("/flow/engine/listTasks",
                    paramMap), (JSONObject) JSON.toJSON(pageDesc)));
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getJSONArray("objList"/*, UserTask.class*/);
    }

    /**
     * 查看某一个节点所有的可以办理的用户
     *
     * @param nodeInstId 节点实例Id
     * @return 用户办件信息
     */
    @Override
    public JSONArray listNodeTaskUsers(String nodeInstId) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
                "/flow/engine/nodeTaskUsers?nodeInstId="+nodeInstId);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getJSONArray();
    }

    /**
     * 获取动态待办
     *
     * @param paramMap 包含nodeInstId，unitCode，userStation
     * @param pageDesc     分页信息
     * @return
     */
    @Override
    public JSONArray listDynamicTask(Map<String, Object> paramMap, PageDesc pageDesc) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl(
                UrlOptUtils.appendParamsToUrl("/flow/engine/listDynamicTasks",
                    paramMap), (JSONObject) JSON.toJSON(pageDesc)));
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getJSONArray("objList"/*, UserTask.class*/);
    }

    /**
     * @param flowInstId   流程实例号
     * @param itemRoleCode 办件角色代码
     * @return 用户范围
     */
    @Override
    public JSONArray listItemRoleFilter(String flowInstId, String itemRoleCode) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/engine/itemRoleFilter/"+flowInstId+"/"+itemRoleCode);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getJSONArray();
    }

}
