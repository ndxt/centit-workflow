package com.centit.demo.common;

import com.centit.demo.dao.ApprovalEventDao;
import com.centit.demo.dao.ApprovalProcessDao;
import com.centit.demo.po.ApprovalProcess;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chen_rj on 2017/8/4.
 */
@Transactional
@Component
public class VoteAndSetAuditorsBean implements NodeEventSupport {
    @Autowired
    private ApprovalEventDao approvalEventDao;
    @Autowired
    private ApprovalProcessDao approvalProcessDao;
    @Autowired
    private FlowEngine flowEngine;
    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode)  {

    }

    /**
     * 多实例 生成统计结果
     * @param flowInst 流程实例
     * @param nodeInst 节点实例
     * @param nodeInfo 用户自定义操作参数
     * @param optUserCode 当前操作用户
     * @
     */
    @Override
    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode)  {
        String nodeInstId = nodeInst.getNodeInstId();
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
                                   NodeInfo nodeInfo, String optUserCode)  {
        return false;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst, NodeInfo nodeInfo, String optUserCode)  {
        return false;
    }
}
