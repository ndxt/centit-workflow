package com.centit.workflow.common;

import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
import org.springframework.stereotype.Component;

/**
 * Created by chen_rj on 2017/8/4.
 */
@Component("VoteAndSetAuditorsBean")
public class VoteAndSetAuditorsBean implements NodeEventSupport {
    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst, String optParam, String optUserCode) throws WorkflowException {

    }

    @Override
    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst, String optParam, String optUserCode) throws WorkflowException {

    }

    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst, String optParam, String optUserCode) throws WorkflowException {
        return false;
    }
}
