package com.centit.workflow.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.support.network.HttpExecutor;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TODO 节点事件接口
 * 
 * @author codefan
 * @create 2013-7-10
 * @version
 */
public class RemoteBeanNodeEventSupport implements NodeEventExecutor{

    private static Logger logger = LoggerFactory.getLogger(RemoteBeanNodeEventSupport.class);
    private  String url;

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode)
            throws WorkflowException {
        if( nodeInfo.getOptBean()==null || "".equals(nodeInfo.getOptBean()))
            return;
        String optBeanUrl = url + "/" + nodeInfo.getOptBean();
        try {
            AppSession appSession = new AppSession(url,false,null,null);
            CloseableHttpClient httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            String result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/"+ nodeInfo.getOptBean()),null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                       NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        if( nodeInfo.getOptBean()==null || "".equals(nodeInfo.getOptBean()))
            return;
        String optBeanUrl = url + "/" + nodeInfo.getOptBean();
        try {
            AppSession appSession = new AppSession(url,false,null,null);
            CloseableHttpClient httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            String result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/"+ nodeInfo.getOptBean()),null);
        } catch (Exception e) {
            e.printStackTrace();
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
        boolean needSubmit = true;
        String optBeanUrl = url + "/" + nodeInfo.getOptBean();
        try {
            AppSession appSession = new AppSession(url,false,null,null);
            CloseableHttpClient httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            String result =  HttpExecutor.formPost(httpClient,appSession.completeQueryUrl("/"+ nodeInfo.getOptBean()),null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return needSubmit;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        return false;
    }
}
