package com.centit.workflow.commons;

import com.centit.workflow.po.FlowInfo;

import java.util.Set;

public interface NodeMsgSupport {
    /**
     * 发送节点消息到消息中心，在节点创建之后调用，比如submit接口、createInstan接口之后
     * @param nodeInstId 当前节点id
     * @param nextNodeInsts 下一步的节点ids
     * @param userCode 当前操作人
     * @throws WorkflowException
     */
    void sendNodeMsg(long nodeInstId, Set<Long> nextNodeInsts,String userCode) throws WorkflowException;

    /**
     * 在流程办结的时候更新节点消息到消息中心
     * @param flowInstId 当前节点id
     * @param userCode 当前操作人
     * @throws WorkflowException
     */
    void sendFlowNodeMsg(String flowInstId,String userCode) throws WorkflowException;

    /**
     * 发送节点信息到短信服务
     * @param nextNodeInsts 下一步的节点ids
     * @param userCode 当前操作人
     * @throws WorkflowException
     */
    void sendNodeSms(Set<Long> nextNodeInsts,String userCode) throws WorkflowException;

    /**
     * 在流程强制办结的时候发送短信到原本的所有待办人
     * @param flowInstId 当前节点id
     * @param userCode 当前操作人
     * @throws WorkflowException
     */
    void sendFlowNodeSms(String flowInstId,String userCode) throws WorkflowException;

    /**
     * 在新增流程定义的时候发送流程信息到业务系统
     * @param flowInfo 当前节点id
     * @throws WorkflowException
     */
    void sendFlowInfo(FlowInfo flowInfo) throws WorkflowException;
}
