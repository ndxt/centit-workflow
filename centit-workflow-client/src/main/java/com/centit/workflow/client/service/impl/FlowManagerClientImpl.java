package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.UrlOptUtils;
import com.centit.workflow.client.service.FlowManagerClient;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
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
public class FlowManagerClientImpl implements FlowManagerClient {

    public FlowManagerClientImpl() {

    }

    private WorkflowAppSession appSession;

    @Autowired
    public void setAppSession(WorkflowAppSession appSession) {
        this.appSession = appSession;
    }

    @Override
    public JSONArray listFlowInstNodes(String wfinstid){
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",String.valueOf(wfinstid));

        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/listFlowInstNodes",
            paramMap);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getJSONArray();
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
    public int suspendNodeInstance(String nodeInstId, String mangerUserCode) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("admin",mangerUserCode);
        RestfulHttpRequest.getResponseData(appSession,
            "/flow/manager/suspendNodeInst/"+nodeInstId,paramMap);
        return 0;
    }

    /**
     * 使流程的 挂起和失效的节点 正常运行 N 正常
     *
     * @param nodeInstId     节点实例id
     * @param mangerUserCode 管理人员代码
     * @return 状态码
     */
    @Override
    public int activizeNodeInstance(String nodeInstId, String mangerUserCode) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("admin",mangerUserCode);
        RestfulHttpRequest.jsonPut(appSession,
            "/flow/manager/activizeNodeInst/"+nodeInstId,paramMap);
        return 0;
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
    public List<OperationLog> listUserActionLogs(String userCode, PageDesc pageDesc, Date lastTime) {
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

}
