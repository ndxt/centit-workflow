package com.centit.workflow.controller;

import com.centit.framework.core.common.JsonResultUtils;
import com.centit.workflow.po.ApprovalAuditor;
import com.centit.workflow.po.ApprovalEvent;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.service.ApprovalService;
import com.centit.workflow.service.FlowEngine;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Controller
@RequestMapping("/approval")
public class ApprovalController {
    @Resource
    private FlowEngine flowEngine;
    @Resource
    private ApprovalService approvalService;
    @RequestMapping("/startProcess")
    public void startProcess(HttpServletResponse response){
        ApprovalEvent approvalEvent = new ApprovalEvent();
        approvalEvent.setEventTitle("xxx报销流程申请");
        approvalEvent.setEventDesc("出差报销，100元");
        approvalEvent.setRequestTime(new Date());
        approvalEvent.setApprovalState("A");
        approvalEvent.setCurrentPhase("0");
        List<ApprovalAuditor> approvalAuditors =new ArrayList<>();
        ApprovalAuditor approvalAuditor = new ApprovalAuditor();
        approvalAuditor.setPhraseNo("0");
        approvalAuditor.setUserCode("u0000000");
        approvalAuditors.add(approvalAuditor);
        approvalService.startProcess(approvalEvent,approvalAuditors,3,approvalAuditor.getUserCode());
        JsonResultUtils.writeBlankJson(response);
    }
    @RequestMapping("/submitRequestForm")
    public void submitRequestForm(HttpServletResponse response){
        flowEngine.submitOpt(17,"u0000000","",null,null);
        JsonResultUtils.writeBlankJson(response);
    }
}
