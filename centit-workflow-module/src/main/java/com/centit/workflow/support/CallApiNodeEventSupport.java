package com.centit.workflow.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.dde.adapter.DdeDubboTaskRun;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.compiler.Lexer;
import com.centit.support.compiler.VariableFormula;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.impl.FlowVariableTranslate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author codefan
 * 2013-7-10
 */
public class CallApiNodeEventSupport implements NodeEventSupport {

    private static Logger logger = LoggerFactory.getLogger(CallApiNodeEventSupport.class);
    private FlowVariableTranslate varTrans;
    private DdeDubboTaskRun ddeDubboTaskRun;

    public CallApiNodeEventSupport(FlowVariableTranslate varTrans, DdeDubboTaskRun ddeDubboTaskRun) {
        this.varTrans = varTrans;
        this.ddeDubboTaskRun = ddeDubboTaskRun;
    }

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode) {
        throw new ObjectException(ObjectException.FUNCTION_NOT_SUPPORT, "Call API不支持afterCreate事件！");
    }

    @Override
    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode) {
        throw new ObjectException(ObjectException.FUNCTION_NOT_SUPPORT, "Call API不支持beforeSubmit事件！");
    }

    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode) {
        if (StringUtils.isBlank(nodeInfo.getOptCode())) {
            throw new ObjectException(ObjectException.DATA_NOT_FOUND_EXCEPTION,
                "配置信息有误，接口调用自动运行节点没有正确配置接口id:" + nodeInfo.getNodeId());
        }
        Map<String, Object> params = CollectionsOpt.createHashMap(
            "flowInstId", flowInst.getFlowInstId(),
            "nodeInstId", nodeInst.getNodeInstId(),
            "preInstId",flowInst.getPreInstId(),
            "preNodeInstId", flowInst.getPreNodeInstId(),
            "userCode", optUserCode);
        if (StringUtils.isNotBlank(flowInst.getFlowOptTag())) {
            if ("{".equals(Lexer.getFirstWord(flowInst.getFlowOptTag()))) {
                params.putAll(JSON.parseObject(flowInst.getFlowOptTag()));
            } else {
                params.put("optTag", flowInst.getFlowOptTag());
            }
        }
        /* Pretreatment.mapTemplateString(nodeInfo.getOptParam(), varTrans);
           这个转换有问题，不能输入{}号。
        */
        String nodeParams = nodeInfo.getOptParam();
        if (StringUtils.isNotBlank(nodeParams)) {
            if (!"{".equals(Lexer.getFirstWord(nodeParams))) {
                nodeParams = "{ " + nodeParams + " }";
            }
            JSONObject paramJson = JSONObject.parseObject(nodeParams);
            if (paramJson != null) {
                // 添加对变量的支持
                for (Map.Entry<String, Object> ent : paramJson.entrySet()) {
                    if (ent.getValue() instanceof String) {
                        Object objValue = VariableFormula.calculate((String) ent.getValue(), varTrans);
                        if (objValue != null) {
                            params.put(ent.getKey(), objValue);
                        } else {
                            params.put(ent.getKey(), ent.getValue());
                        }
                    } else {
                        params.put(ent.getKey(), ent.getValue());
                    }
                }
            }
        }
        logger.info("自动运行api网关" + nodeInfo.getOptCode() + "，参数:" + params);
        try {
            ddeDubboTaskRun.runTask(nodeInfo.getOptCode(), params);
            return true;
        } catch (Exception e) {
            throw new ObjectException(WorkflowException.AutoRunNodeDubboError,"api自动运行节点 " + nodeInst.getNodeInstId() + "出错." + e.getMessage());
        }
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode) {
        return true;
    }

}
