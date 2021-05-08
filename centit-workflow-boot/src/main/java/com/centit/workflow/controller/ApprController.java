package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.impl.ObjectUserUnitVariableTranslate;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.workflow.commons.CreateFlowOptions;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.OptIdeaInfo;
import com.centit.workflow.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author liu_cc
 * @create 2021-05-08 10:50
 */
@Controller
@Api(value = "审批流接口", tags = "审批流接口类")
@RequestMapping("/flow/appr")
@Slf4j
public class ApprController {

    @Autowired
    private FlowEngine flowEngine;

    @Autowired
    private FlowManager flowManager;

    @Autowired
    private FlowDefine flowDefine;

    @Autowired
    private FlowOptService flowOptService;

    @Autowired
    private OptIdeaInfoService optIdeaInfoService;

    @ApiOperation(value = "创建流程", notes = "审批流接口创建流程，同时保存办理记录")
    @WrapUpResponseBody
    @PostMapping(value = "/createInstance")
    public FlowInstance createFlowInstDefault(@RequestBody JSONObject json, HttpServletRequest request) {
        CreateFlowOptions newFlowInstanceOptions = json.toJavaObject(CreateFlowOptions.class);
        FlowInstance flowInstance = flowEngine.createInstance(newFlowInstanceOptions,
            new ObjectUserUnitVariableTranslate(
                BaseController.collectRequestParameters(request)), null);

        // 保存办理记录
        OptIdeaInfo optIdeaInfo = json.toJavaObject(OptIdeaInfo.class);
        optIdeaInfo.setNodeInstId(flowInstance.getFirstNodeInstance().getNodeInstId());
        optIdeaInfo.setFlowInstId(flowInstance.getFlowInstId());
        optIdeaInfoService.saveOptIdeaInfo(optIdeaInfo);
        return flowInstance;
    }

    @ApiOperation(value = "提交节点", notes = "审批流接口提交节点，同时保存办理记录")
    @ApiImplicitParam(name = "json", paramType = "body",
        value = "{'nodeInstId':10,'userCode':'u1','unitCode':'d1','varTrans':'jsonString,可不填'}",
        examples = @Example({
            @ExampleProperty(value = "{'nodeInstId':10,'userCode':'u1','unitCode':'d1','varTrans':'jsonString,可不填'}",
                mediaType = "application/json")
        }))
    @WrapUpResponseBody
    @PostMapping(value = "/submitOpt")
    public Map<String, Object> submitOpt(@RequestBody JSONObject json, HttpServletRequest request) {
        SubmitOptOptions options = json.toJavaObject(SubmitOptOptions.class);
        List<String> nextNodeInstList = flowEngine.submitOpt(options, new ObjectUserUnitVariableTranslate(
            BaseController.collectRequestParameters(request)), null);
        // 返回提交后节点的名称
        Set<String> nodeNames = new HashSet<>();
        String flowInstId = null;
        for (String nodeInstId : nextNodeInstList) {
            flowInstId = flowEngine.getNodeInstById(nodeInstId).getFlowInstId();
            NodeInfo nodeInfo = flowEngine.getNodeInfo(nodeInstId);
            if (nodeInfo != null) {
                nodeNames.add(nodeInfo.getNodeName());
            }
        }
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("nextNodeInsts", nextNodeInstList);
        resultMap.put("nodeNames", StringUtils.join(nodeNames, ","));

        // 保存办理记录
        OptIdeaInfo optIdeaInfo = json.toJavaObject(OptIdeaInfo.class);
        optIdeaInfo.setFlowInstId(flowInstId);
        optIdeaInfoService.saveOptIdeaInfo(optIdeaInfo);
        return resultMap;
    }

    @ApiOperation(value = "终止流程实例", notes = "终止流程实例，同时保存办理记录")
    @WrapUpResponseBody
    @RequestMapping(value = "/stopinst/{flowInstId}", method = RequestMethod.PUT)
    public void stopInstance(@PathVariable String flowInstId, @RequestBody JSONObject json,
                             HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (userCode.isEmpty()) {
            userCode = json.getString("userCode");
        }
        flowManager.stopInstance(flowInstId, userCode, "");

        // 保存办理记录
        OptIdeaInfo optIdeaInfo = json.toJavaObject(OptIdeaInfo.class);
        if (optIdeaInfo.getNodeInstId().isEmpty()) {
            optIdeaInfo.setNodeInstId("0");
        }
        optIdeaInfo.setFlowInstId(flowInstId);
        optIdeaInfoService.saveOptIdeaInfo(optIdeaInfo);
    }

    @ApiOperation(value = "回退节点", notes = "回退节点，同时保存办理记录")
    @WrapUpResponseBody
    @PostMapping(value = "/rollBackNode")
    public String rollBackNode(@RequestBody JSONObject jsonObject) {
        String nodeInstId = jsonObject.getString("nodeInstId");
        String managerUserCode = jsonObject.getString("managerUserCode");
        String lastNodeInstId = flowEngine.rollBackNode(nodeInstId, managerUserCode);

        // 保存办理记录
        OptIdeaInfo optIdeaInfo = jsonObject.toJavaObject(OptIdeaInfo.class);
        optIdeaInfoService.saveOptIdeaInfo(optIdeaInfo);
        optIdeaInfo.setFlowInstId(flowEngine.getNodeInstById(nodeInstId).getFlowInstId());
        return lastNodeInstId;
    }
}
