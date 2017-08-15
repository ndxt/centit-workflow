package com.centit.workflow.controller;

import com.centit.workflow.client.commons.NodeEventSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Controller
@RequestMapping("/eventBean/{beanName}/{methodName}")
public class  EventBeanController{
    public void beanExecute(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse, @PathVariable String beanName,@PathVariable String methodName){
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());//获取spring的context
        NodeEventSupport autoRun = (NodeEventSupport)wac.getBean(beanName);
        //needSubmit = autoRun.runAutoOperator(flowInst, nodeInst, nodeInfo.getOptParam(),optUserCode);
    }
}
