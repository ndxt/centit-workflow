package com.centit.workflow.service.impl;

import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.po.NodeInfo;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * 根据 OSID
 *
 * @author codefan
 * @create 2013-7-10
 */
public class NodeEventSupportFactory {

    private static IntegrationEnvironment integrationEnvironment = null;

    public static NodeEventSupport getNodeEventSupportBean(NodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return null;
        }

        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        if (integrationEnvironment == null) {
            integrationEnvironment = (IntegrationEnvironment) webApplicationContext.getBean("integrationEnvironment");
        }

        OsInfo osInfo = integrationEnvironment == null ? null : integrationEnvironment.getOsInfo(nodeInfo.getOsId());
        if (osInfo != null) {
            RemoteBeanNodeEventSupport remoteNodeEventExecutor = new RemoteBeanNodeEventSupport();
            remoteNodeEventExecutor.setUrl(osInfo.getOsUrl());
            return remoteNodeEventExecutor;
        } else {
            LocalBeanNodeEventSupport localNodeEventExecutor = new LocalBeanNodeEventSupport();
            localNodeEventExecutor.setApplication(webApplicationContext.getServletContext());
            return localNodeEventExecutor;
        }
    }
}
