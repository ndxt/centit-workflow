package com.centit.workflow.service.impl;

import com.centit.workflow.dao.ApprovalAuditorDao;
import com.centit.workflow.dao.ApprovalEventDao;
import com.centit.workflow.dao.ApprovalProcessDao;
import com.centit.workflow.po.*;
import com.centit.workflow.service.ApprovalService;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    @Resource
    private FlowManager flowManager;
    @Override
    public Long startProcess(HttpServletRequest request,ApprovalEvent approvalEvent,
                             List<ApprovalAuditor> approvalAuditors, int phaseNO, String userCode) {
        //保存业务数据 创建流程
        approvalEventDao.saveNewObject(approvalEvent);
        FlowInstance flowInstance = flowEngine.createInstanceLockFirstNode("000070",
                approvalEvent.getEventTitle(),String.valueOf(approvalEvent.getApprovalId()),"u0000000",null);
        if(approvalAuditors != null && approvalAuditors.size()>0){
            for(ApprovalAuditor approvalAuditor : approvalAuditors){
                approvalAuditorDao.saveNewObject(approvalAuditor);
            }
        }
        //计算第一个审核节点 审核员
        List<String> tempAuditors = new ArrayList<>();
        if(approvalAuditors != null && approvalAuditors.size() > 0){
            for(ApprovalAuditor approvalAuditor:approvalAuditors){
                if("1".equals(approvalAuditor.getPhaseNo())){
                    tempAuditors.add(approvalAuditor.getUserCode());
                }
            }
        }
        //初始化阶段计数变量
        flowEngine.saveFlowVariable(flowInstance.getFlowInstId(),"currentPhase","1");
        flowEngine.saveFlowVariable(flowInstance.getFlowInstId(),"maxPhase",String.valueOf(phaseNO));
        flowEngine.assignFlowWorkTeam(flowInstance.getFlowInstId(),"auditor",tempAuditors);
        flowEngine.saveFlowVariable(flowInstance.getFlowInstId(),"auditorCount",String.valueOf(tempAuditors.size()));
        return flowInstance.getFlowInstId();

    }
    @Override
    public void doApproval(ApprovalEvent approvalEvent, List<String> userCodes, ApprovalProcess approvalProcess,
                           long flowInstId , long nodeInstId,String userCode, ServletContext ctx) {
        //设置下一次的 审核阶段  返回本次的阶段
        Map<String,String> phaseNoMap = setNextStepPhaseNo(flowInstId,"currentPhase");
        approvalEvent.setCurrentPhase(phaseNoMap.get("currentPhaseNo"));
        approvalProcess.setPhraseNo(phaseNoMap.get("currentPhaseNo"));
        approvalEventDao.saveNewObject(approvalEvent);
        approvalProcessDao.saveNewObject(approvalProcess);
        //是否通过
        String pass = approvalProcess.getAuditResult();
        flowEngine.saveFlowVariable(flowInstId,"pass","Y".equals(pass)?"0":"1");
        //设置审批人
        setNextStepAuditors(flowInstId,"auditor",userCodes,phaseNoMap.get("nextPhaseNo"));
        flowEngine.submitOpt(nodeInstId,userCode,"",null,ctx);

    }
    /**
     * 设置下一步的审核人 包括设置workTeam 和Auditor
     * @param userCodeList
     * @param flowInstId
     */
    private void setNextStepAuditors(Long flowInstId,String roleCode,List<String> userCodeList, String phaseNo) {
       List<String> tempUserCode = new ArrayList<>();
        //获取这个流程下所有的审核员
        List<ApprovalAuditor> approvalAuditors = approvalAuditorDao.listObjects();
        //如果 审核没有指定下一步的审核人，则使用申请时指定的
        if(userCodeList == null || userCodeList.size() == 0){
            if(approvalAuditors != null && approvalAuditors.size()>0){
                for(ApprovalAuditor approvalAuditor:approvalAuditors){
                    if(phaseNo != null && phaseNo.equals(approvalAuditor.getPhaseNo())){
                        tempUserCode.add(approvalAuditor.getUserCode());
                    }
                }
            }
        }else {//使用本次指定的替换
            tempUserCode = userCodeList;
            List<ApprovalAuditor> tempApprovalAuditors = new ArrayList<>();
            //更新auditor
            for (String userCode : userCodeList) {
                ApprovalAuditor approvalAuditor = new ApprovalAuditor();
                approvalAuditor.setUserCode(userCode);
                approvalAuditor.setPhaseNo(phaseNo);
                tempApprovalAuditors.add(approvalAuditor);
            }
            if (approvalAuditors != null && tempApprovalAuditors.size() > 0) {
                for (ApprovalAuditor approvalAuditor : approvalAuditors) {
                    if (phaseNo.equals(approvalAuditor.getPhaseNo())) {
                        approvalAuditorDao.deleteObject(approvalAuditor);
                    }
                }
            }
            approvalAuditorDao.saveNewObjects(tempApprovalAuditors);
        }
        //更新workTeam
        flowEngine.deleteFlowWorkTeam(flowInstId,roleCode);
        flowEngine.assignFlowWorkTeam(flowInstId,roleCode,tempUserCode);
        //设置审核人数变量
        flowEngine.saveFlowVariable(flowInstId,"auditorCount",String.valueOf(tempUserCode.size()));
    }

    /**
     * 设置下一次的 审核阶段  并返回
     * @param flowInstId
     * @return
     */
    private Map<String,String> setNextStepPhaseNo(Long flowInstId, String varName) {
        Map<String,String> map = new HashMap<>();
        String currentPhaseNo = "";
        String nextPhaseNo = "";
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
        if(currentPhaseNo_Num >= 0){
            nextPhaseNo = String.valueOf(currentPhaseNo_Num+1);
            flowEngine.saveFlowVariable(flowInstId,varName,nextPhaseNo);
        }
        map.put("currentPhaseNo",currentPhaseNo);
        map.put("nextPhaseNo",nextPhaseNo);
        return map;
    }
}
