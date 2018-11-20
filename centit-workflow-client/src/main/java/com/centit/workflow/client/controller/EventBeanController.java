package com.centit.workflow.client.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Controller
@RequestMapping("/workflowEventBean")
public class EventBeanController {

    @RequestMapping(value = "/runAfterCreate")
    public void runAfterCreate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody Map<String, Object> paramMap) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());//获取spring的context
        try {
            FlowInstance flowInstance = JSONObject.parseObject(paramMap.get("flowInst").toString(), FlowInstance.class);
            NodeInstance nodeInstance = JSONObject.parseObject(paramMap.get("nodeInst").toString(), NodeInstance.class);
            NodeInfo nodeInfo = JSONObject.parseObject(paramMap.get("nodeInfo").toString(), NodeInfo.class);
            String optUserCode = (String) paramMap.get("optUserCode");
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean());
            autoRun.runAfterCreate(flowInstance, nodeInstance, nodeInfo, optUserCode);
        } catch (BeansException e) {
            e.printStackTrace();
            JsonResultUtils.writeMessageJson("bean调用失败", httpServletResponse);
        } catch (WorkflowException e) {
            e.printStackTrace();
            JsonResultUtils.writeMessageJson("bean调用失败", httpServletResponse);
        }
        JsonResultUtils.writeMessageJson("bean调用成功", httpServletResponse);
    }

    @RequestMapping(value = "/runBeforeSubmit")
    public void runBeforeSubmit(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody Map<String, Object> paramMap) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());//获取spring的context
        try {
            FlowInstance flowInstance = JSONObject.parseObject(paramMap.get("flowInst").toString(), FlowInstance.class);
            NodeInstance nodeInstance = JSONObject.parseObject(paramMap.get("nodeInst").toString(), NodeInstance.class);
            NodeInfo nodeInfo = JSONObject.parseObject(paramMap.get("nodeInfo").toString(), NodeInfo.class);
            String optUserCode = (String) paramMap.get("optUserCode");
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean());
            autoRun.runBeforeSubmit(flowInstance, nodeInstance, nodeInfo, optUserCode);
        } catch (BeansException e) {
            e.printStackTrace();
            JsonResultUtils.writeMessageJson("bean调用失败", httpServletResponse);
        } catch (WorkflowException e) {
            e.printStackTrace();
            JsonResultUtils.writeMessageJson("bean调用失败", httpServletResponse);
        }
        JsonResultUtils.writeMessageJson("bean调用成功", httpServletResponse);
    }

    @RequestMapping(value = "/runAutoOperator")
    public void runAutoOperator(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody Map<String, Object> paramMap) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());//获取spring的context
        try {
            FlowInstance flowInstance = JSONObject.parseObject(paramMap.get("flowInst").toString(), FlowInstance.class);
            NodeInstance nodeInstance = JSONObject.parseObject(paramMap.get("nodeInst").toString(), NodeInstance.class);
            NodeInfo nodeInfo = JSONObject.parseObject(paramMap.get("nodeInfo").toString(), NodeInfo.class);
            String optUserCode = (String) paramMap.get("optUserCode");
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean());
            autoRun.runAutoOperator(flowInstance, nodeInstance, nodeInfo, optUserCode);
        } catch (BeansException e) {
            e.printStackTrace();
            JsonResultUtils.writeMessageJson("bean调用失败", httpServletResponse);
        } catch (WorkflowException e) {
            e.printStackTrace();
            JsonResultUtils.writeMessageJson("bean调用失败", httpServletResponse);
        }
        JsonResultUtils.writeMessageJson("bean调用成功", httpServletResponse);
    }

    @RequestMapping(value = "/canStepToNext")
    public void canStepToNext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody(required = true) Map<String, Object> map) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());//获取spring的context
        //NodeEventSupport autoRun = (NodeEventSupport)wac.getBean(beanName);
        //needSubmit = autoRun.runAutoOperator(flowInst, nodeInst, nodeInfo.getOptParam(),optUserCode);
    }
}
