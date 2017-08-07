package com.centit.workflow.controller;

import com.centit.framework.core.common.JsonResultUtils;
import com.centit.workflow.service.FlowEngine;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    @RequestMapping("/submitRequestForm/{flowInstId}/{nodeInstId}")
    public void submitRequestForm(HttpServletRequest request, HttpServletResponse response, @PathVariable Long flowInstId, @PathVariable Long nodeInstId){
        //flowEngine.assignFlowWorkTeam(flowInstId,"auditor","u0000001");
        flowEngine.assignFlowWorkTeam(flowInstId,"auditor","u0000000");
        flowEngine.saveFlowVariable(flowInstId,"auditorCount","1");
        flowEngine.submitOpt(nodeInstId,"u0000000","",null,request.getServletContext());
        JsonResultUtils.writeBlankJson(response);
    }

    @RequestMapping("/doApproval/{flowInstId}/{nodeInstId}")
    public void doApproval(HttpServletRequest request, HttpServletResponse response, @PathVariable Long flowInstId, @PathVariable Long nodeInstId){
        ApprovalEvent approvalEvent = new ApprovalEvent();
        approvalEvent.setEventTitle("一审");
        approvalEvent.setEventDesc("出差报销，100元");
        approvalEvent.setRequestTime(new Date());
        approvalEvent.setApprovalState("A");
        approvalEvent.setCurrentPhase("0");
        List<ApprovalAuditor> approvalAuditors =new ArrayList<>();
        ApprovalAuditor approvalAuditor = new ApprovalAuditor();
        approvalAuditor.setPhraseNo("0");
        approvalAuditor.setUserCode("u0000000");
        approvalAuditors.add(approvalAuditor);
        ApprovalProcess approvalProcess = new ApprovalProcess();
        approvalProcess.setAuditResult("Y");
        approvalProcess.setUserCode("u0000000");
        //flowEngine.saveFlowVariable(flowInstId,"pass","0");
        approvalService.doApproval(approvalEvent,approvalAuditors,approvalProcess,flowInstId,nodeInstId,request.getServletContext());
        JsonResultUtils.writeBlankJson(response);
    }
}
