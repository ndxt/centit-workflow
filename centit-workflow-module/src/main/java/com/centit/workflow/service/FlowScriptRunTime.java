package com.centit.workflow.service;

import com.centit.workflow.commons.FlowOptParamOptions;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.impl.FlowVariableTranslate;

import java.util.Map;

public interface FlowScriptRunTime {
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
     * @param options 运行参数
     * @return 所有变量列表
     */
    Map<String, Object> runFlowScript(String script,
                                      FlowInstance flowInst,
                                      NodeInstance nodeInst,
                                      FlowVariableTranslate varTrans,
                                      FlowOptParamOptions options);
}
