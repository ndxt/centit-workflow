package com.centit.workflow.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.support.LocalBeanNodeEventSupport;
import com.centit.workflow.support.RemoteBeanNodeEventSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
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

    public static NodeEventSupport createNodeEventSupportBean(NodeInfo nodeInfo, FlowEngine flowEngine) {
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        HttpServletRequest request = RequestThreadLocal.getLocalThreadWrapperRequest();
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        IOsInfo osInfo = StringUtils.isBlank(nodeInfo.getOsId()) ?
            null : CodeRepositoryUtil.getOsInfo(topUnit, nodeInfo.getOsId());

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
