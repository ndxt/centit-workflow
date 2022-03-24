package com.centit.workflow.service.impl;

import com.centit.support.compiler.Lexer;
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
                    currWord = lexer.getAWord();
                    if (!"(".equals(currWord)) {
                        break;
                    }
                    String valueName = lexer.getAWord();
                    currWord = lexer.getAWord();
                    if (!",".equals(currWord)) {
                        break;
                    }
                    lexer.seekToRightBracket();
                }
                    break;
                case "saveValue":

                    break;
                case "setNextOptUser":
                    break;
                case "closeNodes":
                    break;
                case "closeAllIsolatedNodes":
                    break;
                case "closeAllOtherNodes":
                    break;
                case "setFlowTeam":
                    break;
                case "setFlowOrganize":
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
