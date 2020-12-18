package com.centit.workflow.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.compiler.Lexer;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import com.centit.support.network.UrlOptUtils;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.dao.OptVariableDefineDao;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.po.OptVariableDefine;
import com.centit.workflow.service.FlowEngine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
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
    private OptVariableDefineDao optVariableDefineDao;
    private FlowEngine flowEngine;

    public AutoRunNodeEventSupport(String optUrl, String optParam, String optMethod,
            FlowEngine flowEngine, OptVariableDefineDao optVariableDefineDao){
        this.optUrl = optUrl;
        this.optParam = optParam;
        this.optMethod = optMethod;
        this.flowEngine = flowEngine;
        this.optVariableDefineDao = optVariableDefineDao;
    }

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode) {
    }

    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode)  {
    }

    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode) {
        Map<String, Object> params = CollectionsOpt.createHashMap(
            "flowInstId", nodeInst.getFlowInstId(),
            "nodeInstId", nodeInst.getNodeInstId(),
            "userCode", optUserCode);
        String httpUrl = optUrl;
        if(StringUtils.isNotBlank(flowInst.getFlowOptTag())){
            if(flowInst.getFlowOptTag().indexOf("&")>0){
                httpUrl = UrlOptUtils.appendParamToUrl(optUrl, flowInst.getFlowOptTag());
            } if(Lexer.getFirstWord(flowInst.getFlowOptTag()).equals("{")){
                params.putAll(JSON.parseObject(flowInst.getFlowOptTag()));
            } else {
                params.put("optTag", flowInst.getFlowOptTag());
            }
        }

        String httpRet = null;
        try {
            Object paramMap = JSON.parse(optParam);
            if (StringUtils.isBlank(optMethod) || "R".equalsIgnoreCase(optMethod) || "GET".equalsIgnoreCase(optMethod)) {
                if(paramMap instanceof JSONObject){
                    params.putAll((JSONObject)paramMap);
                    httpRet = HttpExecutor.simpleGet(HttpExecutorContext.create(), httpUrl, params);
                } else {
                    httpRet = HttpExecutor.simpleGet(HttpExecutorContext.create(),
                        UrlOptUtils.appendParamToUrl(httpUrl, optParam),params);
                }
            } else if ("C".equalsIgnoreCase(optMethod) || "POST".equalsIgnoreCase(optMethod)) {
                if(paramMap instanceof JSONObject){
                    params.putAll((JSONObject)paramMap);
                    httpRet = HttpExecutor.jsonPost(HttpExecutorContext.create(),httpUrl, params);
                } else {
                    httpRet = HttpExecutor.jsonPost(HttpExecutorContext.create(),
                        UrlOptUtils.appendParamToUrl(httpUrl, optParam), params);
                }
            } else if ("U".equalsIgnoreCase(optMethod) || "PUT".equalsIgnoreCase(optMethod)) {
                if(paramMap instanceof JSONObject){
                    params.putAll((JSONObject)paramMap);
                    httpRet = HttpExecutor.jsonPut(HttpExecutorContext.create(),httpUrl, params);
                } else {
                    httpRet = HttpExecutor.jsonPut(HttpExecutorContext.create(),
                        UrlOptUtils.appendParamToUrl(httpUrl, optParam), params);
                }
            } else if ("D".equalsIgnoreCase(optMethod) || "delete".equalsIgnoreCase(optMethod)) {
                if(paramMap instanceof JSONObject){
                    params.putAll((JSONObject)paramMap);
                    httpRet = HttpExecutor.simpleDelete(HttpExecutorContext.create(), httpUrl, params);
                } else {
                    httpRet = HttpExecutor.simpleDelete(HttpExecutorContext.create(),
                        UrlOptUtils.appendParamToUrl(httpUrl, optParam),params);
                }
            }
        } catch (IOException e){
            logger.error(e.getMessage());
        }

        if(StringUtils.isNotBlank(httpRet)){
            HttpReceiveJSON json = HttpReceiveJSON.valueOfJson(httpRet);
            if(json.getCode() != 0){
                logger.error(json.getMessage()+":"+JSON.toJSONString(nodeInst));
                return false;
            }
            // 将返回结果设置为流程变量
            JSONObject jo = json.getJSONObject();
            if(jo != null){
                List<OptVariableDefine> variables = optVariableDefineDao.listOptVariableByFlowCode(
                    flowInst.getFlowCode(), flowInst.getVersion());
                if(variables!= null && variables.size()>0) {
                    for (OptVariableDefine variable : variables) {
                        Object value = jo.get(variable.getVariableName());
                        if (value != null) {
                            flowEngine.saveFlowNodeVariable(nodeInst.getNodeInstId(),
                                variable.getVariableName(), value);
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode){
        return true;
    }

}
