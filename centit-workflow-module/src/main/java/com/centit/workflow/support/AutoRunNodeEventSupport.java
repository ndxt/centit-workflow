package com.centit.workflow.support;

import com.alibaba.fastjson.JSON;
import com.centit.dde.adapter.DdeDubboTaskRun;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.compiler.Lexer;
import com.centit.support.compiler.Pretreatment;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.dao.OptVariableDefineDao;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowOptPage;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.impl.FlowVariableTranslate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private DdeDubboTaskRun ddeDubboTaskRun;

    public AutoRunNodeEventSupport(FlowOptPage optPage, NodeInfo nodeInfo,
                                   FlowVariableTranslate varTrans,
                                   FlowEngine flowEngine,
                                   OptVariableDefineDao optVariableDefineDao,DdeDubboTaskRun ddeDubboTaskRun) {
        this.optPage = optPage;
        this.nodeInfo = nodeInfo;
        this.varTrans = varTrans;
        this.flowEngine = flowEngine;
        this.optVariableDefineDao = optVariableDefineDao;
        this.ddeDubboTaskRun = ddeDubboTaskRun;
    }

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode) {
    }

    @Override
    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode) {
    }

    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode) {
        Map<String, Object> params = CollectionsOpt.createHashMap(
            "flowInstId", flowInst.getFlowInstId(),
            "nodeInstId", nodeInst.getNodeInstId(),
            "userCode", optUserCode);
        if (StringUtils.isNotBlank(flowInst.getFlowOptTag())) {
            if ("{".equals(Lexer.getFirstWord(flowInst.getFlowOptTag()))) {
                params.putAll(JSON.parseObject(flowInst.getFlowOptTag()));
            } else {
                params.put("optTag", flowInst.getFlowOptTag());
            }
        }
        String nodeParams = Pretreatment.mapTemplateString(nodeInfo.getOptParam(), varTrans);
        if (StringUtils.isNotBlank(nodeParams)) {
            if (!"{".equals(Lexer.getFirstWord(nodeParams))) {
                nodeParams = "{" + nodeParams + "}";
            }
            params.putAll(JSON.parseObject(nodeParams));
        }
        ddeDubboTaskRun.runTask(nodeInfo.getOptCode(), params);
        return true;
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode) {
        return true;
    }

}
