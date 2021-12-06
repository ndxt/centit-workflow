package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.UrlOptUtils;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowDefine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chen_rj on 2018-5-2.
 */
@Service
public class FlowDefineClientImpl implements FlowDefine {

    public FlowDefineClientImpl() {

    }

    private WorkflowAppSession appSession;

    @Autowired
    public void setAppSession(WorkflowAppSession appSession) {
        this.appSession = appSession;
    }

    @Override
    public List<FlowInfo> listLastVersionFlow(Map<String, Object> filterMap,
                                              PageDesc pageDesc) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl(
                UrlOptUtils.appendParamsToUrl("/flow/define/listFlow",
                filterMap), (JSONObject)JSON.toJSON(pageDesc)));
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getDataAsArray("objList",FlowInfo.class);
    }

    @Override
    public List<FlowInfo> listFlowsByOptId(String optId){
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
           "/flow/define/optFlow/"+optId);
        return receiveJSON.getDataAsArray(FlowInfo.class);
    }



    /**
     * 获取 流程信息
     *
     * @param flowCode 流程代码
     * @param version  流程版本
     * @return 流程信息
     */
    @Override
    public FlowInfo getFlowInfo(String flowCode, long version) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/define/"+String.valueOf(version)+"/"+flowCode);
        return receiveJSON.getDataAsObject(FlowInfo.class);
    }

    @Override
    public FlowInfo getFlowInfo(String flowCode) {
        return getFlowInfo(flowCode, -1);
    }
    /**
     * 根据流程编码获取 流程所有版本
     *
     * @param flowCode 流程代码
     * @return 流程所有版本
     */
    @Override
    public List<FlowInfo> listFlowsByCode(String flowCode, PageDesc pageDesc) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl("/flow/define/allversions/"+flowCode,
                (JSONObject)JSON.toJSON(pageDesc)));
        pageDesc.copy(receiveJSON.getDataAsObject("pageDesc", PageDesc.class));
        return receiveJSON.getDataAsArray("objList",FlowInfo.class);
    }

    /**
     * 获取流程所有办件角色
     *
     * @param flowCode 流程代码
     * @param version  流程版本
     * @return 办件角色代码 办件角色名称
     */
    @Override
    public Map<String, String> listFlowItemRoles(String flowCode, Long version) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/define/itemrole/"+flowCode +"/"+String.valueOf(version));
        return receiveJSON.getDataAsMap(String.class);
    }

    /**
     * 获取流程所有变量
     *
     * @param flowCode 流程代码
     * @param version  流程版本
     * @return 变量 代码 变量 名称
     */
    @Override
    public Map<String, String> listFlowVariableDefines(String flowCode, Long version) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/define/variable/"+flowCode +"/"+String.valueOf(version));
        return receiveJSON.getDataAsMap(String.class);
    }

    /**
     * 获取流程所有阶段
     *
     * @param flowCode 流程代码
     * @param version  流程版本
     * @return 办件角色代码 办件角色名称
     */
    @Override
    public Map<String, String> listFlowStages(String flowCode, Long version) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/define/stage/"+flowCode +"/"+String.valueOf(version));
        return receiveJSON.getDataAsMap(String.class);
    }

    /*
     * 列举 流程办件角色 用户范围
     * @param flowCode 流程代码
     * @param version 流程版本
     * @param itemRoleCode 办件角色代码
     * @return 返回可选用户范围

    @Override
    public JSONArray listItemRoleUserScope(String flowCode, Long version, String itemRoleCode) {
        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/define/itemRoleFilter/"+flowCode+"/"+version+"/"+itemRoleCode);
        return receiveJSON.getJSONArray();
    }
    */

    /**
     * 保存流程定义，内容为JS画的流程描述XML文件
     *
     * @param flowCode
     * @param flowDefXML
     */
    @Override
    public boolean saveDraftFlowDefJson(String flowCode, String flowDefXML) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取保存的流程定义文件,就是0版本的草稿
     *
     * @param flowCode
     */
    @Override
    public String getDraftFlowDefJson(String flowCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 发布流程，返回当前流程版本号
     *
     * @param flowCode
     */
    @Override
    public long publishFlowDef(String flowCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取指定版本流程定义描述文件
     *
     * @param flowCode
     * @param version
     */
    @Override
    public String getFlowDefXML(String flowCode, long version) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取最新版本的流程定义描述文件
     *
     * @param flowCode
     */
    @Override
    public String getFlowDefXML(String flowCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 禁用某个流程
     *
     * @param flowCode
     */
    @Override
    public void disableFlow(String flowCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 启用某个流程
     *
     * @param flowCode
     */
    @Override
    public void enableFlow(String flowCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 保存流程定义对象,(版本只能为0),并且只保存基本信息
     *
     * @param wfDef
     */
    @Override
    public boolean saveDraftFlowDef(FlowInfo wfDef) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 保存流程定义对象,(版本只能为0),并且只流程阶段
     *
     * @param wfDef
     */
    @Override
    public boolean saveDraftFlowStage(FlowInfo wfDef) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 保存流程定义对象,(版本只能为0),并且只流程角色
     *
     * @param wfDef
     */
    @Override
    public boolean saveDraftFlowRole(FlowInfo wfDef) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 保存流程定义对象,(版本只能为0),并且只流程变量
     *
     * @param wfDef
     */
    @Override
    public boolean saveDraftFlowVariableDef(FlowInfo wfDef) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据节点ID获得节点定义
     *
     * @param nodeId
     * @return
     */
    @Override
    public NodeInfo getNodeInfoById(String nodeId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取某一流程某一版本号中存在的所有机构表达式
     *
     * @param flowCode 流程代码
     * @param version  版本
     * @return 机构表达式列表
     */
    @Override
    public Set<String> getUnitExp(String flowCode, Long version) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据流程code删除相关的流程定义，用于删除多余的测试数据
     *
     * @param flowCode 流程代码
     */
    @Override
    public void deleteFlowDef(String flowCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 例举所有节点操作类别
     */
    @Override
    public Map<String, String> listAllOptType() {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 例举所有节点类别
     */
    @Override
    public Map<String, String> listAllNoteType() {
        throw new ObjectException("This function is not been implemented in client.");
    }


    /**
     * 列举所有角色类别
     */
    @Override
    public Map<String, String> listRoleType() {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 列举所有角色
     */
    @Override
    public Map<String, Map<String, String>> listAllRole() {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * @param stype 角色类别
     * @return 角色名称和类别对应列表
     */
    @Override
    public Map<String, String> listRoleByType(String stype) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * @return 内置的流程结构表达式
     */
    @Override
    public Map<String, String> listInsideUnitExp() {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 列举所有的子流程
     */
    @Override
    public Map<String, String> listAllSubFlow() {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取流程最新的版本好
     *
     * @param flowCode 流程代码
     * @return 流程已发布的最新版本号
     */
    @Override
    public Long getFlowLastVersion(String flowCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取某一流程某一版本号中存在的所有办件角色
     *
     * @param flowCode 流程代码
     * @param version  版本 0 草稿 -1 最新版本
     * @param roleCode 办件角色代码
     * @return 流程的办件角色
     */
    @Override
    public OptTeamRole getFlowItemRole(String flowCode, Long version, String roleCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据流程代码获取流程变量的默认值
     *
     * @param flowCode 流程代码
     * @param version  版本 0 草稿 -1 最新版本
     * @return 流程变量信息
     */
    @Override
    public Map<String, String> listFlowDefaultVariables(String flowCode, Long version) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取流程业务中存在的所有办件角色
     * @param optId 流程业务id
     * @return 流程的办件角色列表
     */
    @Override
    public Map<String, String> listOptItemRoles(String optId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 获取流程业务中的某个办件角色
     * @param optId 流程业务id
     * @param roleCode 办件角色代码
     * @return 流程的办件角色
     */
    @Override
    public OptTeamRole getOptItemRole(String optId, String roleCode) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据流程业务id获取业务变量信息
     * @param optId 流程业务id
     * @return
     */
    @Override
    public Map<String, String> listOptVariableDefines(String optId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据流程业务id获取业务变量默认值
     * @param optId 流程业务id
     * @return
     */
    @Override
    public Map<String, String> listOptDefaultVariables(String optId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 根据流程阶段id删除流程阶段
     * @param stageId
     */
    @Override
    public void deleteFlowStageById(String stageId) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    /**
     * 保存流程阶段
     * @param flowStage
     */
    @Override
    public void saveFlowStage(FlowStage flowStage) {
        throw new ObjectException("This function is not been implemented in client.");
    }

    @Override
    public int[] batchUpdateOptId(String optId, List<String> flowCodes) {
        return new int[0];
    }

    @Override
    public String copyWorkFlow(Map<String, Object> parameters) {
        throw new ObjectException("This function is not been implemented in client.");
    }
}
