package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.centit.framework.appclient.AppSession;
import com.centit.support.network.HttpExecutor;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.NodeEventExecutor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * TODO 节点事件接口
 * 
 * @author codefan
 * @create 2013-7-10
 * @version
 */
public class RemoteBeanNodeEventSupport implements NodeEventExecutor {

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
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInst",JSON.toJSONString(flowInst));
        paramMap.put("nodeInst",JSON.toJSONString(nodeInst));
        paramMap.put("nodeInfo",JSON.toJSONString(nodeInfo));
        paramMap.put("optUserCode",optUserCode);
        String jsonParam = JSON.toJSONString(paramMap);
        AppSession appSession = null;
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            appSession = new AppSession(url,false,null,null);
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.jsonPost(httpClient,appSession.completeQueryUrl("/service/eventBean/runAfterCreate"),jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程Bean失败");
        }finally {
            if(appSession != null && httpClient != null){
                appSession.releaseHttpClient(httpClient);
            }
        }

    }

    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                       NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        if( nodeInfo.getOptBean()==null || "".equals(nodeInfo.getOptBean()))
            return;
        String optBeanUrl = url + "/" + nodeInfo.getOptBean();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInst",JSON.toJSONString(flowInst));
        paramMap.put("nodeInst",JSON.toJSONString(nodeInst));
        paramMap.put("nodeInfo",JSON.toJSONString(nodeInfo));
        paramMap.put("optUserCode",optUserCode);
        String jsonParam = JSON.toJSONString(paramMap);
        AppSession appSession = null;
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            appSession = new AppSession(url,false,null,null);
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.jsonPost(httpClient,appSession.completeQueryUrl("/service/eventBean/runBeforeSubmit"),jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程Bean失败");
        }finally {
            if(appSession != null && httpClient != null){
                appSession.releaseHttpClient(httpClient);
            }
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
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInst",JSON.toJSONString(flowInst));
        paramMap.put("nodeInst",JSON.toJSONString(nodeInst));
        paramMap.put("nodeInfo",JSON.toJSONString(nodeInfo));
        paramMap.put("optUserCode",optUserCode);
        String jsonParam = JSON.toJSONString(paramMap);
        AppSession appSession = null;
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            appSession = new AppSession(url,false,null,null);
            httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            result =  HttpExecutor.jsonPost(httpClient,appSession.completeQueryUrl("/service/eventBean/runAutoOperator"),jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程Bean失败");
        }finally {
            if(appSession != null && httpClient != null){
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
}
