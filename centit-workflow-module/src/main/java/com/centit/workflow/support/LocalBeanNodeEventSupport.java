package com.centit.workflow.support;

import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * @author codefan
 * create 2013-7-10
 * @version v4.5
 */
public class LocalBeanNodeEventSupport implements NodeEventSupport {

    private static Logger logger = LoggerFactory.getLogger(LocalBeanNodeEventSupport.class);
    private  ServletContext application;
    private FlowEngine flowEngine;
    public LocalBeanNodeEventSupport(FlowEngine flowEngine){
        this.flowEngine = flowEngine;
    }
    public void setApplication(ServletContext application) {
        this.application = application;
    }

    private boolean checkRunCondition(NodeInfo nodeInfo, NodeInstance nodeInst){
        if(StringUtils.isBlank(nodeInfo.getOptBean())){
            return false;
        }
       /* throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
                "自动运行节点 " + nodeInst.getNodeInstId() +"出错，流程设置时没有设置节点的自动运行bean属性。");*/
        if(application==null)
            throw new WorkflowException(WorkflowException.AutoRunNodeWithoutApplcationContent,
                "自动运行节点 " + nodeInst.getNodeInstId() +"出错，传递的参数application为空");
        return true;
    }

    private  void logError(BeansException e, NodeInfo nodeInfo, NodeInstance nodeInst){
        logger.error("自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());
        throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
            "自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());

    }


    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode)
            throws WorkflowException {
        if(!checkRunCondition(nodeInfo, nodeInst)){
            return;
        }
        try{
            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);//获取spring的context
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean());
            autoRun.runAfterCreate(flowInst, nodeInst, nodeInfo,optUserCode);
        }catch(BeansException e){
            logError(e, nodeInfo, nodeInst);
        }

    }

    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                       NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        if(!checkRunCondition(nodeInfo, nodeInst)){
            return;
        }
        try{
            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);//获取spring的context
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean());
            autoRun.runBeforeSubmit(flowInst, nodeInst, nodeInfo,optUserCode);
        }catch(BeansException e){
            logError(e, nodeInfo, nodeInst);
        }

    }


    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                          NodeInfo nodeInfo, String optUserCode )
            throws WorkflowException {
        if(!checkRunCondition(nodeInfo, nodeInst)){
            throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
                "自动运行节点 " + nodeInst.getNodeInstId() +"出错，流程设置时没有设置节点的自动运行bean属性。");
        }
        try{
            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);//获取spring的context
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean() );
            return autoRun.runAutoOperator(flowInst, nodeInst, nodeInfo,optUserCode);
        }catch(BeansException e){
            logError(e, nodeInfo, nodeInst);
            return false;
        }
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        if(!checkRunCondition(nodeInfo, nodeInst)){
            throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
                "条件判断节点 " + nodeInst.getNodeInstId() +"出错，流程设置时没有设置条件判断的bean属性。");
        }
        try{
            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);//获取spring的context
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean() );
            return autoRun.canStepToNext(flowInst, nodeInst, nodeInfo,optUserCode);
        }catch(BeansException e){
            logError(e, nodeInfo, nodeInst);
            return false;
        }
    }
}
