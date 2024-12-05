package com.centit.workflow.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.support.ApplicationContextRegister;
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

    public static NodeEventSupport createNodeEventSupportBean(String topUnit, NodeInfo nodeInfo, FlowEngine flowEngine) {
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        OsInfo osInfo = StringUtils.isBlank(nodeInfo.getOsId()) ?
            null : CodeRepositoryUtil.getOsInfo(topUnit, nodeInfo.getOsId());

        if (osInfo != null && StringUtils.isNotBlank(osInfo.getOsUrl())) {
            RemoteBeanNodeEventSupport remoteNodeEventExecutor =
                new RemoteBeanNodeEventSupport(flowEngine);
            remoteNodeEventExecutor.setAppSession(fetchAppSession(osInfo.getOsUrl()));
            return remoteNodeEventExecutor;
        } else {
            LocalBeanNodeEventSupport localNodeEventExecutor =
                new LocalBeanNodeEventSupport(flowEngine);
            if (webApplicationContext == null) {
                webApplicationContext = (WebApplicationContext) ApplicationContextRegister.getApplicationContext();
            }
            localNodeEventExecutor.setApplication(webApplicationContext.getServletContext());
            return localNodeEventExecutor;
        }
    }
}
