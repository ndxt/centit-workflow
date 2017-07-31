package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.core.common.ResponseJSON;
import com.centit.framework.core.dao.PageDesc;
import com.centit.support.network.HttpExecutor;
import com.centit.workflow.client.po.*;
import com.centit.workflow.client.service.FlowEngine;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chen_rj on 2017/7/28.
 */
public class FlowEngineImpl implements FlowEngine {
    public FlowEngineImpl() {

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

    @Override
    public FlowInstance createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode) throws Exception{
        FlowInstance flowInstance = new FlowInstance();
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowCode("000057");
        flowInstance.setFlowInfo(flowInfo);
        flowInstance.setFlowOptName(flowOptName);
        flowInstance.setFlowOptTag(flowOptTag);
        flowInstance.setUserCode(userCode);
        flowInstance.setUnitCode(unitCode);

        CloseableHttpClient httpClient = appSession.getHttpClient();
        appSession.checkAccessToken(httpClient);
        String jsonStr = HttpExecutor.jsonPost(httpClient,
                appSession.completeQueryUrl("/flow/engine/createFlowInstDefault"), flowInstance);
        return JSON.parseObject(jsonStr,FlowInstance.class);
    }

    @Override
    public FlowInstance createInstance(String flowCode, long version, String flowOptName, String flowOptTag, String userCode, String unitCode) {
        return null;
    }

    @Override
    public FlowInstance createInstance(String flowCode, long version, String flowOptName, String flowOptTag, String userCode, String unitCode, Map<String, Object> varTrans, ServletContext application) {
        return null;
    }

    @Override
    public FlowInstance createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode, Map<String, Object> varTrans, ServletContext application) {
        return null;
    }

    @Override
    public FlowInstance createInstanceLockFirstNode(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode) {
        return null;
    }

    @Override
    public FlowInstance createInstanceLockFirstNode(String flowCode, long version, String flowOptName, String flowOptTag, String userCode, String unitCode) {
        return null;
    }

    @Override
    public Set<Long> submitOpt(long nodeInstId, String userCode, String unitCode, Map<String, Object> varTrans, ServletContext application)  {
        return null;
    }

    @Override
    public Set<Long> submitOpt(long nodeInstId, String userCode, String grantorCode, String unitCode, Map<String, Object> varTrans, ServletContext application) {
        return null;
    }

    @Override
    public Set<Long> submitOptWithAssignUnitAndUser(long nodeInstId, String userCode, String unitCode, Map<String, Object> varTrans, Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers, ServletContext application)  {
        return null;
    }

    @Override
    public Set<Long> submitOptWithAssignUnitAndUser(long nodeInstId, String userCode, String grantorCode, String unitCode, Map<String, Object> varTrans, Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers, ServletContext application)  {
        return null;
    }

    @Override
    public Set<NodeInfo> viewNextNode(long nodeInstId, String userCode, String unitCode, Map<String, Object> varTrans) {
        return null;
    }

    @Override
    public Set<String> viewNextNodeOperator(long nextNodeId, long curNodeInstId, String userCode, String unitCode, Map<String, Object> varTrans) {
        return null;
    }

    @Override
    public List<UserTask> listUserTasks(String userCode, PageDesc pageDesc) {
        return null;
    }

    @Override
    public List<UserTask> listUserTasksByFlowCode(String userCode, String flowCode, PageDesc pageDesc) {
        return null;
    }

    @Override
    public List<UserTask> listUserTasksByFlowStage(String userCode, String flowStage, PageDesc pageDesc) {
        return null;
    }

    @Override
    public List<UserTask> listUserTasksByNodeCode(String userCode, String nodeCode, PageDesc pageDesc) {
        return null;
    }

    @Override
    public List<UserTask> listUserCompleteTasks(Map<String, Object> filterMap, PageDesc pageDesc) {
        return null;
    }

    @Override
    public boolean canAccess(long nodeInstId, String userCode) {
        return false;
    }

    @Override
    public String getTaskGrantor(long nodeInstId, String userCode) {
        return null;
    }

    @Override
    public List<FlowWarning> listFlowWarning(Map<String, Object> filterMap, PageDesc pageDesc) {
        return null;
    }

    @Override
    public List<FlowWarning> listFlowWarningByInst(Long flowInstId, PageDesc pageDesc) {
        return null;
    }

    @Override
    public List<FlowWarning> listFlowWarningByNodeInst(Long nodeInstId, PageDesc pageDesc) {
        return null;
    }

    @Override
    public List<FlowWarning> listFlowWarningByWarningCode(String warningCode, PageDesc pageDesc) {
        return null;
    }

    @Override
    public void updateFlowInstOptInfo(long flowInstId, String flowOptName, String flowOptTag) {

    }

    @Override
    public void recordActionLog(long nodeInstId, String userCode, String actionType) {

    }

    @Override
    public long rollbackOpt(long nodeInstId, String mangerUserCode) {
        return 0;
    }

    @Override
    public boolean nodeCanBeReclaim(long nodeInstId) {
        return false;
    }

    @Override
    public NodeInstance createPrepNodeInstLockUser(long flowInstId, long curNodeInstId, long nodeId, String createUser, String userCode, String unitCode) {
        return null;
    }

    @Override
    public NodeInstance createPrepNodeInstLockUser(long flowInstId, long curNodeInstId, String nodeCode, String createUser, String userCode, String unitCode) {
        return null;
    }

    @Override
    public NodeInstance createPrepNodeInst(long flowInstId, long curNodeInstId, long nodeId, String userCode, String unitCode) {
        return null;
    }

    @Override
    public NodeInstance createPrepNodeInst(long flowInstId, long curNodeInstId, String nodeCode, String userCode, String unitCode) {
        return null;
    }

    @Override
    public NodeInstance createIsolatedNodeInst(long flowInstId, long curNodeInstId, String nodeCode, String createUser, String userCode, String unitCode) {
        return null;
    }

    @Override
    public NodeInstance createIsolatedNodeInst(long flowInstId, long curNodeInstId, long nodeId, String createUser, String userCode, String unitCode) {
        return null;
    }

    @Override
    public void updateFlowInstParentNode(long flowInstId, long parentFlowInstId, long parentNodeInstId) {

    }

    @Override
    public void disableOtherBranchNodes(long nodeInstId, String optUserCode) {

    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, String userCode) {

    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, List<String> userCodeSet) {

    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, String userCode, String authdesc) {

    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, List<String> userCodeSet, String authdesc) {

    }

    @Override
    public void deleteFlowWorkTeam(long flowInstId, String roleCode, String userCode) {

    }

    @Override
    public void deleteFlowWorkTeam(long flowInstId, String roleCode) {

    }

    @Override
    public Map<String, List<String>> viewFlowWorkTeam(long flowInstId) {
        return null;
    }

    @Override
    public List<String> viewFlowWorkTeam(long flowInstId, String roleCode) {
        return null;
    }

    @Override
    public List<FlowWorkTeam> viewFlowWorkTeamList(long flowInstId, String roleCode) {
        return null;
    }

    @Override
    public List<FlowWorkTeam> viewFlowWorkTeamList(long flowInstId, String roleCode, String authdesc) {
        return null;
    }

    @Override
    public long assignNodeTask(long nodeInstId, String userCode, String mangerUserCode, Date expiretime, String authDesc) {
        return 0;
    }

    @Override
    public int disableNodeTask(long nodeInstId, String userCode, String mangerUserCode) {
        return 0;
    }

    @Override
    public int deleteNodeTask(long nodeInstId, String userCode, String mangerUserCode) {
        return 0;
    }

    @Override
    public int deleteNodeAllTask(long nodeInstId, String mangerUserCode) {
        return 0;
    }

    @Override
    public void assignFlowOrganize(long flowInstId, String roleCode, String unitCode) {

    }

    @Override
    public void assignFlowOrganize(long flowInstId, String roleCode, List<String> unitCodeSet) {

    }

    @Override
    public void assignFlowOrganize(long flowInstId, String roleCode, String unitCode, String authdesc) {

    }

    @Override
    public void assignFlowOrganize(long flowInstId, String roleCode, List<String> unitCodeSet, String authdesc) {

    }

    @Override
    public void deleteFlowOrganize(long flowInstId, String roleCode, String unitCode) {

    }

    @Override
    public void deleteFlowOrganize(long flowInstId, String roleCode) {

    }

    @Override
    public void deleteFlowOrganizeByAuth(long flowInstId, String roleCode, String authDesc) {

    }

    @Override
    public Map<String, List<String>> viewFlowOrganize(long flowInstId) {
        return null;
    }

    @Override
    public List<String> viewFlowOrganize(long flowInstId, String roleCode) {
        return null;
    }

    @Override
    public List<FlowOrganize> viewFlowOrganizeList(long flowInstId, String roleCode) {
        return null;
    }

    @Override
    public List<FlowOrganize> viewFlowOrganizeList(long flowInstId, String roleCode, String authDesc) {
        return null;
    }

    @Override
    public void saveFlowVariable(long flowInstId, String sVar, String sValue) {

    }

    @Override
    public void saveFlowNodeVariable(long nodeInstId, String sVar, String sValue) {

    }

    @Override
    public void saveFlowNodeVariable(long flowInstId, String runToken, String sVar, String sValue) {

    }

    @Override
    public void saveFlowVariable(long flowInstId, String sVar, Set<String> sValues) {

    }

    @Override
    public void saveFlowNodeVariable(long nodeInstId, String sVar, Set<String> sValues) {

    }

    @Override
    public List<FlowVariable> listFlowVariables(long flowInstId) {
        return null;
    }

    @Override
    public List<FlowVariable> viewFlowVariablesByVarname(long flowInstId, String varname) {
        return null;
    }

    @Override
    public FlowVariable viewNodeVariable(long flowInstId, String runToken, String varname) {
        return null;
    }

    @Override
    public void saveFlowAttention(long flowInstId, String attUser, String optUser) {

    }

    @Override
    public void deleteFlowAttention(long flowInstId, String attUser) {

    }

    @Override
    public void deleteFlowAttentionByOptUser(long flowInstId, String optUser) {

    }

    @Override
    public void deleteFlowAttention(long flowInstId) {

    }

    @Override
    public List<InstAttention> viewFlowAttention(long flowInstId) {
        return null;
    }

    @Override
    public InstAttention getFlowAttention(long flowInstId, String userCode) {
        return null;
    }

    @Override
    public List<FlowInstance> viewAttentionFLowInstance(String userCode, String instState) {
        return null;
    }

    @Override
    public FlowInstance getFlowInstById(long flowInstId) {
        return null;
    }

    @Override
    public NodeInstance getNodeInstById(long nodeInstId) {
        return null;
    }

    @Override
    public String getNodeOptUrl(long nodeInstId, String userCode) {
        return null;
    }

    @Override
    public List<NodeInstance> listNodeInstsByNodecode(long flowInstId, String nodeCode) {
        return null;
    }

    @Override
    public StageInstance getStageInstByNodeInstId(long nodeInstId) {
        return null;
    }
}
