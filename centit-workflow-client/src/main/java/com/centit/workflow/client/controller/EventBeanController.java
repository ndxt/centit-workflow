package com.centit.workflow.client.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.support.common.ObjectException;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Controller
@RequestMapping("/workflowEventBean")
public class EventBeanController extends BaseController {

    public static final Logger logger = LoggerFactory.getLogger(EventBeanController.class);

    @RequestMapping(value = "/runAfterCreate")
    @WrapUpResponseBody
    public void runAfterCreate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                               @RequestBody JSONObject paramMap) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());//获取spring的context
        try {
            FlowInstance flowInstance = paramMap.getObject("flowInst", FlowInstance.class);
            NodeInstance nodeInstance = paramMap.getObject("nodeInst", NodeInstance.class);
            NodeInfo nodeInfo = paramMap.getObject("nodeInfo", NodeInfo.class);
            String optUserCode = paramMap.getString("optUserCode");
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean());
            autoRun.runAfterCreate(flowInstance, nodeInstance, nodeInfo, optUserCode);
        } catch (BeansException | WorkflowException e) {
            logger.error("bean调用失败");
            //JsonResultUtils.writeMessageJson("bean调用失败", httpServletResponse);
            throw new ObjectException("bean调用失败", e);
        }

    }

    @RequestMapping(value = "/runBeforeSubmit")
    @WrapUpResponseBody
    public void runBeforeSubmit(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                @RequestBody JSONObject paramMap) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());//获取spring的context
        try {
            FlowInstance flowInstance = paramMap.getObject("flowInst", FlowInstance.class);
            NodeInstance nodeInstance = paramMap.getObject("nodeInst", NodeInstance.class);
            NodeInfo nodeInfo = paramMap.getObject("nodeInfo", NodeInfo.class);
            String optUserCode = paramMap.getString("optUserCode");
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean());
            autoRun.runBeforeSubmit(flowInstance, nodeInstance, nodeInfo, optUserCode);
        } catch (BeansException | WorkflowException e) {
            //e.printStackTrace();
            logger.error("bean调用失败");
            //JsonResultUtils.writeMessageJson("bean调用失败", httpServletResponse);
            throw new ObjectException("bean调用失败", e);
        }
        //JsonResultUtils.writeMessageJson("bean调用成功", httpServletResponse);
    }

    @RequestMapping(value = "/runAutoOperator")
    @WrapUpResponseBody
    public boolean runAutoOperator(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                @RequestBody JSONObject paramMap) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());//获取spring的context
        try {
            FlowInstance flowInstance = paramMap.getObject("flowInst", FlowInstance.class);
            NodeInstance nodeInstance = paramMap.getObject("nodeInst", NodeInstance.class);
            NodeInfo nodeInfo = paramMap.getObject("nodeInfo", NodeInfo.class);
            String optUserCode = paramMap.getString("optUserCode");
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean());
            return autoRun.runAutoOperator(flowInstance, nodeInstance, nodeInfo, optUserCode);
        } catch (BeansException | WorkflowException e) {
            //e.printStackTrace();
            //JsonResultUtils.writeMessageJson("bean调用失败", httpServletResponse);
            logger.error("bean调用失败");
            return false;
        }

    }

    @RequestMapping(value = "/canStepToNext")
    @WrapUpResponseBody
    public boolean canStepToNext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                              @RequestBody JSONObject paramMap) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());//获取spring的context
        try {
            FlowInstance flowInstance = paramMap.getObject("flowInst", FlowInstance.class);
            NodeInstance nodeInstance = paramMap.getObject("nodeInst", NodeInstance.class);
            NodeInfo nodeInfo = paramMap.getObject("nodeInfo", NodeInfo.class);
            String optUserCode = paramMap.getString("optUserCode");
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean());
            return autoRun.canStepToNext(flowInstance, nodeInstance, nodeInfo, optUserCode);
        } catch (BeansException | WorkflowException e) {
            //e.printStackTrace();
            logger.error("bean调用失败");
            return false;
        }
        //JsonResultUtils.writeMessageJson("bean调用成功", httpServletResponse);
    }
}
