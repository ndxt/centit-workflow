package com.centit.workflow.service.impl;

import com.centit.workflow.dao.ApprovalAuditorDao;
import com.centit.workflow.dao.ApprovalEventDao;
import com.centit.workflow.dao.ApprovalProcessDao;
import com.centit.workflow.po.*;
import com.centit.workflow.service.ApprovalService;
import com.centit.workflow.service.FlowEngine;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Service
@Transactional
public class ApprovalServiceImpl implements ApprovalService {
    @Resource
    private ApprovalEventDao approvalEventDao;
    @Resource
    private ApprovalAuditorDao approvalAuditorDao;
    @Resource
    private ApprovalProcessDao approvalProcessDao;
    @Resource
    private FlowEngine flowEngine;
    @Override
    public Long startProcess(ApprovalEvent approvalEvent, List<ApprovalAuditor> approvalAuditors,int phaseNO,String userCode) {
        //保存业务数据 创建流程
        approvalEventDao.saveNewObject(approvalEvent);
        FlowInstance flowInstance = flowEngine.createInstanceLockFirstNode("000070",approvalEvent.getEventTitle(),String.valueOf(approvalEvent.getApprovalId()),"u0000000",null);
        if(approvalAuditors != null && approvalAuditors.size()>0){
            for(ApprovalAuditor approvalAuditor : approvalAuditors){
                approvalAuditorDao.saveNewObject(approvalAuditor);
            }
        }
        //初始化阶段计数变量
        flowEngine.saveFlowVariable(flowInstance.getFlowInstId(),"currentPhase","0");
        flowEngine.saveFlowVariable(flowInstance.getFlowInstId(),"maxPhase",String.valueOf(phaseNO));
        return flowInstance.getFlowInstId();

    }
    @Override
    public void doApproval(ApprovalEvent approvalEvent, List<String> userCodes, ApprovalProcess approvalProcess, long flowInstId , long nodeInstId, ServletContext ctx) {
        //设置下一次的 审核阶段  返回本次的阶段
        String phaseNo = setNextStepPhaseNo(flowInstId,"currentPhase");
        approvalEvent.setCurrentPhase(phaseNo);
        approvalProcess.setPhraseNo(phaseNo);
        approvalEventDao.saveNewObject(approvalEvent);
        approvalProcessDao.saveNewObject(approvalProcess);
        //是否通过
        String pass = approvalProcess.getAuditResult();
        flowEngine.saveFlowVariable(flowInstId,"pass","Y".equals(pass)?"0":"1");
        //设置下次审核的审批人
        setNextStepAuditors(flowInstId,"auditor",userCodes,phaseNo);
        flowEngine.submitOpt(nodeInstId,approvalProcess.getUserCode(),"",null,ctx);


    }
    /**
     * 设置下一步的审核人 包括设置workTeam 和Auditor
     * @param userCodeList
     * @param flowInstId
     */
    private void setNextStepAuditors(Long flowInstId,String roleCode,List<String> userCodeList, String phaseNo) {
        if(userCodeList == null || userCodeList.size() == 0){
            return;
        }
        List<ApprovalAuditor> tempApprovalAuditors = new ArrayList<>();
        //更新workTeam
        flowEngine.deleteFlowWorkTeam(flowInstId,roleCode);
        flowEngine.assignFlowWorkTeam(flowInstId,roleCode,userCodeList);
        //更新auditor
        for(String userCode:userCodeList){
            ApprovalAuditor approvalAuditor = new ApprovalAuditor();
            approvalAuditor.setUserCode(userCode);
            approvalAuditor.setPhaseNo(phaseNo);
            tempApprovalAuditors.add(approvalAuditor);
        }
        //获取这个流程下所有的审核员 用这次指定的审核员 替换原来的
        List<ApprovalAuditor> approvalAuditors = approvalAuditorDao.listObjects();
        if(approvalAuditors != null && tempApprovalAuditors.size()>0){
            for(ApprovalAuditor approvalAuditor:approvalAuditors){
                if(phaseNo.equals(approvalAuditor.getPhaseNo())){
                    approvalAuditorDao.deleteObject(approvalAuditor);
                }
            }
        }
        approvalAuditorDao.saveNewObjects(tempApprovalAuditors);
    }

    /**
     * 设置下一次的 审核阶段  返回本次的阶段
     * @param flowInstId
     * @return
     */
    private String setNextStepPhaseNo(Long flowInstId,String varName) {
        String currentPhaseNo = "";
        int currentPhaseNo_Num = -1;
        List<FlowVariable> flowVariables = flowEngine.viewFlowVariablesByVarname(flowInstId,varName);
        if(flowVariables != null && flowVariables.size()>0){
            currentPhaseNo = flowVariables.get(0).getVarValue();
        }
        try {
            currentPhaseNo_Num = Integer.parseInt(currentPhaseNo);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if(currentPhaseNo_Num > 0){
            flowEngine.saveFlowVariable(flowInstId,varName,String.valueOf(currentPhaseNo_Num+1));
        }
        return currentPhaseNo;
    }
}
