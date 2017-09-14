package com.centit.workflow.support;

import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;


public class CloseOtherBranchEventBean implements NodeEventSupport {

    private FlowEngine flowEng;

    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode) {
        flowEng.disableOtherBranchNodes(nodeInst.getNodeInstId(), optUserCode);
        return true;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst, NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        return false;
    }

    public void setFlowEngine(FlowEngine flowEng) {
        this.flowEng = flowEng;
    }

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode) {
        
    }

    @Override
    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode) {
        
    }

 

}
