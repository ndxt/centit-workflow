package com.centit.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.workflow.po.ApprovalAuditor;
import com.centit.workflow.po.ApprovalEvent;
import com.centit.workflow.po.ApprovalProcess;
import com.centit.workflow.po.UserTask;
import com.centit.workflow.service.ApprovalService;
import com.centit.workflow.service.FlowEngine;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    @Resource
    private PlatformEnvironment platformEnvironment;
    @RequestMapping(value = "/listAllUser", method = RequestMethod.GET)
    public void listAllUser(HttpServletResponse response){
        Object userList = platformEnvironment.listAllUsers();
        JsonResultUtils.writeSingleDataJson(userList,response);
    }
    @RequestMapping("/startProcess")
    public void startProcess(HttpServletRequest request,HttpServletResponse response,String approvalTitle,String approvalDesc,String approvalAuditors){
        //设置审核人  申请事项等业务数据
        List<ApprovalAuditor> auditors = JSON.parseArray(approvalAuditors,ApprovalAuditor.class);
        if(auditors == null && auditors.size() == 0){
            JsonResultUtils.writeBlankJson(response);
            return;
        }
        ApprovalEvent approvalEvent = new ApprovalEvent();
        approvalEvent.setEventTitle(approvalTitle);
        approvalEvent.setEventDesc(approvalDesc);
        approvalEvent.setRequestTime(new Date());
        approvalEvent.setApprovalState("A");
        approvalEvent.setCurrentPhase("0");
        //开启工作流 提交申请节点
        Long flowInstId = approvalService.startProcess(approvalEvent,auditors,3,auditors.get(0).getUserCode());
        flowEngine.assignFlowWorkTeam(flowInstId,"auditor","u0000000");
        flowEngine.saveFlowVariable(flowInstId,"auditorCount","1");
        List<UserTask> tasks = flowEngine.listUserTasks(auditors.get(0).getUserCode(),new PageDesc(-1,-1));
        if(tasks != null && tasks.size()>0){
            flowEngine.submitOpt(tasks.get(0).getNodeInstId(),"u0000000","",null,request.getServletContext());
        }
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
        approvalAuditor.setPhaseNo("0");
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
