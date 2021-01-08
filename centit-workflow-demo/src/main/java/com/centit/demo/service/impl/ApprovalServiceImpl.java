package com.centit.demo.service.impl;

import com.centit.demo.dao.ApprovalAuditorDao;
import com.centit.demo.dao.ApprovalEventDao;
import com.centit.demo.dao.ApprovalProcessDao;
import com.centit.demo.po.ApprovalAuditor;
import com.centit.demo.po.ApprovalEvent;
import com.centit.demo.po.ApprovalProcess;
import com.centit.demo.service.ApprovalService;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.commons.CreateFlowOptions;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.UserTask;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private ApprovalEventDao approvalEventDao;
    @Autowired
    private ApprovalAuditorDao approvalAuditorDao;
    @Autowired
    private ApprovalProcessDao approvalProcessDao;
    @Autowired
    private FlowEngine flowEngine;
    @Autowired
    private FlowManager flowManager;

    @Override
    public String startProcess(HttpServletRequest request, ApprovalEvent approvalEvent,
                             List<ApprovalAuditor> approvalAuditors, int phaseNO, String userCode) throws Exception{
        //保存业务数据 创建流程
        Long approvalId = approvalEventDao.getNextApprovalEventId();
        approvalEvent.setApprovalId(approvalId);
        FlowInstance flowInstance = flowEngine.createInstance(
            CreateFlowOptions.create().flow("000070")
                .optName(approvalEvent.getEventTitle())
            .optTag(String.valueOf(approvalEvent.getApprovalId()))
            .user("u0000000")
            .workUser("u0000000"));
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
                           String flowInstId , String nodeInstId,String userCode, ServletContext ctx) throws Exception{
        List<ApprovalEvent> approvalEvents = approvalEventDao.getApprovalEventByFlowInstId(flowInstId);
        if(approvalEvents != null && approvalEvents.size()>0){
            approvalProcess.setPhraseNo(approvalEvents.get(0).getCurrentPhase());
            approvalProcess.setApprovalId(approvalEvents.get(0).getApprovalId());
        }
        approvalProcessDao.saveNewObject(approvalProcess);
        //是否通过
        String pass = approvalProcess.getAuditResult();
        flowEngine.saveFlowVariable(flowInstId,"pass", "Y".equals(pass) ? "0":"1");
//        //设置审批人
//        setNextStepAuditors(flowInstId,"auditor",userCodes,phaseNoMap.get("nextPhaseNo"));
        flowEngine.submitOpt(SubmitOptOptions.create().nodeInst(nodeInstId).user(userCode));

    }

    @Override
    public List<UserTask>  getUserTasksByUserCode(String userCode) throws Exception {
        return flowEngine.listTasks(
            CollectionsOpt.createHashMap("userCode",userCode),
            new PageDesc(-1,-1));
    }
}
