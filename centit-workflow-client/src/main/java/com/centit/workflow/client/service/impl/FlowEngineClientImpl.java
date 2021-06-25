package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.UrlOptUtils;
import com.centit.workflow.commons.CreateFlowOptions;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.*;

/**
 * Created by chen_rj on 2017/7/28.
 */
@Service
public class FlowEngineClientImpl implements FlowEngine {

    public FlowEngineClientImpl() {
    }

    private WorkflowAppSession appSession;

    @Autowired
    public void setAppSession(WorkflowAppSession appSession) {
        this.appSession = appSession;
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
    public void saveFlowVariable(String flowInstId, String varName, Object varValue)  {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("varName", varName);
        paramMap.put("varValue", varValue);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/saveFlowVariable", paramMap);
    }


    @Override
    public void saveFlowNodeVariable(String nodeInstId, String varName, Object varValue)  {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("varName", varName);
        paramMap.put("varValue", varValue);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/saveFlowNodeVariable", paramMap);
    }

    /**
     * 设置流程节点上下文变量
     *
     * @param flowInstId 工作流实例号
     * @param runToken   令牌值
     * @param sVar       变量名
     * @param sValue     变量值
     */
    @Override
    public void saveFlowNodeVariable(String flowInstId, String runToken, String sVar, Object sValue) {
        throw new ObjectException("This function is not been implemented in client.");
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

    /**
     * 分配工作小组 --办件角色
     *
     * @param nodeInstId  节点实例号 不能为空
     * @param roleCode    办件角色 不能为空
     * @param userCodeSet 用户代码列表，添加
     */
    @Override
    public void assignFlowWorkTeamByNode(String nodeInstId, String roleCode, List<String> userCodeSet) {
        throw new ObjectException("This function is not been implemented in client.");
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
    public void assignFlowOrganize(String flowInstId, String roleCode,
                                String unitCode) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("unitCode", unitCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/addFlowOrganize", paramMap);
    }

    @Override
    public List<String> submitOpt(SubmitOptOptions options)  {
        String returnJson = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/submitOpt", options);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(returnJson);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        // 返回的是下一节点的节点实例id
        JSONArray nextNodeInsts = receiveJSON.getJSONObject().getJSONArray("nextNodeInsts");
        if (!nextNodeInsts.isEmpty()) {
            return nextNodeInsts.toJavaList(String.class);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取流程实例信息
     *
     * @param flowInstId 实例id
     * @return 实例信息
     */
    @Override
    public FlowInstance getFlowInstById(String flowInstId) {
        return RestfulHttpRequest.getResponseObject(appSession,
            "/flow/engine/inst/"+flowInstId, FlowInstance.class);
    }

    /**
     * 获取流程定义信息
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
    public NodeInstance getNodeInstById(String nodeInstId) {
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

    //rollBackNode
    @Override
    public String rollBackNode(String nodeInstId, String managerUserCode) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nodeInstId", nodeInstId);
        paramMap.put("managerUserCode", managerUserCode);
        String res = RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/rollBackNode", paramMap);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(res);
        return receiveJSON.getDataAsString();
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
    public List<FlowInstanceGroup> listFlowInstGroup(Map<String, Object> paramMap, PageDesc pageDesc) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl(
                UrlOptUtils.appendParamsToUrl("/flow/engine/flowGroup",
                    paramMap), (JSONObject) JSON.toJSON(pageDesc)));
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getDataAsArray("objList", FlowInstanceGroup.class);
    }

    /**
     * 获取流程实例的业务节点信息
     * @param flowInstId
     * @return
     */
    @Override
    public JSONArray viewFlowNodes(String flowInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public List<Map<String, Object>> listNodeTasks(List<String> nextNodeInstList) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public Map<String, Object> getNodeTasks(String nodeInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据条件查询待办，包括flowInstId，flowOptTag
     *
     * @param paramMap 查询参数
     * @param pageDesc 分页信息
     * @return 获取待办列表 这里指静态代办
     */
    @Override
    public List<UserTask> listTasks(Map<String, Object> paramMap, PageDesc pageDesc) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl(
                UrlOptUtils.appendParamsToUrl("/flow/engine/listTasks",
                    paramMap), (JSONObject) JSON.toJSON(pageDesc)));
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getDataAsArray("objList", UserTask.class);
    }

    /**
     * 查看某一个节点所有的可以办理的用户
     *
     * @param nodeInstId 节点实例Id
     * @return 用户办件信息
     */
    @Override
    public List<UserTask> listNodeOperator(String nodeInstId) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
                "/flow/engine/nodeTaskUsers?nodeInstId="+nodeInstId);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getDataAsArray(UserTask.class);
    }

    /**
     * 获取动态待办(查询用户岗位待办)
     *
     * @param paramMap 包含nodeInstId，unitCode，userStation
     * @param pageDesc     分页信息
     * @return
     */
    @Override
    public List<UserTask> listDynamicTask(Map<String, Object> paramMap, PageDesc pageDesc) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl(
                UrlOptUtils.appendParamsToUrl("/flow/engine/listDynamicTasks",
                    paramMap), (JSONObject) JSON.toJSON(pageDesc)));
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getDataAsArray("objList", UserTask.class);
    }

    /**
     * 创建流程实例或子流程实例
     *
     * @param options     NewFlowInstanceOptions 流程创建选项编码
     * @param varTrans    UserUnitVariableTranslate 机构执行环境
     * @param application spring上下文环境。作为独立服务后这个应该不需要了
     * @return FlowInstance
     */
    @Override
    public FlowInstance createInstance(CreateFlowOptions options, UserUnitVariableTranslate varTrans, ServletContext application) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 返回下一步节点的节点实例ID
     *
     * @param options     SubmitOptOptions 提交流程操作选项编码
     * @param varTrans    UserUnitVariableTranslate 机构执行环境
     * @param application
     * @return 节点实例编号列表
     */
    @Override
    public List<String> submitOpt(SubmitOptOptions options, UserUnitVariableTranslate varTrans, ServletContext application)  {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 预判下一步节点的节点编号
     * @param options
     * @return 节点信息列表
     */
    @Override
    public Set<NodeInfo> viewNextNode(SubmitOptOptions options) {
//        throw new ObjectException("This function is not been implemented in client.");
        String json =  RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/viewNextNode", options);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(json);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return new HashSet<>(receiveJSON.getDataAsArray(NodeInfo.class));
    }

    /**
     * 查看下一节点可以操作的人员类表
     *
     * @param nextNodeId    下一个节点编号
     * @param options
     * @return 用户代码
     */
    @Override
    public Set<String> viewNextNodeOperator(String nextNodeId, SubmitOptOptions options) {
//        throw new ObjectException("This function is not been implemented in client.");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nextNodeId", nextNodeId);
        paramMap.put("options", options);
        String json =  RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/viewNextNodeOperator", paramMap);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(json);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return new HashSet<>(receiveJSON.getDataAsArray(String.class));
    }

    /**
     * 查看某一个用户所有的待办，并且分页
     *
     * @param userCode 操作用户编号
     * @param pageDesc 分页信息
     * @return 用户任务列表
     */
    @Override
    public List<UserTask> listUserTasks(String userCode, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 这个查看某个用户对用特定流程的待办
     *
     * @param filterMap 过滤条件，按道理 必须包括一个 userCode 条件
     * @param pageDesc  分页信息
     * @return 用户任务列表
     */
    @Override
    public List<UserTask> listUserTasksByFilter(Map<String, Object> filterMap, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 这个查看某个用户对用特定流程的待办
     *
     * @param userCode 用户代码
     * @param flowCode 流程代码
     * @param pageDesc 分页信息
     * @return 用户任务列表
     */
    @Override
    public List<UserTask> listUserTasksByFlowCode(String userCode, String flowCode, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查看某一个用户对应某一个阶段的待办
     *
     * @param userCode  用户代码
     * @param flowStage 流程阶段
     * @param pageDesc  分页信息
     * @return 用户任务列表
     */
    @Override
    public List<UserTask> listUserTasksByFlowStage(String userCode, String flowStage, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询某个用户的对应某一个节点的待办，这个节点可以是多个流程中的节点，只要这些节点的nodecode一致
     *
     * @param userCode 用户代码
     * @param nodeCode 节点代码
     * @param pageDesc 分页信息
     * @return 用户任务列表
     */
    @Override
    public List<UserTask> listUserTasksByNodeCode(String userCode, String nodeCode, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取动态待办
     *
     * @param searchColumn 包含nodeInstId，unitCode，userStation
     * @param pageDesc     分页信息
     * @return 获取待办列表 这里指动态代办
     */
    @Override
    public List<UserTask> listDynamicTaskByUnitStation(Map<String, Object> searchColumn, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查看某一个用户所有的已办，并且分页
     *
     * @param filterMap 过滤条件
     * @param pageDesc  分页信息
     * @return 用户任务列表
     */
    @Override
    public List<UserTask> listUserCompleteTasks(Map<String, Object> filterMap, PageDesc pageDesc) {
//        throw new ObjectException("This function is not been implemented in client.");
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl(
                UrlOptUtils.appendParamsToUrl("/flow/engine/listCompleteTasks",
                    filterMap), (JSONObject) JSON.toJSON(pageDesc)));
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getDataAsArray("objList", UserTask.class);
    }

    /**
     * 判断一个用户是否可以处理指定的节点,可以喝submitOpt结合使用，
     * 判断当前操作人员是否可以访问提交后的下一个节点。
     *
     * @param nodeInstId 节点实例代码
     * @param userCode   用户代码
     * @return 是否有权限
     */
    @Override
    public boolean canAccess(String nodeInstId, String userCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取任务授权人，如果是用户自己的任务，返回自己，否则返回授权人
     *
     * @param nodeInstId 节点实例id
     * @param userCode   用户代码
     * @return 授权人
     */
    @Override
    public String getTaskGrantor(String nodeInstId, String userCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 自定义预警查询
     *
     * @param filterMap 过滤条件
     * @param pageDesc  分页信息
     * @return 预警列表
     */
    @Override
    public List<FlowWarning> listFlowWarning(Map<String, Object> filterMap, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询某个流程的预警
     *
     * @param flowInstId 流程实例代码
     * @param pageDesc   分页信息
     * @return 预警列表
     */
    @Override
    public List<FlowWarning> listFlowWarningByInst(String flowInstId, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询某个节点的预警
     *
     * @param nodeInstId 节点实例代码
     * @param pageDesc   分页信息
     * @return 预警列表
     */
    @Override
    public List<FlowWarning> listFlowWarningByNodeInst(String nodeInstId, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询某一个类别的预警
     *
     * @param warningCode 预警类别
     * @param pageDesc    分页信息
     * @return 预警列表
     */
    @Override
    public List<FlowWarning> listFlowWarningByWarningCode(String warningCode, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }


    /**
     * 更改流程的父节点，这个函数只是用来手动的将一个流程作为子流程挂到父流程的节点上，一般不会使用。
     *
     * @param flowInstId       子流程实例id
     * @param parentFlowInstId 父流程实例id
     * @param parentNodeInstId 父流程节点实例id
     */
    @Override
    public void updateFlowInstParentNode(String flowInstId, String parentFlowInstId, String parentNodeInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 关闭本节点分支以外的其他分支的所有节点,特指和本节点平行的分支，就是同一个父类令牌的分支
     *
     * @param nodeInstId  当前活动节点
     * @param optUserCode 操作人员
     */
    @Override
    public void disableOtherBranchNodes(String nodeInstId, String optUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 分配工作小组 --办件角色
     *
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode    办件角色 不能为空
     * @param runToken    令牌
     * @param userCodeSet 用户代码列表，添加
     */
    @Override
    public void assignFlowWorkTeam(String flowInstId, String roleCode, String runToken, List<String> userCodeSet) {
        throw new ObjectException("This function is not been implemented in client.");
          /*HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId", flowInstId);
        paramMap.put("roleCode", roleCode);
        paramMap.put("userCode", userCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/engine/addFlowWorkTeam", paramMap);*/
    }

    /**
     * 删除工作小组--办件角色
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   办件角色 不能为空
     * @param userCode   用户代码，添加
     */
    @Override
    public void deleteFlowWorkTeam(String flowInstId, String roleCode, String userCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查看工作小组
     *
     * @param flowInstId 流程实例号 不能为空
     * @return Map roleCode,Set userCode
     */
    @Override
    public Map<String, List<String>> viewFlowWorkTeam(String flowInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查看工作小组中某个角色的成员
     *
     * @param flowInstId 工作流实例号
     * @param roleCode   角色代码
     * @return 流程工作组
     */
    @Override
    public List<FlowWorkTeam> viewFlowWorkTeamList(String flowInstId, String roleCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查看工作小组中某个角色的成员,并且通过制定的授权说明过滤
     *
     * @param flowInstId 工作流实例号
     * @param roleCode   角色代码
     * @param authdesc   角色描述
     * @return 流程工作组
     */
    @Override
    public List<FlowWorkTeam> viewFlowWorkTeamList(String flowInstId, String roleCode, String authdesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 分配流程组织机构
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   机构角色 不能为空
     * @param unitCode   机构代码，添加
     * @param authdesc
     */
    @Override
    public void assignFlowOrganize(String flowInstId, String roleCode, String unitCode, String authdesc) {
        throw new ObjectException("This function is not been implemented in client.");
   }

    /**
     * 分配工作小组 --办件角色
     *
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode    机构角色 不能为空
     * @param unitCodeSet 机构代码列表，添加
     * @param authdesc
     */
    @Override
    public void assignFlowOrganize(String flowInstId, String roleCode, List<String> unitCodeSet, String authdesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 删除工作小组--办件角色
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   机构角色 不能为空
     * @param unitCode   机构代码，添加
     */
    @Override
    public void deleteFlowOrganize(String flowInstId, String roleCode, String unitCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 删除工作小组--办件角色
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   机构角色 不能为空
     * @param authDesc
     */
    @Override
    public void deleteFlowOrganizeByAuth(String flowInstId, String roleCode, String authDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查看工作小组
     *
     * @param flowInstId 流程实例号 不能为空
     * @return Map roleCode,Set unitCode
     */
    @Override
    public Map<String, List<String>> viewFlowOrganize(String flowInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * @param flowInstId 工作流实例号
     * @param roleCode   机构角色代码
     * @return 流程组织架构
     */
    @Override
    public List<FlowOrganize> viewFlowOrganizeList(String flowInstId, String roleCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * @param flowInstId 工作流实例号
     * @param roleCode   机构角色代码
     * @param authDesc   授权信息
     * @return 流程组织架构
     */
    @Override
    public List<FlowOrganize> viewFlowOrganizeList(String flowInstId, String roleCode, String authDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询流程变量
     *
     * @param flowInstId 工作流实例号
     * @return 所有流程变量
     */
    @Override
    public List<FlowVariable> listFlowVariables(String flowInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询某个流程节点的变量
     *
     * @param flowInstId 工作流实例号
     * @param runToken   令牌
     * @param varname    变量名
     * @return 流程变量
     */
    @Override
    public FlowVariable viewNodeVariable(String flowInstId, String runToken, String varname) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 设置流程关注人员
     *
     * @param attObj 流程实例id
     *               attUser 关注人员
     *               optUser 设置人员
     */
    @Override
    public void saveFlowAttention(InstAttention attObj) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 删除流程关注人员
     *
     * @param flowInstId 工作流实例号
     * @param attUser    关注人员
     */
    @Override
    public void deleteFlowAttention(String flowInstId, String attUser) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 删除流程关注人员
     *
     * @param flowInstId 工作流实例号
     * @param optUser    关注设置人员
     */
    @Override
    public void deleteFlowAttentionByOptUser(String flowInstId, String optUser) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 删除流程所有关注人员
     *
     * @param flowInstId 工作流实例号
     */
    @Override
    public void deleteFlowAttention(String flowInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取流程关注人员
     *
     * @param flowInstId 工作流实例号
     * @return 关注信息列表
     */
    @Override
    public List<InstAttention> viewFlowAttention(String flowInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * @param flowInstId 工作流实例号
     * @param userCode   关注人员
     * @return 关注信息
     */
    @Override
    public InstAttention getFlowAttention(String flowInstId, String userCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 返回所有关在的项目
     *
     * @param userCode  关注人
     * @param instState N 正常  C 完成   P 暂停 挂起     F 强行结束  A 所有
     * @return 流程实例信息列表
     */
    @Override
    public List<FlowInstance> viewAttentionFLowInstance(String userCode, String instState) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 返回所有关在的项目
     *
     * @param optName
     * @param userCode  关注人
     * @param instState N 正常  C 完成   P 暂停 挂起     F 强行结束  A 所有
     * @return 流程实例信息列表
     */
    @Override
    public List<FlowInstance> viewAttentionFLowInstanceByOptName(String optName, String userCode, String instState) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取用户操作节点的Url，if ! canAccess rteurn null
     *
     * @param nodeInstId 节点实例代码
     * @param userCode   用户代码
     * @return optUrl
     */
    @Override
    public String getNodeOptUrl(String nodeInstId, String userCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据节点实例号 获得节点实例
     *
     * @param flowInstId 流程实例id
     * @param nodeCode   节点代码
     * @return 节点信息列表
     */
    @Override
    public List<NodeInstance> listNodeInstsByNodecode(String flowInstId, String nodeCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取节点所在阶段信息
     *
     * @param nodeInstId 节点实例id
     * @return 阶段信息
     */
    @Override
    public StageInstance getStageInstByNodeInstId(String nodeInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }
}
