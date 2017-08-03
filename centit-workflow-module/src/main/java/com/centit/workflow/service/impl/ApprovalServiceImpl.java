package com.centit.workflow.service.impl;

import com.centit.workflow.dao.ApprovalAuditorDao;
import com.centit.workflow.dao.ApprovalEventDao;
import com.centit.workflow.dao.ApprovalProcessDao;
import com.centit.workflow.po.ApprovalAuditor;
import com.centit.workflow.po.ApprovalEvent;
import com.centit.workflow.po.ApprovalProcess;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.service.ApprovalService;
import com.centit.workflow.service.FlowEngine;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Service
public class ApprovalServiceImpl implements ApprovalService{
    @Resource
    private ApprovalEventDao approvalEventDao;
    @Resource
    private ApprovalAuditorDao approvalAuditorDao;
    @Resource
    private ApprovalProcessDao approvalProcessDao;
    @Resource
    private FlowEngine flowEngine;
    @Override
    public void startApplication(ApprovalEvent approvalEvent, List<ApprovalAuditor> approvalAuditors,long flowInstId) {
        approvalEventDao.saveNewObject(approvalEvent);
        approvalAuditorDao.saveNewObjects(approvalAuditors);
        //初始化阶段计数变量
        flowEngine.saveFlowVariable(flowInstId,"currentPhase","0");
    }

    @Override
    public void doApproval(ApprovalEvent approvalEvent, List<ApprovalAuditor> approvalAuditors, ApprovalProcess approvalProcess,long flowInstId) {
        approvalEventDao.saveNewObject(approvalEvent);
        approvalProcessDao.saveNewObject(approvalProcess);
        List<FlowVariable> variables = flowEngine.viewFlowVariablesByVarname(flowInstId,"currentPhase");
        //设置阶段计数值
        if(variables != null && variables.size() > 0){
            String value = variables.get(0).getVarValue();
            try {
                value = String.valueOf(Integer.parseInt(value)+1);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            flowEngine.saveFlowVariable(flowInstId,"currentPhase",value);
        }
    }
}
