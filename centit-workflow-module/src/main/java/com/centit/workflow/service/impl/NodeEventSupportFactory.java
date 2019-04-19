package com.centit.workflow.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.po.NodeInfo;
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

    private static AppSession fetchAppSession(String url){
        String sUrl = StringUtils.isBlank(url)? "blank": url;
        AppSession appSession = appSessionPoolMap.get(sUrl);
        if(appSession == null){
            appSession =  new AppSession(url, false, null, null);
            appSessionPoolMap.put(sUrl, appSession);
        }
        return appSession;
    }

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
            remoteNodeEventExecutor.setAppSession(fetchAppSession(osInfo.getOsUrl()));
            return remoteNodeEventExecutor;
        } else {
            LocalBeanNodeEventSupport localNodeEventExecutor = new LocalBeanNodeEventSupport();
            localNodeEventExecutor.setApplication(webApplicationContext.getServletContext());
            return localNodeEventExecutor;
        }
    }
}
