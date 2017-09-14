package com.centit.workflow.common;

import com.centit.workflow.client.service.FlowEngineClient;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.dao.ApprovalEventDao;
import com.centit.workflow.dao.ApprovalProcessDao;
import com.centit.workflow.po.ApprovalProcess;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by chen_rj on 2017/8/4.
 */
@Transactional
@Component("VoteAndSetAuditorsBean")
public class VoteAndSetAuditorsBean implements NodeEventSupport {
    @Resource
    private ApprovalEventDao approvalEventDao;
    @Resource
    private ApprovalProcessDao approvalProcessDao;
    @Resource
    private FlowEngineClient flowEngine;
    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode) throws WorkflowException {

    }

    /**
     * 多实例 生成统计结果
     * @param flowInst 流程实例
     * @param nodeInst 节点实例
     * @param nodeInfo 用户自定义操作参数
     * @param optUserCode 当前操作用户
     * @throws WorkflowException
     */
    @Override
    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        Long nodeInstId = nodeInst.getNodeInstId();
        List<ApprovalProcess> approvalProcesses = null;
        try {
            approvalProcesses = approvalProcessDao.getApprovalProcessByNodeInstId(nodeInst.getNodeInstId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(approvalProcesses == null || approvalProcesses.size() == 0){
            return;
        }
        String pass = "0";
        for(ApprovalProcess approvalProcess:approvalProcesses){
            if(!"Y".equals(approvalProcess.getAuditResult())){
                pass = "1";
                break;
            }
        }
        try {
            flowEngine.saveFlowVariable(flowInst.getFlowInstId(),"pass",pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        return false;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst, NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        return false;
    }
}
