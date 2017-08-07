package com.centit.workflow.common;

import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * Created by chen_rj on 2017/8/4.
 */
@Transactional
@Component("SetNextAuditorsBean")
public class SetNextAuditorsBean implements NodeEventSupport {
    @Resource
    private FlowEngine flowEngine;
    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst, String optParam, String optUserCode) throws WorkflowException {

    }

    @Override
    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst, String optParam, String optUserCode) throws WorkflowException {

        System.out.print(">>>>>>>>>runBeforeSubmit>>>>>>>");
    }

    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst, String optParam, String optUserCode) throws WorkflowException {
        return false;
    }
}
