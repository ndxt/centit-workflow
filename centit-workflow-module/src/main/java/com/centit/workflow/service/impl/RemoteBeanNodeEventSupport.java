package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.centit.framework.appclient.AppSession;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author codefan
 * 2013-7-10
 */
public class RemoteBeanNodeEventSupport implements NodeEventSupport {

    private static Logger logger = LoggerFactory.getLogger(RemoteBeanNodeEventSupport.class);
    private AppSession appSession ;

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode)
        throws WorkflowException {
        if (nodeInfo.getOptBean() == null || "".equals(nodeInfo.getOptBean()))
            return;
        //String optBeanUrl = url + "/" + nodeInfo.getOptBean();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInst", JSON.toJSONString(flowInst));
        paramMap.put("nodeInst", JSON.toJSONString(nodeInst));
        paramMap.put("nodeInfo", JSON.toJSONString(nodeInfo));
        paramMap.put("optUserCode", optUserCode);
        String jsonParam = JSON.toJSONString(paramMap);
        CloseableHttpClient httpClient = null;
        //String result = null;
        try {
            //appSession = new AppSession(url,false,null,null);
               httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            appSession.jsonPost(httpClient,
                appSession.completeQueryUrl("/workflowEventBean/runAfterCreate"), jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程Bean失败");
        } finally {
            if (appSession != null && httpClient != null) {
                appSession.releaseHttpClient(httpClient);
            }
        }

    }

    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        if (nodeInfo.getOptBean() == null || "".equals(nodeInfo.getOptBean()))
            return;
        //String optBeanUrl = url + "/" + nodeInfo.getOptBean();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInst", JSON.toJSONString(flowInst));
        paramMap.put("nodeInst", JSON.toJSONString(nodeInst));
        paramMap.put("nodeInfo", JSON.toJSONString(nodeInfo));
        paramMap.put("optUserCode", optUserCode);
        String jsonParam = JSON.toJSONString(paramMap);
        CloseableHttpClient httpClient = null;
        //String result = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            /*result =  */
            appSession.jsonPost(httpClient,
                appSession.completeQueryUrl("/workflowEventBean/runBeforeSubmit"), jsonParam);//eventBean
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程Bean失败");
        } finally {
            if (appSession != null && httpClient != null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
    }

    /**
     * @param flowInst FlowInstance
     * @param nodeInst NodeInstance
     * @param nodeInfo NodeInfo
     * @param optUserCode String
     * @return boolean
     */
    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode)
        throws WorkflowException {
        boolean needSubmit = true;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("flowInst", JSON.toJSONString(flowInst));
        paramMap.put("nodeInst", JSON.toJSONString(nodeInst));
        paramMap.put("nodeInfo", JSON.toJSONString(nodeInfo));
        paramMap.put("optUserCode", optUserCode);
        String jsonParam = JSON.toJSONString(paramMap);
        CloseableHttpClient httpClient = null;
        //String result = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            /*result =  */
            appSession.jsonPost(httpClient, appSession.completeQueryUrl("/workflowEventBean/runAutoOperator"), jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程Bean失败");
        } finally {
            if (appSession != null && httpClient != null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
        return needSubmit;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        return false;
    }

    public void setAppSession(AppSession appSession) {
        this.appSession = appSession;
    }
}
