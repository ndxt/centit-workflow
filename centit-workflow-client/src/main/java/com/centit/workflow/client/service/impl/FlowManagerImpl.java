package com.centit.workflow.client.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.framework.core.dao.PageDesc;
import com.centit.workflow.client.service.FlowManager;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017/7/28.
 */
public class FlowManagerImpl implements FlowManager {
    public FlowManagerImpl() {

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


//    @Override
//    public List<FlowInstance> listFlowInstance(Map<String, Object> filterMap, PageDesc pageDesc) {
//        return null;
//    }
//
//    @Override
//    public FlowInstance getFlowInstance(long flowInstId) {
//        return null;
//    }
//
//    @Override
//    public String viewFlowInstance(long flowInstId) {
//        return null;
//    }
//
//    @Override
//    public String viewFlowNodeInstance(long flowInstId) {
//        return null;
//    }
//
//    @Override
//    public int stopInstance(long flowInstId, String mangerUserCode, String admindesc) {
//        return 0;
//    }
//
//    @Override
//    public int suspendInstance(long flowInstId, String mangerUserCode, String admindesc) {
//        return 0;
//    }
//
//    @Override
//    public int activizeInstance(long flowInstId, String mangerUserCode, String admindesc) {
//        return 0;
//    }
//
//    @Override
//    public List<FlowInstance> listPauseTimerFlowInst(String userCode, PageDesc pageDesc) {
//        return null;
//    }
//
//    @Override
//    public int suspendFlowInstTimer(long flowInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public int activizeFlowInstTimer(long flowInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public long resetFlowTimelimt(long flowInstId, String timeLimit, String mangerUserCode, String admindesc) {
//        return 0;
//    }
//
//    @Override
//    public void updateFlowInstUnit(long flowInstId, String unitCode, String optUserCode) {
//
//    }
//
//    @Override
//    public List<ManageActionLog> listManageActionLog(long flowInstId, PageDesc pageDesc) {
//        return null;
//    }
//
//    @Override
//    public List<NodeInstance> listFlowInstNodes(long wfinstid) {
//        return null;
//    }
//
//    @Override
//    public long suspendNodeInstance(long nodeInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public long activizeNodeInstance(long nodeInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public long forceCommit(long nodeInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public List<NodeInstance> listPauseTimerNodeInst(String userCode, PageDesc pageDesc) {
//        return null;
//    }
//
//    @Override
//    public int suspendNodeInstTimer(long nodeInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public int activizeNodeInstTimer(long nodeInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public long resetFlowToThisNode(long nodeInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public long forceDissociateRuning(long nodeInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public void updateNodeInstUnit(long nodeInstId, String unitCode, String optUserCode) {
//
//    }
//
//    @Override
//    public void updateNodeRoleInfo(long nodeInstId, String roleType, String roleCode, String mangerUserCode) {
//
//    }
//
//    @Override
//    public long resetNodeTimelimt(long nodeInstId, String timeLimit, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public List<UserTask> listNodeTasks(long nodeInstId) {
//        return null;
//    }
//
//    @Override
//    public List<ActionTask> listNodeActionTasks(long nodeinstid) {
//        return null;
//    }
//
//    @Override
//    public List<StageInstance> listStageInstByFlowInstId(long flowInstId) {
//        return null;
//    }
//
//    @Override
//    public long resetStageTimelimt(long flowInstId, long stageId, String timeLimit, String mangerUserCode, String admindesc) {
//        return 0;
//    }
//
//    @Override
//    public List<ActionLog> listNodeActionLogs(long nodeinstid) {
//        return null;
//    }
//
//    @Override
//    public List<ActionLog> listFlowActionLogs(long flowInstId) {
//        return null;
//    }
//
//    @Override
//    public List<ActionLog> listUserActionLogs(String userCode, PageDesc pageDesc, Date lastTime) {
//        return null;
//    }
//
//    @Override
//    public List<ActionLog> listGrantorActionLog(String userCode, PageDesc pageDesc) {
//        return null;
//    }
//
//    @Override
//    public List<ActionLog> listGrantdedActionLog(String userCode, PageDesc pageDesc) {
//        return null;
//    }
//
//    @Override
//    public List<FlowInstance> listUserAttachFlowInstance(String userCode, String flowPhase, Map<String, Object> filterMap, PageDesc pageDesc) {
//        return null;
//    }
//
//    @Override
//    public List<NodeInstance> listNodesWithoutOpt() {
//        return null;
//    }
//
//    @Override
//    public long assignTask(long nodeInstId, String userCode, String mangerUserCode, Date expiretime, String authDesc) {
//        return 0;
//    }
//
//    @Override
//    public List<ActionTask> listNodeInstTasks(Long nodeInstId) {
//        return null;
//    }
//
//    @Override
//    public int disableTask(long taskInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public int deleteTask(long taskInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    @Override
//    public RoleRelegate getRoleRelegateById(Long relegateno) {
//        return null;
//    }
//
//    @Override
//    public void saveRoleRelegate(RoleRelegate roleRelegate) {
//
//    }
//
//    @Override
//    public List<RoleRelegate> listRoleRelegateByUser(String userCode) {
//        return null;
//    }
//
//    @Override
//    public List<RoleRelegate> listRoleRelegateByGrantor(String grantor) {
//        return null;
//    }
//
//    @Override
//    public void deleteRoleRelegate(Long relegateno) {
//
//    }
//
//    @Override
//    public int moveUserTaskTo(String fromUserCode, String toUserCode, String optUserCode, String moveDesc) {
//        return 0;
//    }
}
