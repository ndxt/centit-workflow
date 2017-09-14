package com.centit.workflow.support;

import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;


public class CallStoredProcedureEventBean implements NodeEventSupport {

    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode) {

        return true;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst, NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        return false;
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
