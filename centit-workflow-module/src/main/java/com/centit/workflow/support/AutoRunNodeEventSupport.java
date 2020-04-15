package com.centit.workflow.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import com.centit.support.network.UrlOptUtils;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author codefan
 * 2013-7-10
 */
public class AutoRunNodeEventSupport implements NodeEventSupport {

    private static Logger logger = LoggerFactory.getLogger(AutoRunNodeEventSupport.class);
    private String optUrl;
    private String optParam;
    private String optMethod;

    public AutoRunNodeEventSupport(String optUrl, String optParam, String optMethod){
        this.optUrl = optUrl;
        this.optParam = optParam;
        this.optMethod = optMethod;
    }

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode)
        throws WorkflowException {
    }

    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
    }

    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode)
        throws WorkflowException {
        Map<String, Object> params = CollectionsOpt.createHashMap(
            "flowInstId", nodeInst.getFlowInstId(),
            "nodeInstId", nodeInst.getNodeInstId(),
            "userCode", optUserCode);
        try {
            if ("C".equalsIgnoreCase(optMethod) || "POST".equalsIgnoreCase(optMethod)) {
                Object paramMap = JSON.parse(optParam);
                if(paramMap instanceof JSONObject){
                    params.putAll((JSONObject)paramMap);
                    HttpExecutor.jsonPost(HttpExecutorContext.create(),optUrl, params);
                } else {
                    HttpExecutor.jsonPost(HttpExecutorContext.create(),
                        UrlOptUtils.appendParamToUrl(optUrl, optParam), params);
                }
            } else if ("U".equalsIgnoreCase(optMethod) || "PUT".equalsIgnoreCase(optMethod)) {
                Object paramMap = JSON.parse(optParam);
                if(paramMap instanceof JSONObject){
                    params.putAll((JSONObject)paramMap);
                    HttpExecutor.jsonPut(HttpExecutorContext.create(),optUrl, params);
                } else {
                    HttpExecutor.jsonPut(HttpExecutorContext.create(),
                        UrlOptUtils.appendParamToUrl(optUrl, optParam), params);
                }
            } else if ("D".equalsIgnoreCase(optMethod) || "delete".equalsIgnoreCase(optMethod)) {
                Object paramMap = JSON.parse(optParam);
                if(paramMap instanceof JSONObject){
                    params.putAll((JSONObject)paramMap);
                    HttpExecutor.simpleDelete(HttpExecutorContext.create(), optUrl, params);
                } else {
                    HttpExecutor.simpleDelete(HttpExecutorContext.create(),
                        UrlOptUtils.appendParamToUrl(optUrl, optParam),params);
                }
            }
        } catch (IOException e){
            logger.error(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
         return true;
    }


}
