package com.centit.workflow.po;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.support.common.WorkTimeSpan;

import javax.persistence.*;
import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */ 

public class UserTask implements java.io.Serializable {
	private static final long serialVersionUID =  1L;


	private Long nodeInstId;
	private String unitCode;
	private String userCode;

	private Long  flowInstId;
	private String  authDesc;
	private String  flowOptName;
    private String  flowName;
	public String getFlowName() {
        return flowName;
    }
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
	private String  flowOptTag;
	private String  flowCode;
	private String  version;
	private String  nodeCode;
    private String  nodeName;
	private String  nodeType;
	private String  nodeOptType;
	private String  optParam;
	private String  optCode;
	private Date  createTime;
	private String  expireOpt;
    private Date  lastUpdateTime;
    private String lastUpdateUser;
    private Long  promiseTime;
    private Long  timeLimit;
    private String grantor;
    private String roleType;
    private String roleCode;
    private String instState;
    
    //流程节点阶段
    private String flowStage;


    private Date nodeCreateTime;

    private Date nodeExpireTime;

    private Date nodeLastUpdateTime;

    private Date flowExpireTime;

    private Long flowTimeLimit;
    
  
	// Constructors
	/** default constructor */
	public UserTask() {
	}
	
	public String getRoleTypeText() {
        return CodeRepositoryUtil.getValue("WFRoleType", getRoleType());
    }

	public Long getNodeInstId() {
		return nodeInstId;
	}

	public void setNodeInstId(Long nodeInstId) {
		this.nodeInstId = nodeInstId;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getInstState() {
		return instState;
	}

	public void setInstState(String instState) {
		this.instState = instState;
	}

	public String getFlowCode() {
        return flowCode;
    }
    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getNodeCode() {
        return nodeCode;
    }
    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }
    
    public String getRoleType() {
        return this.roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }
  
    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

	// Property accessors
 	public Long getFlowInstId() {
		return this.flowInstId;
	}
	
	public void setFlowInstId(Long flowInstId) {
		this.flowInstId = flowInstId;
	}
  
	public String getAuthDesc() {
		return this.authDesc;
	}
	
	public void setAuthDesc(String authDesc) {
		this.authDesc = authDesc;
	}
  
	public String getFlowOptName() {
		return this.flowOptName;
	}
	
	public void setFlowOptName(String flowOptName) {
		this.flowOptName = flowOptName;
	}
  
	public String getFlowOptTag() {
		return this.flowOptTag;
	}
	
	public void setFlowOptTag(String flowOptTag) {
		this.flowOptTag = flowOptTag;
	}
  
	public String getNodeName() {
		return this.nodeName;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
  
	public String getNodeType() {
		return this.nodeType;
	}
	
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
  
	public String getNodeOptType() {
		return this.nodeOptType;
	}
	
	public void setNodeOptType(String nodeOptType) {
		this.nodeOptType = nodeOptType;
	}
  
	public String getOptParam() {
		return this.optParam;
	}
	
	public void setOptParam(String optParam) {
		this.optParam = optParam;
	}
	public String getOptCode() {
		return this.optCode;
	}
	
	public void setOptCode(String optCode) {
		this.optCode = optCode;
	}
  
	public Date getCreateTime() {
		return this.createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getPromiseTimeStr() {
        if(promiseTime==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumber(promiseTime);
        return wts.getTimeSpanDesc();
    }
    
    public Long getPromiseTime() {
        return promiseTime;
    }
    
    public void setPromiseTime(Long promiseTime) {
        this.promiseTime = promiseTime;
    }
  
    public String getTimeLimitStr() {
        if(timeLimit==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumber(timeLimit);
        return wts.getTimeSpanDesc();
    }

	public String getNodeOptUrl(){
		return null;
	}
    /**
     * N：通知， O:不处理 ，X：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）
     * @return
     */
    public String getExpireOpt() {
        return this.expireOpt;
    }
    /**
     * N：通知， O:不处理 ，X：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）
     * @param expireopt
     */
    public void setExpireOpt(String expireopt) {
        this.expireOpt = expireopt;
    }
	public void copy(UserTask other){
  
		this.setNodeInstId(other.getNodeInstId());  
		this.setUnitCode(other.getUnitCode());  
		this.setUserCode(other.getUserCode());  
		this.setRoleType(other.getRoleType());  
		this.setRoleCode(other.getRoleCode());
  
		this.flowInstId= other.getFlowInstId();  
		this.authDesc= other.getAuthDesc();  
		this.flowOptName= other.getFlowOptName();  
		this.flowOptTag= other.getFlowOptTag();  
		this.nodeName= other.getNodeName();  
		this.nodeType= other.getNodeType();  
		this.nodeOptType= other.getNodeOptType();
		this.optParam= other.getOptParam();
		this.optCode= other.getOptCode();  
		this.createTime= other.getCreateTime();  
		this.promiseTime= other.getPromiseTime();  
		this.lastUpdateTime= other.getLastUpdateTime();
		this.expireOpt= other.getExpireOpt();
		this.grantor = other.getGrantor();
		this.flowStage = other.getFlowStage();
        this.roleType = other.getRoleType();
        this.roleCode = other.getRoleCode(); 
	}
	
	public void copyNotNullProperty(UserTask other){
  
	if( other.getNodeInstId() != null)
		this.setNodeInstId(other.getNodeInstId());  
	if( other.getUnitCode() != null)
		this.setUnitCode(other.getUnitCode());  
	if( other.getUserCode() != null)
		this.setUserCode(other.getUserCode());  
	if( other.getRoleType() != null)
		this.setRoleType(other.getRoleType());  
	if( other.getRoleCode() != null)
		this.setRoleCode(other.getRoleCode());
  
		if( other.getFlowInstId() != null)
			this.flowInstId= other.getFlowInstId();  
		if( other.getAuthDesc() != null)
			this.authDesc= other.getAuthDesc();  
		if( other.getFlowOptName() != null)
			this.flowOptName= other.getFlowOptName();  
		if( other.getFlowOptTag() != null)
			this.flowOptTag= other.getFlowOptTag();  
		if( other.getNodeName() != null)
			this.nodeName= other.getNodeName();  
		if( other.getNodeType() != null)
			this.nodeType= other.getNodeType();  
		if( other.getNodeOptType() != null)
			this.nodeOptType= other.getNodeOptType();
		if( other.getOptParam() != null)
			this.optParam= other.getOptParam();
		if( other.getOptCode() != null)
			this.optCode= other.getOptCode();  
		if( other.getCreateTime() != null)
			this.createTime= other.getCreateTime();  
		if( other.getPromiseTime() != null)
			this.promiseTime= other.getPromiseTime();  
		if( other.getExpireOpt() != null)
			this.expireOpt= other.getExpireOpt();
        if( other.getLastUpdateTime() != null)
            this.lastUpdateTime= other.getLastUpdateTime();
		if(other.getGrantor() != null){
		    this.grantor = other.getGrantor();
		}
		if(other.getFlowStage() != null)
		    this.flowStage = other.getFlowStage();
		if(other.getRoleType() != null){
		    this.roleType = other.getRoleType();
		}
		if(other.getRoleCode() != null){
		    this.roleCode = other.getRoleCode(); 
		}
	}
	
	public void clearProperties(){
  
		this.flowInstId= null;  
		this.authDesc= null;  
		this.flowOptName= null;  
		this.flowOptTag= null;  
		this.nodeName= null;  
		this.nodeType= null;  
		this.nodeOptType= null;
		this.optParam= null;
		this.optCode= null;  
		this.createTime= null;  
		this.promiseTime=null;  
		this.expireOpt= null;
		this.grantor = null;
		this.flowStage = null;
	}
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    public String getLastUpdateUser() {
        return lastUpdateUser;
    }
    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }
    public Long getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }
    public String getGrantor() {
        return grantor;
    }
    public void setGrantor(String grantor) {
        this.grantor = grantor;
    }
    public String getFlowStage() {
        return flowStage;
    }
    public void setFlowStage(String flowPhase) {
        this.flowStage = flowPhase;
    }
    public String getInststate() {
        return instState;
    }
    public void setInststate(String inststate) {
        this.instState = inststate;
    }
    public Date getNodeCreateTime() {
        return nodeCreateTime;
    }
    public void setNodeCreateTime(Date nodeCreateTime) {
        this.nodeCreateTime = nodeCreateTime;
    }
    public Date getNodeExpireTime() {
        return nodeExpireTime;
    }
    public void setNodeExpireTime(Date nodeExpireTime) {
        this.nodeExpireTime = nodeExpireTime;
    }
 
    public Date getNodeLastUpdateTime() {
        return nodeLastUpdateTime;
    }
    public void setNodeLastUpdateTime(Date nodeLastUpdateTime) {
        this.nodeLastUpdateTime = nodeLastUpdateTime;
    }
    public Date getFlowExpireTime() {
        return flowExpireTime;
    }
    public void setFlowExpireTime(Date flowExpireTime) {
        this.flowExpireTime = flowExpireTime;
    }
    public Long getFlowTimeLimit() {
        return flowTimeLimit;
    }
    public void setFlowTimeLimit(Long flowTimeLimit) {
        this.flowTimeLimit = flowTimeLimit;
    }
}
