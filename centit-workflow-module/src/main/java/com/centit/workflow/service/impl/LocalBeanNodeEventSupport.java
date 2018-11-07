package com.centit.workflow.service.impl;

import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * 
 * TODO 节点事件接口
 * 
 * @author codefan
 * @create 2013-7-10
 * @version
 */
public class LocalBeanNodeEventSupport implements NodeEventSupport {

    private static Logger logger = LoggerFactory.getLogger(LocalBeanNodeEventSupport.class);
    private  ServletContext application;

    public void setApplication(ServletContext application) {
        this.application = application;
    }

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode)
            throws WorkflowException {
        if( nodeInfo.getOptBean()==null || "".equals(nodeInfo.getOptBean()))
            return;

        if(application==null)
            throw new WorkflowException(WorkflowException.AutoRunNodeWithoutApplcationContent,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，传递的参数application为空");
        try{
            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);//获取spring的context
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean() );
            autoRun.runAfterCreate(flowInst, nodeInst, nodeInfo,optUserCode);
        }catch(BeansException e){
            logger.error("自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());
            throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());
        }

    }

    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                       NodeInfo nodeInfo, String optUserCode) throws WorkflowException {

        if( nodeInfo.getOptBean()==null || "".equals(nodeInfo.getOptBean()))
            return;

        if(application==null)
            throw new WorkflowException(WorkflowException.AutoRunNodeWithoutApplcationContent,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，传递的参数application为空");
        try{
            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);//获取spring的context
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean() );
            autoRun.runBeforeSubmit(flowInst, nodeInst, nodeInfo,optUserCode);
        }catch(BeansException e){
            logger.error("自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());
            throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());
        }

    }

    /**
     *
     * @param flowInst
     * @param nodeInst
     * @param nodeInfo
     * @param optUserCode
     * @return
     */
    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                          NodeInfo nodeInfo, String optUserCode )
            throws WorkflowException {
        if( nodeInfo.getOptBean()==null || "".equals(nodeInfo.getOptBean()))
            throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，流程设置时没有设置节点的自动运行bean属性。");
        if(application==null)
            throw new WorkflowException(WorkflowException.AutoRunNodeWithoutApplcationContent,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，传递的参数application为空");
        try{
            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);//获取spring的context
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean() );
            return autoRun.runAutoOperator(flowInst, nodeInst, nodeInfo,optUserCode);
        }catch(BeansException e){
            logger.error("自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());
            throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());
        }
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode) throws WorkflowException {

        if( nodeInfo.getOptBean()==null || "".equals(nodeInfo.getOptBean()))
            throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，流程设置时没有设置节点的自动运行bean属性。");

        if(application==null)
            throw new WorkflowException(WorkflowException.AutoRunNodeWithoutApplcationContent,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，传递的参数application为空");
        try{
            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);//获取spring的context
            NodeEventSupport autoRun = (NodeEventSupport) wac.getBean(nodeInfo.getOptBean() );
            return autoRun.canStepToNext(flowInst, nodeInst, nodeInfo,optUserCode);
        }catch(BeansException e){
            logger.error("自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());
            throw new WorkflowException(WorkflowException.AutoRunNodeBeanNotFound,
                    "自动运行节点 " + nodeInst.getNodeInstId() +"出错，可能是bean:"+nodeInfo.getOptBean()+ " 找不到 。" +e.getMessage());
        }
    }
}
