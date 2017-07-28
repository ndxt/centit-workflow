package com.centit.workflow.controller;

import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.PageDesc;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.service.FlowEngine;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chen_rj on 2017/7/28.
 */
@Controller
@RequestMapping("/flow/engine")
public class FlowEngineController  extends BaseController {
    @Resource
    private FlowEngine flowEng;
    @RequestMapping(value = "/createFlowInstDefault")
    public void createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode, HttpServletResponse httpResponse) {
        FlowInstance flowInstance = flowEng.createInstance(flowCode, flowOptName,flowOptTag,userCode,unitCode);
        JsonResultUtils.writeSingleDataJson(flowInstance,httpResponse);
    }

//    public FlowInstance createInstance(String flowCode, long version, String flowOptName, String flowOptTag, String userCode, String unitCode) {
//        return null;
//    }
//
//    public FlowInstance createInstance(String flowCode, long version, String flowOptName, String flowOptTag, String userCode, String unitCode, Map<String, Object> varTrans, ServletContext application) {
//        return null;
//    }
//
//    public FlowInstance createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode, Map<String, Object> varTrans, ServletContext application) {
//        return null;
//    }
//
//    public FlowInstance createInstanceLockFirstNode(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode) {
//        return null;
//    }
//
//    public FlowInstance createInstanceLockFirstNode(String flowCode, long version, String flowOptName, String flowOptTag, String userCode, String unitCode) {
//        return null;
//    }
//
//    public Set<Long> submitOpt(long nodeInstId, String userCode, String unitCode, Map<String, Object> varTrans, ServletContext application)  {
//        return null;
//    }
//
//    public Set<Long> submitOpt(long nodeInstId, String userCode, String grantorCode, String unitCode, Map<String, Object> varTrans, ServletContext application) {
//        return null;
//    }
//
//    public Set<Long> submitOptWithAssignUnitAndUser(long nodeInstId, String userCode, String unitCode, Map<String, Object> varTrans, Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers, ServletContext application)  {
//        return null;
//    }
//
//    public Set<Long> submitOptWithAssignUnitAndUser(long nodeInstId, String userCode, String grantorCode, String unitCode, Map<String, Object> varTrans, Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers, ServletContext application)  {
//        return null;
//    }
//
//    public Set<NodeInfo> viewNextNode(long nodeInstId, String userCode, String unitCode, Map<String, Object> varTrans) {
//        return null;
//    }
//
//    public Set<String> viewNextNodeOperator(long nextNodeId, long curNodeInstId, String userCode, String unitCode, Map<String, Object> varTrans) {
//        return null;
//    }
//
//    public List<UserTask> listUserTasks(String userCode, PageDesc pageDesc) {
//        return null;
//    }
//
//    public List<UserTask> listUserTasksByFlowCode(String userCode, String flowCode, PageDesc pageDesc) {
//        return null;
//    }
//
//    public List<UserTask> listUserTasksByFlowStage(String userCode, String flowStage, PageDesc pageDesc) {
//        return null;
//    }
//
//    public List<UserTask> listUserTasksByNodeCode(String userCode, String nodeCode, PageDesc pageDesc) {
//        return null;
//    }
//
//    public List<UserTask> listUserCompleteTasks(Map<String, Object> filterMap, PageDesc pageDesc) {
//        return null;
//    }
//
//    public boolean canAccess(long nodeInstId, String userCode) {
//        return false;
//    }
//
//    public String getTaskGrantor(long nodeInstId, String userCode) {
//        return null;
//    }
//
//    public List<FlowWarning> listFlowWarning(Map<String, Object> filterMap, PageDesc pageDesc) {
//        return null;
//    }
//
//    public List<FlowWarning> listFlowWarningByInst(Long flowInstId, PageDesc pageDesc) {
//        return null;
//    }
//
//    public List<FlowWarning> listFlowWarningByNodeInst(Long nodeInstId, PageDesc pageDesc) {
//        return null;
//    }
//
//    public List<FlowWarning> listFlowWarningByWarningCode(String warningCode, PageDesc pageDesc) {
//        return null;
//    }
//
//    public void updateFlowInstOptInfo(long flowInstId, String flowOptName, String flowOptTag) {
//
//    }
//
//    public void recordActionLog(long nodeInstId, String userCode, String actionType) {
//
//    }
//
//    public long rollbackOpt(long nodeInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    public boolean nodeCanBeReclaim(long nodeInstId) {
//        return false;
//    }
//
//    public NodeInstance createPrepNodeInstLockUser(long flowInstId, long curNodeInstId, long nodeId, String createUser, String userCode, String unitCode) {
//        return null;
//    }
//
//    public NodeInstance createPrepNodeInstLockUser(long flowInstId, long curNodeInstId, String nodeCode, String createUser, String userCode, String unitCode) {
//        return null;
//    }
//
//    public NodeInstance createPrepNodeInst(long flowInstId, long curNodeInstId, long nodeId, String userCode, String unitCode) {
//        return null;
//    }
//
//    public NodeInstance createPrepNodeInst(long flowInstId, long curNodeInstId, String nodeCode, String userCode, String unitCode) {
//        return null;
//    }
//
//    public NodeInstance createIsolatedNodeInst(long flowInstId, long curNodeInstId, String nodeCode, String createUser, String userCode, String unitCode) {
//        return null;
//    }
//
//    public NodeInstance createIsolatedNodeInst(long flowInstId, long curNodeInstId, long nodeId, String createUser, String userCode, String unitCode) {
//        return null;
//    }
//
//    public void updateFlowInstParentNode(long flowInstId, long parentFlowInstId, long parentNodeInstId) {
//
//    }
//
//    public void disableOtherBranchNodes(long nodeInstId, String optUserCode) {
//
//    }
//
//    public void assignFlowWorkTeam(long flowInstId, String roleCode, String userCode) {
//
//    }
//
//    public void assignFlowWorkTeam(long flowInstId, String roleCode, List<String> userCodeSet) {
//
//    }
//
//    public void assignFlowWorkTeam(long flowInstId, String roleCode, String userCode, String authdesc) {
//
//    }
//
//    public void assignFlowWorkTeam(long flowInstId, String roleCode, List<String> userCodeSet, String authdesc) {
//
//    }
//
//    public void deleteFlowWorkTeam(long flowInstId, String roleCode, String userCode) {
//
//    }
//
//    public void deleteFlowWorkTeam(long flowInstId, String roleCode) {
//
//    }
//
//    public Map<String, List<String>> viewFlowWorkTeam(long flowInstId) {
//        return null;
//    }
//
//    public List<String> viewFlowWorkTeam(long flowInstId, String roleCode) {
//        return null;
//    }
//
//    public List<FlowWorkTeam> viewFlowWorkTeamList(long flowInstId, String roleCode) {
//        return null;
//    }
//
//    public List<FlowWorkTeam> viewFlowWorkTeamList(long flowInstId, String roleCode, String authdesc) {
//        return null;
//    }
//
//    public long assignNodeTask(long nodeInstId, String userCode, String mangerUserCode, Date expiretime, String authDesc) {
//        return 0;
//    }
//
//    public int disableNodeTask(long nodeInstId, String userCode, String mangerUserCode) {
//        return 0;
//    }
//
//    public int deleteNodeTask(long nodeInstId, String userCode, String mangerUserCode) {
//        return 0;
//    }
//
//    public int deleteNodeAllTask(long nodeInstId, String mangerUserCode) {
//        return 0;
//    }
//
//    public void assignFlowOrganize(long flowInstId, String roleCode, String unitCode) {
//
//    }
//
//    public void assignFlowOrganize(long flowInstId, String roleCode, List<String> unitCodeSet) {
//
//    }
//
//    public void assignFlowOrganize(long flowInstId, String roleCode, String unitCode, String authdesc) {
//
//    }
//
//    public void assignFlowOrganize(long flowInstId, String roleCode, List<String> unitCodeSet, String authdesc) {
//
//    }
//
//    public void deleteFlowOrganize(long flowInstId, String roleCode, String unitCode) {
//
//    }
//
//    public void deleteFlowOrganize(long flowInstId, String roleCode) {
//
//    }
//
//    public void deleteFlowOrganizeByAuth(long flowInstId, String roleCode, String authDesc) {
//
//    }
//
//    public Map<String, List<String>> viewFlowOrganize(long flowInstId) {
//        return null;
//    }
//
//    public List<String> viewFlowOrganize(long flowInstId, String roleCode) {
//        return null;
//    }
//
//    public List<FlowOrganize> viewFlowOrganizeList(long flowInstId, String roleCode) {
//        return null;
//    }
//
//    public List<FlowOrganize> viewFlowOrganizeList(long flowInstId, String roleCode, String authDesc) {
//        return null;
//    }
//
//    public void saveFlowVariable(long flowInstId, String sVar, String sValue) {
//
//    }
//
//    public void saveFlowNodeVariable(long nodeInstId, String sVar, String sValue) {
//
//    }
//
//    public void saveFlowNodeVariable(long flowInstId, String runToken, String sVar, String sValue) {
//
//    }
//
//    public void saveFlowVariable(long flowInstId, String sVar, Set<String> sValues) {
//
//    }
//
//    public void saveFlowNodeVariable(long nodeInstId, String sVar, Set<String> sValues) {
//
//    }
//
//    public List<FlowVariable> listFlowVariables(long flowInstId) {
//        return null;
//    }
//
//    public List<FlowVariable> viewFlowVariablesByVarname(long flowInstId, String varname) {
//        return null;
//    }
//
//    public FlowVariable viewNodeVariable(long flowInstId, String runToken, String varname) {
//        return null;
//    }
//
//    public void saveFlowAttention(long flowInstId, String attUser, String optUser) {
//
//    }
//
//    public void deleteFlowAttention(long flowInstId, String attUser) {
//
//    }
//
//    public void deleteFlowAttentionByOptUser(long flowInstId, String optUser) {
//
//    }
//
//    public void deleteFlowAttention(long flowInstId) {
//
//    }
//
//    public List<InstAttention> viewFlowAttention(long flowInstId) {
//        return null;
//    }
//
//    public InstAttention getFlowAttention(long flowInstId, String userCode) {
//        return null;
//    }
//
//    public List<FlowInstance> viewAttentionFLowInstance(String userCode, String instState) {
//        return null;
//    }
//
//    public FlowInstance getFlowInstById(long flowInstId) {
//        return null;
//    }
//
//    public NodeInstance getNodeInstById(long nodeInstId) {
//        return null;
//    }
//
//    public String getNodeOptUrl(long nodeInstId, String userCode) {
//        return null;
//    }
//
//    public List<NodeInstance> listNodeInstsByNodecode(long flowInstId, String nodeCode) {
//        return null;
//    }
//
//    public StageInstance getStageInstByNodeInstId(long nodeInstId) {
//        return null;
//    }
}
