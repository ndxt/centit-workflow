package com.centit.workflow.commons;
/**
 * Created by codefan on 17-9-11.
 * @author codefan
 */
@SuppressWarnings("unused")
public interface ExtraFlowManager {

    /**
     * 终止一个流程
     */
    
    int stopInstance(long flowInstId, String mangerUserCode, String admindesc) throws WorkflowException;
    /**
     * 暂停一个流程
     */
    
    int suspendInstance(long flowInstId, String mangerUserCode, String admindesc) throws WorkflowException;
    /**
     * 使流程失效
     */
    
    int activizeInstance(long flowInstId, String mangerUserCode, String admindesc) throws WorkflowException;
    
    
    /**
     * 暂停流程的一个节点
     */
    long suspendNodeInstance(long nodeInstId, String mangerUserCode) throws WorkflowException;
    
    /**
     * 使流程的 挂起和失效的节点 正常运行
     */
    long activizeNodeInstance(long nodeInstId, String mangerUserCode) throws WorkflowException;
  
}
