package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.centit.framework.appclient.AppSession;
import com.centit.support.network.HttpExecutor;
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
        Map<String,Object> paramMap = new HashMap<>();
        String s = JSON.toJSONString(flowInst);
        FlowInstance flowInstance = JSON.parseObject(s,FlowInstance.class);
        paramMap.put("flowInst",s);
        paramMap.put("nodeInst",nodeInst);
        paramMap.put("nodeInfo",nodeInfo);
        paramMap.put("optUserCode",optUserCode);
        String jsonParam = JSON.toJSONString(paramMap);
        try {
            AppSession appSession = new AppSession(url,false,null,null);
            CloseableHttpClient httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            String result =  HttpExecutor.jsonPost(httpClient,appSession.completeQueryUrl("/service/eventBean/runAfterCreate"),jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程Bean失败");
        }

    }

    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                       NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        if( nodeInfo.getOptBean()==null || "".equals(nodeInfo.getOptBean()))
            return;
        String optBeanUrl = url + "/" + nodeInfo.getOptBean();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInst",flowInst);
        paramMap.put("nodeInst",nodeInst);
        paramMap.put("nodeInfo",nodeInfo);
        paramMap.put("optUserCode",optUserCode);
        paramMap.put("beanName",nodeInfo.getOptBean());
        String jsonParam = JSON.toJSONString(flowInst);
        try {
            AppSession appSession = new AppSession(url,false,null,null);
            CloseableHttpClient httpClient = appSession.getHttpClient();
            appSession.checkAccessToken(httpClient);
            String result =  HttpExecutor.jsonPost(httpClient,appSession.completeQueryUrl("/service/eventBean/runBeforeSubmit"),jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程Bean失败");
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
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("flowInst",flowInst);
            paramMap.put("nodeInst",nodeInst);
            paramMap.put("nodeInfo",nodeInfo);
            paramMap.put("optUserCode",optUserCode);
            paramMap.put("beanName",nodeInfo.getOptBean());
            String jsonParam = JSON.toJSONString(flowInst);
            String result =  HttpExecutor.jsonPost(httpClient,appSession.completeQueryUrl("/service/eventBean/runAutoOperator"),jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程Bean失败");
        }
        return needSubmit;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        return false;
    }
}
