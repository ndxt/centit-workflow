package com.centit.workflow.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.po.FlowOptPage;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.support.AutoRunNodeEventSupport;
import com.centit.workflow.support.LocalBeanNodeEventSupport;
import com.centit.workflow.support.RemoteBeanNodeEventSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据 OSID
 * @author codefan
 * @create 2013-7-10
 */
public class NodeEventSupportFactory {

    private static IntegrationEnvironment integrationEnvironment = null;
    private static ConcurrentHashMap<String, AppSession> appSessionPoolMap = new ConcurrentHashMap<>(10);

    /*public static class DummyNodeEventSupport implements NodeEventSupport{

        @Override
        public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst, NodeInfo nodeInfo, String optUserCode) throws WorkflowException {

        }

        @Override
        public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst, NodeInfo nodeInfo, String optUserCode) throws WorkflowException {

        }

        @Override
        public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst, NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
            return true;
        }

        @Override
        public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst, NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
            return true;
        }
    }
    */
    private static AppSession fetchAppSession(String url){
        String sUrl = StringUtils.isBlank(url)? "blank": url;
        AppSession appSession = appSessionPoolMap.get(sUrl);
        if(appSession == null){
            appSession =  new AppSession(url, false, null, null);
            appSessionPoolMap.put(sUrl, appSession);
        }
        return appSession;
    }

    public static NodeEventSupport createNodeEventSupportBean(NodeInfo nodeInfo,
                                                              FlowOptPage optPage,
                                                              FlowEngine flowEngine) {
        if (NodeInfo.AUTO_NODE_OPT_CODE_CALL.equals(nodeInfo.getAutoRunType())) {
            return new AutoRunNodeEventSupport(
                optPage.getPageUrl(),
                nodeInfo.getOptParam(),
                optPage.getOptMethod(),
                flowEngine);
        }
        // 添加一个发送消息的
        return createNodeEventSupportBean(nodeInfo, flowEngine);
    }

    public static NodeEventSupport createNodeEventSupportBean(NodeInfo nodeInfo, FlowEngine flowEngine) {
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        if (integrationEnvironment == null) {
            integrationEnvironment = (IntegrationEnvironment) webApplicationContext.getBean("integrationEnvironment");
        }

        OsInfo osInfo = StringUtils.isBlank(nodeInfo.getOsId()) ?
            null : integrationEnvironment.getOsInfo(nodeInfo.getOsId());

        if (osInfo != null && StringUtils.isNotBlank(osInfo.getOsUrl())) {
            RemoteBeanNodeEventSupport remoteNodeEventExecutor =
                new RemoteBeanNodeEventSupport(flowEngine);
            remoteNodeEventExecutor.setAppSession(fetchAppSession(osInfo.getOsUrl()));
            return remoteNodeEventExecutor;
        } else {
            LocalBeanNodeEventSupport localNodeEventExecutor =
                new LocalBeanNodeEventSupport(flowEngine);
            localNodeEventExecutor.setApplication(webApplicationContext.getServletContext());
            return localNodeEventExecutor;
        }
    }
}
