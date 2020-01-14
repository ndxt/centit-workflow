package com.centit.workflow.commons;

import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;

/**
 *
 * 节点事件接口
 *
 * @author codefan
 *  2013-7-10
 * @version 4.0.0
 */
@SuppressWarnings("unused")
public interface NodeEventSupport {
    /**
     * 在节点创建后的调用的的事件
     * @param flowInst 流程实例
     * @param nodeInst 节点实例
     * @param nodeInfo 用户自定义操作参数
     * @param optUserCode 当前操作用户
     */
    void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                        NodeInfo nodeInfo, String optUserCode) throws WorkflowException;

    /**
     * 在节点提交前的调用的的事件
     * @param flowInst 流程实例
     * @param nodeInst 节点实例
     * @param nodeInfo 用户自定义操作参数
     * @param optUserCode 当前操作用户
     */

    void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                         NodeInfo nodeInfo, String optUserCode) throws WorkflowException;

    /**
     * 自动运行节点所指向的spring bean需要实现这个接口
     * @param flowInst 流程实例
     * @param nodeInst 节点实例
     * @param nodeInfo 用户自定义操作参数
     * @param optUserCode 当前操作用户
     * @return true 需要运行后调用自动提交，false 则不需要，一般在函数内部已经调用submitNode
     */
    boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                            NodeInfo nodeInfo, String optUserCode) throws WorkflowException;

    /**
     * 是否可以跳转到下一个节点，用于 汇聚节点自行判断条件
     * @param flowInst 流程实例
     * @param nodeInst 节点实例
     * @param nodeInfo 用户自定义操作参数
     * @param optUserCode 当前操作用户
     * @return 是否可以跳转到下一个节点，用于 汇聚节点自行判断条件
     * @throws WorkflowException 异常
     */
    boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                          NodeInfo nodeInfo, String optUserCode) throws WorkflowException;
}
