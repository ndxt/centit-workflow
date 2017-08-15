package com.centit.workflow.commons;

import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;


public class CloseOtherBranchNodesBean {

    private FlowEngine flowEng;
    

    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   String optParam, String optUserCode) {
        flowEng.disableOtherBranchNodes(nodeInst.getNodeInstId(), optUserCode);
        return true;
    }

    public void setFlowEngine(FlowEngine flowEng) {
        this.flowEng = flowEng;
    }


    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
            String optParam, String optUserCode) {
        // TODO Auto-generated method stub
        
    }


    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
            String optParam, String optUserCode) {
        // TODO Auto-generated method stub
        
    }

 

}
