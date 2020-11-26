package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.UrlOptUtils;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017/7/28.
 */
@Service
public class FlowManagerClientImpl implements FlowManager {

    public FlowManagerClientImpl() {

    }

    private WorkflowAppSession appSession;

    @Autowired
    public void setAppSession(WorkflowAppSession appSession) {
        this.appSession = appSession;
    }

    @Override
    public List<NodeInstance> listFlowInstNodes(String wfinstid){
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",String.valueOf(wfinstid));

        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/listFlowInstNodes",
            paramMap);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getDataAsArray(NodeInstance.class);
    }

    @Override
    public List<NodeInstance> listFlowActiveNodes(String flowInstId){
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",String.valueOf(flowInstId));

        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/engine/activeNodes",
            paramMap);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getDataAsArray(NodeInstance.class);
    }

    /**
     * 根据 示例ID获得实例
     *
     * @param flowInstId 流程 实例id
     * @return 流程实例信息
     */
    @Override
    public FlowInstance getFlowInstance(String flowInstId) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/"+flowInstId);
        return receiveJSON.getDataAsObject("flowInst", FlowInstance.class);
    }


    @Override
    public int stopInstance(String flowInstId, String mangerUserCode, String admindesc){
        /*String s = */RestfulHttpRequest.jsonPut(appSession,
            "/flow/manager/stopInstance/"+flowInstId+"/"+mangerUserCode,null);
        return 0;
    }

    /**
     * 暂停一个流程    P 暂停 挂起
     *
     * @param flowInstId     流程实例id
     * @param mangerUserCode 管理人员代码
     * @param admindesc      管理原因
     *                       throws Exception 异常
     * @return 状态码
     */
    @Override
    public int suspendInstance(String flowInstId, String mangerUserCode, String admindesc) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("admin",mangerUserCode);
        paramMap.put("stopDesc",admindesc);
        RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/suspendinst/"+flowInstId,paramMap);
        return 0;
    }

    /**
     * 激活一个 挂起的或者无效的流程  N 正常
     *
     * @param flowInstId     流程实例id
     * @param mangerUserCode 管理人员代码
     * @param admindesc      管理原因
     * @return 状态码
     */
    @Override
    public int activizeInstance(String flowInstId, String mangerUserCode, String admindesc) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("admin",mangerUserCode);
        paramMap.put("stopDesc",admindesc);
        RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/activizeinst/"+flowInstId,paramMap);
        return 0;
    }

    /**
     * 暂停流程的一个节点  P 暂停
     *
     * @param nodeInstId     节点实例id
     * @param mangerUserCode 管理人员代码
     * @return 状态码
     */
    @Override
    public long suspendNodeInstance(String nodeInstId, String mangerUserCode) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("admin",mangerUserCode);
        RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/suspendNodeInst/"+nodeInstId,paramMap);
        return 0l;
    }

    /**
     * 使流程的 挂起和失效的节点 正常运行 N 正常
     *
     * @param nodeInstId     节点实例id
     * @param mangerUserCode 管理人员代码
     * @return 状态码
     */
    @Override
    public long activizeNodeInstance(String nodeInstId, String mangerUserCode) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("admin",mangerUserCode);
        RestfulHttpRequest.jsonPut(appSession,
            "/flow/manager/activizeNodeInst/"+nodeInstId,paramMap);
        return 0l;
    }

    /**
     * 强制流转到下一结点，这个好像不好搞，主要是无法获得业务数据，只能提交没有分支的节点
     *
     * @param nodeInstId     节点实例id
     * @param mangerUserCode 管理人员代码
     * @return 状态码
     */
    @Override
    public String forceCommit(String nodeInstId, String mangerUserCode) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("admin",mangerUserCode);
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/forceCommit/"+nodeInstId,paramMap);
        return receiveJSON.getDataAsString();
    }

    /**
     * 从这个节点重新运行该流程，包括已经结束的流程
     *
     * @param nodeInstId     节点实例id
     * @param mangerUserCode 管理人员代码
     * @return 新的节点实例id
     */
    @Override
    public NodeInstance resetFlowToThisNode(String nodeInstId, String mangerUserCode) {
        String json =  RestfulHttpRequest.jsonPost(appSession,
            "/flow/manager/resetToCurrent/"+nodeInstId+"?userCode="+mangerUserCode, null);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(json);
        return receiveJSON.getDataAsObject(NodeInstance.class);
    }

    /**
     * 分配节点任务
     * Task_assigned 设置为 S 如果多于 一个人 放在 ActionTask 表中，并且把  Task_assigned 设置为 T
     *
     * @param nodeInstId 节点实例ID
     * @param userCode 操作用户
     * @param mangerUserCode 管理用户
     * @param authDesc 授权说明
     */
    @Override
    public int assignNodeTask(String nodeInstId, String userCode, String mangerUserCode, String authDesc) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("userCode",userCode);
        paramMap.put("authDesc",authDesc);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/manager/assign/"+nodeInstId+"/"+mangerUserCode, paramMap);
        return 0;
    }

    /**
     * 添加节点任务, 添加操作人元
     * Task_assigned 设置为 S 如果多于 一个人 放在 ActionTask 表中，并且把  Task_assigned 设置为 T
     *
     * @param nodeInstId
     * @param userCode
     * @param mangerUserCode
     * @param authDesc
     */
    @Override
    public int addNodeTask(String nodeInstId, String userCode, String mangerUserCode, String authDesc) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("userCode",userCode);
        paramMap.put("authDesc",authDesc);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/manager/addNodeTask/"+nodeInstId+"/"+mangerUserCode, paramMap);
        return 0;
    }

    /**
     * 删除节点任务
     *
     * @param nodeInstId 节点实例ID
     * @param userCode 操作用户
     * @param mangerUserCode 管理用户
     */
    @Override
    public int deleteNodeTask(String nodeInstId, String userCode, String mangerUserCode) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("userCode",userCode);
        RestfulHttpRequest.jsonPost(appSession,
            "/flow/manager/deleteNodeTask/"+nodeInstId+"/"+mangerUserCode, paramMap);
        return 0;
    }

    /**
     * 获取节点实例的操作日志列表
     *
     * @param flowInstId 流程实例号
     * @param nodeInstId 节点实例好
     * @return List<WfActionLog>
     */
    @Override
    public List<OperationLog> listNodeActionLogs(String flowInstId, String nodeInstId) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/nodelogs/"+flowInstId+"/"+nodeInstId);
        return receiveJSON.getDataAsArray(OperationLog.class);
    }

    /**
     * 获取节点实例的操作日志列表

     * @param flowInstId     流程实例号
     * @param withNodeAction 是否包括节点的日志
     * @return List<WfActionLog>
     */
    @Override
    public List<OperationLog> listFlowActionLogs(String flowInstId, boolean withNodeAction) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/flowlogs/"+flowInstId+"?withNodeAction="+withNodeAction);
        return receiveJSON.getDataAsArray(OperationLog.class);
    }

    /**
     * 获取用户所有的操作记录
     *
     * @param userCode
     * @param pageDesc 和分页机制结合
     * @param lastTime if null return all
     * @return
     */
    @Override
    public List<? extends OperationLog> listUserActionLogs(String userCode, Date lastTime, PageDesc pageDesc) {
        String url = UrlOptUtils.appendParamsToUrl("/flow/manager/userlogs/"+userCode,
            (JSONObject) JSON.toJSON(pageDesc));
        if(lastTime!=null){
            UrlOptUtils.appendParamToUrl(url,
                "lastTime="+ DatetimeOpt.convertDatetimeToString(lastTime));
        }
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession, url);
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getDataAsArray("objList", OperationLog.class);
    }

    /**
     * 获取系统中所有的流程实例
     *
     * @param filterMap 过滤条件
     * @param pageDesc  分页描述
     * @return
     */
    @Override
    public JSONArray listFlowInstance(Map<String, Object> filterMap, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查看工作流程实例状态或进度
     *
     * @param flowInstId 流程 实例id
     * @return XML 描述的流程流转状态图
     */
    @Override
    public Map<String, Object> viewFlowInstance(String flowInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查看工作流程节点示例图
     *
     * @param flowInstId 流程 实例id
     * @return 工作流程节点示例图
     */
    @Override
    public String viewFlowNodeInstance(String flowInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询某人暂停计时的流程
     *
     * @param userCode
     * @param pageDesc
     * @return
     */
    @Override
    public List<FlowInstance> listPauseTimerFlowInst(String userCode, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 暂停流程计时
     *
     * @param flowInstId
     * @param mangerUserCode
     */
    @Override
    public int suspendFlowInstTimer(String flowInstId, String mangerUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 唤醒流程计时
     *
     * @param flowInstId
     * @param mangerUserCode
     */
    @Override
    public int activizeFlowInstTimer(String flowInstId, String mangerUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 设置流程期限
     *
     * @param flowInstId     流程实例编号
     * @param timeLimit      新的流程期限 5D3h
     * @param mangerUserCode 管理人员代码
     * @param admindesc      流程期限更改原因说明
     * @return
     */
    @Override
    public long resetFlowTimelimt(String flowInstId, String timeLimit, String mangerUserCode, String admindesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 更改流程所属机构
     *
     * @param flowInstId  流程实例ID
     * @param unitCode    机构代码
     * @param optUserCode
     */
    @Override
    public void updateFlowInstUnit(String flowInstId, String unitCode, String optUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询某人暂定计时的节点
     *
     * @param userCode
     * @param pageDesc
     * @return
     */
    @Override
    public List<NodeInstance> listPauseTimerNodeInst(String userCode, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 暂停节点定时
     *
     * @param nodeInstId
     * @param mangerUserCode
     */
    @Override
    public int suspendNodeInstTimer(String nodeInstId, String mangerUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 唤醒节点定时
     *
     * @param nodeInstId
     * @param mangerUserCode
     */
    @Override
    public int activizeNodeInstTimer(String nodeInstId, String mangerUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 强制一个并行分支的节点为游离状态，在提交其他并行分支前调用
     *
     * @param nodeInstId
     * @param mangerUserCode
     */
    @Override
    public String forceDissociateRuning(String nodeInstId, String mangerUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 更改节点所属机构
     *
     * @param nodeInstId  节点实例ID
     * @param unitCode    机构代码
     * @param optUserCode
     */
    @Override
    public void updateNodeInstUnit(String nodeInstId, String unitCode, String optUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 更改节点的角色信息
     *
     * @param nodeInstId
     * @param roleType
     * @param roleCode
     * @param mangerUserCode
     */
    @Override
    public void updateNodeRoleInfo(String nodeInstId, String roleType, String roleCode, String mangerUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 设置流程期限
     *
     * @param nodeInstId     流程节点实例编号
     * @param timeLimit      新的流程期限 5D3h
     * @param mangerUserCode 管理人员代码
     * @return
     */
    @Override
    public long resetNodeTimelimt(String nodeInstId, String timeLimit, String mangerUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据节点ID查询能够操作该节点的所有人员，如果为空，则需要分配工作任务单
     * 这个是返回所有能够操作本节点的人员
     *
     * @param nodeInstId
     */
    @Override
    public List<UserTask> listNodeTasks(String nodeInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取节点实例的任务列表，这个返回在actionTask中手动分配的工作人员
     *
     * @param nodeInstId
     * @return List<ActionTask>
     */
    @Override
    public List<ActionTask> listNodeActionTasks(String nodeInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取节点所在阶段信息
     *
     * @param flowInstId
     * @return
     */
    @Override
    public List<StageInstance> listStageInstByFlowInstId(String flowInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 设置流程期限
     *
     * @param flowInstId     流程实例编号
     * @param stageId
     * @param timeLimit      新的流程期限 5D3h
     * @param mangerUserCode 管理人员代码
     * @param admindesc      流程期限更改原因说明
     * @return
     */
    @Override
    public long resetStageTimelimt(String flowInstId, String stageId, String timeLimit, String mangerUserCode, String admindesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public List<? extends OperationLog> listNodeActionLogs(String nodeInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取用户参与 流程实例 按照时间倒序排列
     *
     * @param userCode  用户代码
     * @param flowPhase
     * @param filterMap
     * @param pageDesc  分页描述
     * @return
     */
    @Override
    public List<FlowInstance> listUserAttachFlowInstance(String userCode, String flowPhase, Map<String, Object> filterMap, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查找所有没有操作用户的节点
     *
     * @return List<NodeInstance>
     */
    @Override
    public List<NodeInstance> listNodesWithoutOpt() {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * @param nodeInstId
     * @Author:chen_rj
     * @Description:获取节点任务
     * @Date:8:51 2017/7/14
     */
    @Override
    public List<ActionTask> listNodeInstTasks(String nodeInstId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 删除节点任务
     *
     * @param taskId
     * @param mangerUserCode
     */
    @Override
    public int deleteNodeTaskById(String taskId, String mangerUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 删除节点任务
     *
     * @param nodeInstId
     * @param flowInstId
     * @param mangerUserCode
     */
    @Override
    public void deleteNodeActionTasks(String nodeInstId, String flowInstId, String mangerUserCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * @param relegateno
     * @return
     */
    @Override
    public RoleRelegate getRoleRelegateById(Long relegateno) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * @param roleRelegate
     */
    @Override
    public void saveRoleRelegate(RoleRelegate roleRelegate) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询别人委托给我的
     *
     * @param userCode
     * @return
     */
    @Override
    public List<RoleRelegate> listRoleRelegateByUser(String userCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 查询我委托给别人的
     *
     * @param grantor
     * @return
     */
    @Override
    public List<RoleRelegate> listRoleRelegateByGrantor(String grantor) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * @param relegateno
     */
    @Override
    public void deleteRoleRelegate(String relegateno) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 将 fromUserCode 所有任务 迁移 给 toUserCode
     *
     * @param fromUserCode 任务属主
     * @param toUserCode   新的属主
     * @param optUserCode  操作人员
     * @param moveDesc     迁移描述
     * @return 返回迁移的任务数
     */
    @Override
    public int moveUserTaskTo(String fromUserCode, String toUserCode, String optUserCode, String moveDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 将 fromUserCode 所有任务 迁移 给 toUserCode
     *
     * @param nodeInstIds  任务节点结合
     * @param fromUserCode 任务属主
     * @param toUserCode   新的属主
     * @param optUserCode  操作人员
     * @param moveDesc     迁移描述
     * @return 返回迁移的任务数
     */
    @Override
    public int moveUserTaskTo(List<String> nodeInstIds, String fromUserCode, String toUserCode, String optUserCode, String moveDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public void updateFlow(FlowInstance flowInstance) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 流程拉回到首节点
     *
     * @param flowInstId
     * @param managerUserCode
     * @param force           是否强制，否的话 需要判断流程最后提交人是否是自己
     */
    @Override
    public NodeInstance reStartFlow(String flowInstId, String managerUserCode, Boolean force) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public Boolean changeRelegateValid(String json) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public List<JSONObject> getListRoleRelegateByGrantor(String grantor) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public void saveRoleRelegateList(RoleRelegate roleRelegate) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public RoleRelegate getRoleRelegateByPara(String json) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取所有流程分组
     *
     * @param filterMap
     * @param pageDesc
     * @return
     */
    @Override
    public JSONArray listFlowInstGroup(Map<String, Object> filterMap, PageDesc pageDesc) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据ID获得流程分组
     *
     * @param flowInstGroupId
     * @return
     */
    @Override
    public FlowInstanceGroup getFlowInstanceGroup(String flowInstGroupId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public void updateFlowInstOptInfoAndUser(String flowInstId, String flowOptName, String flowOptTag, String userCode, String unitCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }
}
