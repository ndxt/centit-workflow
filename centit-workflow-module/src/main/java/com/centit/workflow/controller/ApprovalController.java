package com.centit.workflow.controller;

import com.centit.workflow.po.ApprovalAuditor;
import com.centit.workflow.po.ApprovalEvent;
import com.centit.workflow.service.ApprovalService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Controller
@RequestMapping("/approval")
public class ApprovalController {
    @Resource
    private ApprovalService approvalService;
    @RequestMapping("/saveForm")
    public void saveForm(){
        ApprovalEvent approvalEvent = new ApprovalEvent();
        List<ApprovalAuditor> list = new ArrayList<>();
        approvalService.setAllAuditors(list);
    }
}
