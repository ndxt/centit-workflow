package com.centit.workflow.service.impl;

import com.centit.workflow.client.po.FlowInstance;
import com.centit.workflow.client.service.FlowEngineClient;
import com.centit.workflow.client.service.FlowManagerClient;
import com.centit.workflow.dao.ApprovalAuditorDao;
import com.centit.workflow.dao.ApprovalEventDao;
import com.centit.workflow.dao.ApprovalProcessDao;
import com.centit.workflow.po.ApprovalAuditor;
import com.centit.workflow.po.ApprovalEvent;
import com.centit.workflow.po.ApprovalProcess;
import com.centit.workflow.service.ApprovalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    private FlowEngineClient flowEngine;
    @Resource
    private FlowManagerClient flowManager;

    @Override
    public Long startProcess(HttpServletRequest request,ApprovalEvent approvalEvent,
                             List<ApprovalAuditor> approvalAuditors, int phaseNO, String userCode) throws Exception{
        //保存业务数据 创建流程
        Long approvalId = approvalEventDao.getNextApprovalEventId();
        approvalEvent.setApprovalId(approvalId);
        FlowInstance flowInstance = flowEngine.createInstanceLockFirstNode("000070",
                approvalEvent.getEventTitle(),String.valueOf(approvalEvent.getApprovalId()),"u0000000",null);
        approvalEvent.setFlowInstId(flowInstance.getFlowInstId());
        approvalEventDao.saveNewObject(approvalEvent);
        if(approvalAuditors != null && approvalAuditors.size()>0){
            for(ApprovalAuditor approvalAuditor : approvalAuditors){
                approvalAuditor.setApprovalId(approvalId);
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
    public void doApproval(List<String> userCodes, ApprovalProcess approvalProcess,
                           long flowInstId , long nodeInstId,String userCode, ServletContext ctx) throws Exception{
        List<ApprovalEvent> approvalEvents = approvalEventDao.getApprovalEventByFlowInstId(flowInstId);
        if(approvalEvents != null && approvalEvents.size()>0){
            approvalProcess.setPhraseNo(approvalEvents.get(0).getCurrentPhase());
            approvalProcess.setApprovalId(approvalEvents.get(0).getApprovalId());
        }
        approvalProcessDao.saveNewObject(approvalProcess);
        //是否通过
        String pass = approvalProcess.getAuditResult();
        flowEngine.saveFlowVariable(flowInstId,"pass","Y".equals(pass)?"0":"1");
//        //设置审批人
//        setNextStepAuditors(flowInstId,"auditor",userCodes,phaseNoMap.get("nextPhaseNo"));
        flowEngine.submitOpt(nodeInstId,userCode,"",null,ctx);

    }
}
