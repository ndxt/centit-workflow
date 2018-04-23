package com.centit.demo.common;


import com.centit.demo.dao.ApprovalAuditorDao;
import com.centit.demo.dao.ApprovalEventDao;
import com.centit.demo.po.ApprovalAuditor;
import com.centit.demo.po.ApprovalEvent;
import com.centit.workflow.client.service.FlowEngineClient;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_rj on 2017/8/4.
 */
@Transactional
@Component("SetNextAuditorsBean")
public class SetNextAuditorsBean implements NodeEventSupport {
    @Resource
    private FlowEngineClient flowEngine;
    @Resource
    private ApprovalEventDao approvalEventDao;
    @Resource
    private ApprovalAuditorDao approvalAuditorDao;
    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode) throws WorkflowException {

    }

    @Override
    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode) throws WorkflowException {

    }

    /**
     * 自动运行节点，帮助重置流程变量 和workTeam
     * @param flowInst 流程实例
     * @param nodeInst 节点实例
     * @param nodeInfo 用户自定义操作参数
     * @param optUserCode 当前操作用户
     * @return
     * @throws WorkflowException
     */
    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        //先获取当前所在阶段
        List<ApprovalEvent> approvalEvents= approvalEventDao.getApprovalEventByFlowInstId(flowInst.getFlowInstId());
        if(approvalEvents == null || approvalEvents.size() == 0){
            return false;
        }
        ApprovalEvent approvalEvent = approvalEvents.get(0);
        String currentPhaseNo = approvalEvent.getCurrentPhase();
        String nextPhaseNo = "";
        try {
            int phaseNo_Num = Integer.parseInt(currentPhaseNo);
            nextPhaseNo = String.valueOf(phaseNo_Num+1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        List<FlowVariable> flowVariables = null;
        try {
            flowVariables = flowEngine.viewFlowVariablesByVarname(flowInst.getFlowInstId(),"maxPhase");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(flowVariables == null || flowVariables.size() == 0){
            return false;
        }
        List<ApprovalAuditor> approvalAuditors = approvalAuditorDao.getAuditorsByPhaseNo(nextPhaseNo);
        if((approvalAuditors == null || approvalAuditors.size() == 0) && !currentPhaseNo.equals(flowVariables.get(0).getVarValue()) ){
            return false;
        }
        //设置 currentPhaseNo 变量和审核人数量 和 审核人
        try {
            flowEngine.saveFlowVariable(flowInst.getFlowInstId(),"currentPhase",nextPhaseNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> userCodes = new ArrayList<>();
        for(ApprovalAuditor approvalAuditor:approvalAuditors){
            userCodes.add(approvalAuditor.getUserCode());
        }
        try {
            flowEngine.deleteFlowWorkTeam(flowInst.getFlowInstId(),"auditor");
            flowEngine.assignFlowWorkTeam(flowInst.getFlowInstId(),"auditor",userCodes);
            flowEngine.saveFlowVariable(flowInst.getFlowInstId(),"auditorCount",String.valueOf(userCodes.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //同步业务中的 阶段计数
        approvalEvent.setCurrentPhase(nextPhaseNo);
        approvalEventDao.mergeObject(approvalEvent);
        return true;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst, NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        return false;
    }
}
