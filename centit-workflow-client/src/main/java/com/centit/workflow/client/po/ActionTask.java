package com.centit.workflow.client.po;

import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */
public class ActionTask implements java.io.Serializable {
	private static final long serialVersionUID =  1L;
	private Long taskId;
	private Long nodeInstId;
	private Date assignTime;
	private Date expireTime;
	private String userCode;
	private String roleType;
	private String roleCode;
	private String taskState;
	private String isValid;
	private String authDesc;

	// Constructors
	/** default constructor */
	public ActionTask() {
	}
	/** minimal constructor */
	public ActionTask(
		Long taskid		
		,Date  assigntime,Date  expiretime) {
	
	
		this.taskId = taskid;		
	
		this.assignTime = assigntime;
		this.expireTime = expiretime;
	}

/** full constructor */
	public ActionTask(
	 Long taskid		
	,Long  nodeinstid,Date  assigntime,Date  expiretime,String  usercode,String  roletype,String  rolecode,String  taskstate,String  isvalid,String  authdesc) {
	
	
		this.taskId = taskid;		
	
		this.nodeInstId = nodeinstid;
		this.assignTime = assigntime;
		this.expireTime = expiretime;
		this.userCode = usercode;
		this.roleType = roletype;
		this.roleCode = rolecode;
		this.taskState = taskstate;
		this.isValid = isvalid;
		this.authDesc = authdesc;
	}
	

  
	public Long getTaskId() {
		return this.taskId;
	}

	public void setTaskId(Long taskid) {
		this.taskId = taskid;
	}
	// Property accessors
  
	public Long getNodeInstId() {
		return this.nodeInstId;
	}
	
	public void setNodeInstId(Long nodeinstid) {
		this.nodeInstId = nodeinstid;
	}
  
	public Date getAssignTime() {
		return this.assignTime;
	}
	
	public void setAssignTime(Date assigntime) {
		this.assignTime = assigntime;
	}
  
	public Date getExpireTime() {
		return this.expireTime;
	}
	
	public void setExpireTime(Date expiretime) {
		this.expireTime = expiretime;
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
  
	/**
	 * @return A:已分配  C：已完成（提交）
	 */
	public String getTaskState() {
		return this.taskState;
	}
	
	/**
	 * 
	 * @param taskstate A:已分配  C：已完成（提交）
	 */
	public void setTaskState(String taskstate) {
		this.taskState = taskstate;
	}
  
    public boolean isValid() {
        return "T".equals(isValid);
    }
    /**
     * T 正常 F 失效
     * @return
     */
	public String getIsvalid() {
		return this.isValid;
	}
	
	public void setIsvalid(String isvalid) {
		this.isValid = isvalid;
	}
  
	public String getAuthDesc() {
		return this.authDesc;
	}
	
	public void setAuthDesc(String authdesc) {
		this.authDesc = authdesc;
	}



	public void copy(ActionTask other){
  
		this.setTaskId(other.getTaskId());  
		this.nodeInstId = other.getNodeInstId();
		this.assignTime = other.getAssignTime();
		this.expireTime = other.getExpireTime();
		this.userCode = other.getUserCode();
		this.roleType = other.getRoleType();
		this.roleCode = other.getRoleCode();
		this.taskState = other.getTaskState();
		this.isValid = other.getIsvalid();
		this.authDesc = other.getAuthDesc();

	}
	
	public void copyNotNullProperty(ActionTask other){
    	if( other.getTaskId() != null)
    		this.setTaskId(other.getTaskId());  
		if( other.getNodeInstId() != null)
			this.nodeInstId = other.getNodeInstId();
		if( other.getAssignTime() != null)
			this.assignTime = other.getAssignTime();
		if( other.getExpireTime() != null)
			this.expireTime = other.getExpireTime();
		if( other.getUserCode() != null)
			this.userCode = other.getUserCode();
		if( other.getRoleType() != null)
			this.roleType = other.getRoleType();
		if( other.getRoleCode() != null)
			this.roleCode = other.getRoleCode();
		if( other.getTaskState() != null)
			this.taskState = other.getTaskState();
		if( other.getIsvalid() != null)
			this.isValid = other.getIsvalid();
		if( other.getAuthDesc() != null)
			this.authDesc = other.getAuthDesc();
	}
 }
