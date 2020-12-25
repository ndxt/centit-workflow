package com.centit.workflow.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.compiler.Lexer;
import com.centit.support.compiler.Pretreatment;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import com.centit.support.network.UrlOptUtils;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.dao.OptVariableDefineDao;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.impl.FlowVariableTranslate;
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
    private FlowOptPage optPage;
    private NodeInfo nodeInfo;
    private FlowVariableTranslate varTrans;
    private OptVariableDefineDao optVariableDefineDao;
    private FlowEngine flowEngine;

    public AutoRunNodeEventSupport(FlowOptPage optPage, NodeInfo nodeInfo,
                                   FlowVariableTranslate varTrans,
                                   FlowEngine flowEngine,
                                   OptVariableDefineDao optVariableDefineDao){
        this.optPage = optPage;
        this.nodeInfo = nodeInfo;
        this.varTrans = varTrans;
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
        String httpUrl = optPage.getPageUrl();
        if(StringUtils.isNotBlank(flowInst.getFlowOptTag())){
            if(flowInst.getFlowOptTag().indexOf("&")>0){
                httpUrl = UrlOptUtils.appendParamToUrl(optPage.getPageUrl(), flowInst.getFlowOptTag());
            } if(Lexer.getFirstWord(flowInst.getFlowOptTag()).equals("{")){
                params.putAll(JSON.parseObject(flowInst.getFlowOptTag()));
            } else {
                params.put("optTag", flowInst.getFlowOptTag());
            }
        }

        String httpRet = null;
        try {
            String optMethod = optPage.getOptMethod();
            String pageParams = Pretreatment.mapTemplateString(optPage.getRequestParams(), varTrans);
            String nodeParams = Pretreatment.mapTemplateString(nodeInfo.getOptParam(), varTrans);

            if(StringUtils.isNotBlank(pageParams)){
                if("{".equals(Lexer.getFirstWord(pageParams))){
                    Object object = JSON.parseObject(pageParams);
                    if (object instanceof Map) {
                        params.putAll((Map<String, Object>) object);
                    } else {
                        httpUrl = UrlOptUtils.appendParamToUrl(httpUrl, pageParams);
                    }
                } else {
                    httpUrl = UrlOptUtils.appendParamToUrl(httpUrl, pageParams);
                }
            }

            if(StringUtils.isNotBlank(nodeParams)){
                if("{".equals(Lexer.getFirstWord(nodeParams))){
                    Object object = JSON.parseObject(nodeParams);
                    if (object instanceof Map) {
                        params.putAll((Map<String, Object>) object);
                    } else {
                        httpUrl = UrlOptUtils.appendParamToUrl(httpUrl, nodeParams);
                    }
                } else {
                    httpUrl = UrlOptUtils.appendParamToUrl(httpUrl, nodeParams);
                }
            }

            httpUrl = UrlOptUtils.appendParamsToUrl(httpUrl, params);

            if (StringUtils.isBlank(optMethod) || "R".equalsIgnoreCase(optMethod) || "GET".equalsIgnoreCase(optMethod)) {
                httpRet = HttpExecutor.simpleGet(HttpExecutorContext.create(), httpUrl);
            } else if ("C".equalsIgnoreCase(optMethod) || "POST".equalsIgnoreCase(optMethod)) {
                httpRet = HttpExecutor.jsonPost(HttpExecutorContext.create(), httpUrl,
                    Pretreatment.mapTemplateString(optPage.getRequestBody(), varTrans));
            } else if ("U".equalsIgnoreCase(optMethod) || "PUT".equalsIgnoreCase(optMethod)) {
                httpRet = HttpExecutor.jsonPut(HttpExecutorContext.create(),httpUrl,
                    Pretreatment.mapTemplateString(optPage.getRequestBody(), varTrans));
            } else if ("D".equalsIgnoreCase(optMethod) || "delete".equalsIgnoreCase(optMethod)) {
               httpRet = HttpExecutor.simpleDelete(HttpExecutorContext.create(), httpUrl);
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
                List<OptVariableDefine> variables =
                    optVariableDefineDao.listOptVariableByFlowCode(
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
