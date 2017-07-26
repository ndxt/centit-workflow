package com.centit.workflow.commons;

import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;


public class CloseOtherBranchNodesBean implements NodeEventSupport{

    private FlowEngine flowEng;
    
    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   String optParam, String optUserCode) {
        flowEng.disableOtherBranchNodes(nodeInst.getNodeInstId(), optUserCode);
        return true;
    }

    public void setFlowEngine(FlowEngine flowEng) {
        this.flowEng = flowEng;
    }

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
            String optParam, String optUserCode) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
            String optParam, String optUserCode) {
        // TODO Auto-generated method stub
        
    }

 

}
