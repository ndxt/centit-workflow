package com.centit.workflow.client.po;

import com.centit.framework.components.CodeRepositoryUtil;

import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */ 
public class ManageActionLog implements java.io.Serializable {
	private static final long serialVersionUID =  1L;

	private Long actionId;
	
	private Long flowInstId;
	private Long nodeInstId;
	private String actionType;
	private Date actionTime;
	private String userCode;
	private String roleType;
	private String roleCode;
	private String adminDesc;

	// Constructors
	/** default constructor */
	public ManageActionLog() {
	}
	/** minimal constructor */
	public ManageActionLog(
		Long actionid		
		,String  actiontype,Date  actiontime) {
	
	
		this.actionId = actionid;
	
		this.actionType = actiontype;
		this.actionTime = actiontime;
	}

/** full constructor */
	public ManageActionLog(
	 Long actionid		
	,Long  wfinstid,String  actiontype,Date  actiontime,String  usercode,String  roletype,String  rolecode,String  admindesc) {
	
	
		this.actionId = actionid;
	
		this.flowInstId = wfinstid;
		this.actionType = actiontype;
		this.actionTime = actiontime;
		this.userCode = usercode;
		this.roleType = roletype;
		this.roleCode = rolecode;
		this.adminDesc = admindesc;
	}
	

  
	public Long getActionId() {
		return this.actionId;
	}

	public void setActionId(Long actionid) {
		this.actionId = actionid;
	}
	// Property accessors
  
	public Long getFlowInstId() {
		return this.flowInstId;
	}
	
	public void setFlowInstId(Long wfinstid) {
		this.flowInstId = wfinstid;
	}
  
	public Long getNodeInstId() {
        return nodeInstId;
    }
    public void setNodeInstId(Long nodeinstid) {
        this.nodeInstId = nodeinstid;
    }
    
    /**
     *  @return actionType 对流程操作用大写字母，对节点管理操作用小写字母
     *             S s: 状态变更， 超时唤醒、 使失效、 使一个正常的节点变为游离状态 、 是游离节点失效
     *               c: 创建节点  、创建一个游离节点 创建（任意）指定节点
     *             R r: 流转管理，包括  强行回退  、强行提交   
     *             T t: 期限管理 、 设置期限
     *               a: 节点任务管理  分配任务、  删除任务 、  禁用任务
     *             U u: 变更属性
     */
    public String getActionType() {
		return this.actionType;
	}
	
    
    public String getActionTypeText(){
        return CodeRepositoryUtil.getValue("WfActionType", getActionType());
    }
    /**
     *  @param actiontype 对流程操作用大写字母，对节点管理操作用小写字母
     *             S s: 状态变更， 超时唤醒、 使失效、 使一个正常的节点变为游离状态 、 是游离节点失效
     *               c: 创建节点  、创建一个游离节点 创建（任意）指定节点
     *             R r: 流转管理，包括  强行回退  、强行提交   
     *             T t: 期限管理 、 设置期限
     *               a: 节点任务管理  分配任务、  删除任务 、  禁用任务
     *             U u: 变更属性
     */
	public void setActionType(String actiontype) {
		this.actionType = actiontype;
	}
  
	public Date getActionTime() {
		return this.actionTime;
	}
	
	public void setActionTime(Date actiontime) {
		this.actionTime = actiontime;
	}
  
	public String getUserCode() {
		return this.userCode;
	}
	
	public void setUserCode(String usercode) {
		this.userCode = usercode;
	}
  
	public String getRoleType() {
		return this.roleType;
	}
	
	public void setRoleType(String roletype) {
		this.roleType = roletype;
	}
  
	public String getRoleCode() {
		return this.roleCode;
	}
	
	public void setRoleCode(String rolecode) {
		this.roleCode = rolecode;
	}
  
	public String getAdminDesc() {
		return this.adminDesc;
	}
	
	public void setAdminDesc(String admindesc) {
		this.adminDesc = admindesc;
	}



	public void copy(ManageActionLog other){
  
		this.setActionId(other.getActionId());
  
		this.flowInstId = other.getFlowInstId();
		this.actionType = other.getActionType();
		this.actionTime = other.getActionTime();
		this.userCode = other.getUserCode();
		this.roleType = other.getRoleType();
		this.roleCode = other.getRoleCode();
		this.adminDesc = other.getAdminDesc();
		this.nodeInstId = other.getNodeInstId();
	}
	
	public void copyNotNullProperty(ManageActionLog other){
  
	if( other.getActionId() != null)
		this.setActionId(other.getActionId());
  
		if( other.getFlowInstId() != null)
			this.flowInstId = other.getFlowInstId();
		if( other.getActionType() != null)
			this.actionType = other.getActionType();
		if( other.getActionTime() != null)
			this.actionTime = other.getActionTime();
		if( other.getUserCode() != null)
			this.userCode = other.getUserCode();
		if( other.getRoleType() != null)
			this.roleType = other.getRoleType();
		if( other.getRoleCode() != null)
			this.roleCode = other.getRoleCode();
		if( other.getAdminDesc() != null)
			this.adminDesc = other.getAdminDesc();
	    if( other.getNodeInstId() != null)
	        this.nodeInstId = other.getNodeInstId();

	}
}
