package com.centit.workflow.commons;

public interface ExtraFlowManager {

    /**
     * 终止一个流程
     */
    
    public int stopInstance(long flowInstId,String mangerUserCode,String admindesc) throws WorkflowException;;
    /**
     * 暂停一个流程
     */
    
    public int suspendInstance(long flowInstId,String mangerUserCode,String admindesc) throws WorkflowException;;
    /**
     * 使流程失效
     */
    
    public int activizeInstance(long flowInstId,String mangerUserCode,String admindesc) throws WorkflowException;;
    
    
    /**
     * 暂停流程的一个节点
     */
    public long suspendNodeInstance(long nodeInstId,String mangerUserCode) throws WorkflowException;;
    
    /**
     * 使流程的 挂起和失效的节点 正常运行
     */
    public long activizeNodeInstance(long nodeInstId,String mangerUserCode) throws WorkflowException;;
  
}
