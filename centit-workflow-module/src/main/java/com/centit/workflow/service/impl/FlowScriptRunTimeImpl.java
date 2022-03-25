package com.centit.workflow.service.impl;

import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.common.LeftRightPair;
import com.centit.support.compiler.Lexer;
import com.centit.support.compiler.VariableFormula;
import com.centit.workflow.dao.FlowInstanceDao;
import com.centit.workflow.dao.NodeInstanceDao;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowScriptRunTime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class FlowScriptRunTimeImpl implements FlowScriptRunTime {

    @Autowired
    FlowEngine flowEngine;

    @Autowired
    private FlowInstanceDao flowInstanceDao;

    @Autowired
    private NodeInstanceDao nodeInstanceDao;

    private LeftRightPair<String, Object> fetchFuncStringFormulaParams(Lexer lexer, FlowVariableTranslate varTrans){
        String currWord = lexer.getAWord();
        if (!"(".equals(currWord)) {
            return null;
        }
        String valueName = lexer.getAWord();
        currWord = lexer.getAWord();
        if (!",".equals(currWord)) {
            return null;
        }
        int formulaBeginPos = lexer.getCurrPos();
        lexer.seekToRightBracket();
        int formulaEndPos = lexer.getCurrPos();
        String formula = lexer.getBuffer(formulaBeginPos, formulaEndPos);
        Object value = VariableFormula.calculate(formula, varTrans);
        return new LeftRightPair<>(StringRegularOpt.trimString(valueName), value);
    }

    private String fetchFuncStringParam(Lexer lexer){
        String currWord = lexer.getAWord();
        if (!"(".equals(currWord)) {
            return null;
        }
        String valueName = lexer.getAWord();
        lexer.seekToRightBracket();
        return StringRegularOpt.trimString(valueName);
    }

    private Object fetchFuncFormulaParam(Lexer lexer, FlowVariableTranslate varTrans){
        String currWord = lexer.getAWord();
        if (!"(".equals(currWord)) {
            return null;
        }
        int formulaBeginPos = lexer.getCurrPos();
        lexer.seekToRightBracket();
        int formulaEndPos = lexer.getCurrPos();
        String formula = lexer.getBuffer(formulaBeginPos, formulaEndPos);
        return VariableFormula.calculate(formula, varTrans);
    }
    /**
     * 运行流程脚本，流程脚本包括以下几个函数
     * setValue(字符串常量 name, 表达式 formula); // 计算变量
     * saveValue(字符串常量 name, 表达式 formula); // 计算变量，并保存到流程的变量表中
     * setNextOptUser(表达式 user); // 设置下一个交互节点的操作人员
     * closeNodes(字符串常量 nodeCode); //根据环节代码关闭节点
     * closeAllIsolatedNodes();// 关闭所有游离节点
     * closeAllOtherNodes(); // 关闭所有其他节点，非本节点全部关闭
     * deleteFlowWorkTeam(字符串常量 roleCode);//清除角色
     * assignFlowWorkTeam(字符串常量 roleCode, 表达式 users);// 设置办件角色
     * deleteFlowOrganize(字符串常量 roleCode);// 清除机构组
     * assignFlowOrganize(字符串常量 roleCode, 表达式 units);// 设置办件机构
     *
     * @param script  执行的脚本
     * @param flowInst 流程实例
     * @param nodeInst 节点实例
     * @param varTrans 变量
     * @return 所有变量列表
     */
    @Override
    public Map<String, Object> runFlowScript(String script, FlowInstance flowInst, NodeInstance nodeInst,
                                             FlowVariableTranslate varTrans) {
        Lexer lexer = new Lexer(script);
        Map<String, Object> retValueMap = new HashMap<>(16);
        String currWord = lexer.getAWord();
        while(StringUtils.isNotBlank(currWord)){
            //String funcName = currWord.toLowerCase();
            switch (currWord){
                case "setValue":
                {
                    LeftRightPair<String, Object> params = fetchFuncStringFormulaParams(lexer, varTrans);
                    if(params ==null){
                        break;
                    }
                    varTrans.setInnerVariable(params.getLeft(), nodeInst.getRunToken(), params.getRight());
                    retValueMap.put(params.getLeft(),params.getRight());
                }
                    break;
                case "saveValue":
                {
                    LeftRightPair<String, Object> params = fetchFuncStringFormulaParams(lexer, varTrans);
                    if(params ==null){
                        break;
                    }
                    flowEngine.saveFlowNodeVariable(nodeInst.getFlowInstId(), nodeInst.getRunToken(),
                        params.getLeft(),params.getRight());
                    varTrans.setInnerVariable(params.getLeft(), nodeInst.getRunToken(), params.getRight());
                    retValueMap.put(params.getLeft(),params.getRight());
                }
                    break;
                case "setNextOptUser":
                    String lockedUser =
                        StringBaseOpt.castObjectToString(fetchFuncFormulaParam(lexer, varTrans) );
                    if(StringUtils.isNotBlank(lockedUser)){
                        retValueMap.put("_lock_user",lockedUser);
                    }
                    break;
                case "closeNodes": { //closeNodes(nodeCode); //根据环节代码关闭节点
                    String nodecode = fetchFuncStringParam(lexer);
                    if (StringUtils.isNotBlank(nodecode)) {
                        closeNodes(nodecode, flowInst);
                    }
                }
                    break;
                case "closeAllIsolatedNodes": //closeAllIsolatedNodes();// 关闭所有游离节点
                    lexer.seekToRightBracket();
                    closeAllIsolatedNodes(flowInst);
                    break;

                case "closeAllOtherNodes": //closeAllOtherNodes(); // 关闭所有其他节点，非本节点全部关闭
                    lexer.seekToRightBracket();
                    closeAllOtherNodes(flowInst, nodeInst);
                    break;

                case "deleteFlowWorkTeam":{
                    String roleCode = fetchFuncStringParam(lexer);
                    if(StringUtils.isNotBlank(roleCode)){
                        flowEngine.deleteFlowWorkTeam(flowInst.getFlowInstId(), roleCode);
                    }
                    break;
                }

                case "assignFlowWorkTeam"://setFlowTeam(roleCode, users);// 设置办件角色
                {
                    LeftRightPair<String, Object> params = fetchFuncStringFormulaParams(lexer, varTrans);
                    if (params == null || StringUtils.isBlank(params.getLeft())) {
                        break;
                    }
                    flowEngine.assignFlowWorkTeam(flowInst.getFlowInstId(), params.getLeft(),
                        nodeInst.getRunToken(),
                        StringBaseOpt.objectToStringList(params.getRight()));
                }
                    break;

                case "deleteFlowOrganize":{
                    String roleCode = fetchFuncStringParam(lexer);
                    if(StringUtils.isNotBlank(roleCode)){
                        flowEngine.deleteFlowOrganize(flowInst.getFlowInstId(), roleCode);
                    }
                    break;
                }


                case "assignFlowOrganize"://setFlowOrganize(roleCode, units);// 设置办件机构
                {
                    LeftRightPair<String, Object> params = fetchFuncStringFormulaParams(lexer, varTrans);
                    if (params == null || StringUtils.isBlank(params.getLeft())) {
                        break;
                    }
                    flowEngine.assignFlowOrganize(flowInst.getFlowInstId(), params.getLeft(),
                        StringBaseOpt.objectToStringList(params.getRight()), "来自脚本引擎的授权");
                }
                    break;
            }
            currWord = lexer.getAWord();
            while(";".equals(currWord)){
                currWord = lexer.getAWord();
            }
        }
        return retValueMap;
    }

    private void closeNodeInstanceInside(NodeInstance ni) {

        if ("W".equals(ni.getNodeState())) { //结束子流程
            FlowInstance subFlowInst = flowInstanceDao.getObjectById(ni.getSubFlowInstId());
            if (subFlowInst != null) {
                FlowOptUtils.endInstance(subFlowInst, "F", "system", flowInstanceDao);
            }
        }
        ni.setNodeState("F");// 节点设置为无效
        ni.setLastUpdateTime(DatetimeOpt.currentUtilDate());
        ni.setLastUpdateUser("system");
        nodeInstanceDao.updateObject(ni);
    }

    private void closeNodes(String nodeCode, FlowInstance flowInst) {
        if(flowInst.getFlowNodeInstances().size() == 0){
            flowInstanceDao.fetchObjectReference(flowInst, "flowNodeInstances");
        }
        Set<NodeInstance> activeNodes = flowInst.getActiveNodeInstances();
        for(NodeInstance ni : activeNodes){
            String nc = ni.getNodeCode();
            if(nc!=null) {
                nodeInstanceDao.fetchObjectReference(ni, "node");
                nc = ni.getNodeCode();
            }
            if(StringUtils.equals(nodeCode, nc)){
                closeNodeInstanceInside(ni);
            }
        }
    }

    private void closeAllIsolatedNodes( FlowInstance flowInst) {
        if(flowInst.getFlowNodeInstances().size() == 0){
            flowInstanceDao.fetchObjectReference(flowInst, "flowNodeInstances");
        }
        Set<NodeInstance> activeNodes = flowInst.getActiveNodeInstances();
        for(NodeInstance ni : activeNodes){
            if (ni.getRunToken().contains(NodeInstance.RUN_TOKEN_ISOLATED)) {
                closeNodeInstanceInside(ni);
            }
        }
    }

    private void closeAllOtherNodes( FlowInstance flowInst, NodeInstance nodeInst) {
        if(flowInst.getFlowNodeInstances().size() == 0){
            flowInstanceDao.fetchObjectReference(flowInst, "flowNodeInstances");
        }
        Set<NodeInstance> activeNodes = flowInst.getActiveNodeInstances();
        for(NodeInstance ni : activeNodes){
            if (! StringUtils.equals(ni.getRunToken(), nodeInst.getRunToken())) {
                closeNodeInstanceInside(ni);
            }
        }
    }
}
