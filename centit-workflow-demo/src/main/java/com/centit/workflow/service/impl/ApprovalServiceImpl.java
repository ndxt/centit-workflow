package com.centit.workflow.service.impl;

import com.centit.workflow.dao.ApprovalAuditorDao;
import com.centit.workflow.dao.ApprovalEventDao;
import com.centit.workflow.dao.ApprovalProcessDao;
import com.centit.workflow.po.*;
import com.centit.workflow.service.ApprovalService;
import com.centit.workflow.service.FlowEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
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
    public void startProcess(ApprovalEvent approvalEvent, List<ApprovalAuditor> approvalAuditors,int phaseNO,String userCode) {
        //保存业务数据 创建流程
        approvalEventDao.saveNewObject(approvalEvent);
        FlowInstance flowInstance = flowEngine.createInstanceLockFirstNode("000070",approvalEvent.getEventTitle(),String.valueOf(approvalEvent.getApprovalId()),"u0000000",null);
        if(approvalAuditors != null && approvalAuditors.size()>0){
            for(ApprovalAuditor approvalAuditor : approvalAuditors){
                approvalAuditorDao.saveNewObject(approvalAuditor);
                //flowEngine.assignFlowWorkTeam(flowInstance.getFlowInstId(),"auditor",approvalAuditor.getUserCode());
            }
        }
//        List<UserTask> userTasks = flowEngine.listUserTasksByFlowCode(userCode,"000070",new PageDesc(-1,-1));
//        Long nodeInstId = 0l;
//        if(userTasks != null && userTasks.size()>0){
//            nodeInstId = userTasks.get(0).getNodeInstId();
//        }
//        flowEngine.submitOpt(nodeInstId,userCode,"",null,null);
        //初始化阶段计数变量
        flowEngine.saveFlowVariable(flowInstance.getFlowInstId(),"currentPhase","0");
        flowEngine.saveFlowVariable(flowInstance.getFlowInstId(),"maxPhase",String.valueOf(phaseNO));

    }

    @Override
    public void doApproval(ApprovalEvent approvalEvent, List<ApprovalAuditor> approvalAuditors, ApprovalProcess approvalProcess, long flowInstId , long nodeInstId, ServletContext ctx) {
        approvalEventDao.saveNewObject(approvalEvent);
        approvalProcessDao.saveNewObject(approvalProcess);
        if(approvalAuditors != null && approvalAuditors.size()>0){
            for(ApprovalAuditor approvalAuditor : approvalAuditors){
                approvalAuditorDao.saveNewObject(approvalAuditor);
            }
        }
        //是否通过
        String pass = approvalProcess.getAuditResult();
        flowEngine.saveFlowVariable(flowInstId,"pass","Y".equals(pass)?"0":"1");
        //阶段计数
        List<FlowVariable> variables = flowEngine.viewFlowVariablesByVarname(flowInstId,"currentPhase");
        if(variables != null && variables.size()>0){
            try {
                flowEngine.saveFlowVariable(flowInstId,"currentPhase",String.valueOf(Integer.parseInt(variables.get(0).getVarValue())+1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        flowEngine.submitOpt(nodeInstId,approvalProcess.getUserCode(),"",null,ctx);
    }
}
