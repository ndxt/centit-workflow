package com.centit.workflow.service.impl;

import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.common.LeftRightPair;
import com.centit.support.compiler.Lexer;
import com.centit.support.compiler.VariableFormula;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.FlowScriptRunTime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class FlowScriptRunTimeImpl implements FlowScriptRunTime {

    @Autowired
    FlowEngine flowEngine;

    @Autowired
    private FlowManager flowManager;

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
     * setValue(name, formula); // 计算变量
     * saveValue(name, formula); // 计算变量，并保存到流程的变量表中
     * setNextOptUser(user); // 设置下一个交互节点的操作人员
     * closeNodes(nodeCode); //根据环节代码关闭节点
     * closeAllIsolatedNodes();// 关闭所有游离节点
     * closeAllOtherNodes(); // 关闭所有其他节点，非本节点全部关闭
     * setFlowTeam(roleCode, users);// 设置办件角色
     * setFlowOrganize(roleCode, units);// 设置办件机构
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
                case "closeNodes": //closeNodes(nodeCode); //根据环节代码关闭节点
                    String nodecode = fetchFuncStringParam(lexer);
                    if(StringUtils.isNotBlank(nodecode)){
                       //flowEngine
                    }
                    break;
                case "closeAllIsolatedNodes": //closeAllIsolatedNodes();// 关闭所有游离节点
                    lexer.seekToRightBracket();

                    break;
                case "closeAllOtherNodes": //closeAllOtherNodes(); // 关闭所有其他节点，非本节点全部关闭
                    lexer.seekToRightBracket();

                    break;
                case "setFlowTeam"://setFlowTeam(roleCode, users);// 设置办件角色
                {
                    LeftRightPair<String, Object> params = fetchFuncStringFormulaParams(lexer, varTrans);
                    if (params == null) {
                        break;
                    }
                }
                    break;
                case "setFlowOrganize"://setFlowOrganize(roleCode, units);// 设置办件机构
                {
                    LeftRightPair<String, Object> params = fetchFuncStringFormulaParams(lexer, varTrans);
                    if (params == null) {
                        break;
                    }
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
}
