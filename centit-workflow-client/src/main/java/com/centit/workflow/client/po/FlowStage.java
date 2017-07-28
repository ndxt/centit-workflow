package com.centit.workflow.client.po;


/**
 * create by scaffold
 * @author codefan@hotmail.com
 */ 
public class FlowStage implements java.io.Serializable {
	private static final long serialVersionUID =  1L;
	
	private Long stageId;
	
	private String  stageCode;
	private String  stageName;
	private String  isAccountTime;
	private String  limitType;
	private String  timeLimit;
	private String  expireOpt;
	
	private FlowInfo flowDefine;
	
	public FlowInfo getFlowDefine() {
        return flowDefine;
    }
    public void setFlowDefine(FlowInfo flowDefine) {
        this.flowDefine = flowDefine;
    }
    // Constructors
	/** default constructor */
	public FlowStage() {
	}
	/** minimal constructor */
	public FlowStage(
		Long stageId		
		,String  stageCode) {	
		this.stageId = stageId;	
		this.stageCode= stageCode; 		
	}

/** full constructor */
	public FlowStage(
			Long stageId
	, FlowInfo flowDefine, String  stageCode, String  stageName, String  isAccountTime, String  limitType, String  timeLimit, String  expireOpt) {

		this.stageId = stageId;
		this.flowDefine = flowDefine;
		this.stageCode= stageCode;
		this.stageName= stageName;
		this.isAccountTime= isAccountTime;
		this.limitType= limitType;
		this.timeLimit= timeLimit;
		this.expireOpt= expireOpt;		
	}
	

  
	public Long getStageId() {
		return this.stageId;
	}

	public void setStageId(Long stageId) {
		this.stageId = stageId;
	}
	// Property accessors
  
	public Long getVersion() {
	    if (null != flowDefine) {
	        return this.flowDefine.getVersion();
        }
		
	    return null;
	}
	
	/*public void setVersion(Long version) {
	    if (null != flowDefine) {
	        this.flowDefine.setVersion(version);
        }
	}*/
	
	public String getFlowCode() {
	    if (null != flowDefine) {
	        return this.flowDefine.getFlowCode();
	    }	    
	    return null;
	}
	
	/*public void setFlowCode(String flowCode) {
	    if (null != flowDefine) {
	        this.flowDefine.setFlowCode(flowCode);
        }
	}*/
  
	public String getStageCode() {
		return this.stageCode;
	}
	
	public void setStageCode(String stageCode) {
		this.stageCode = stageCode;
	}
  
	public String getStageName() {
		return this.stageName;
	}
	
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
  
	public String getIsAccountTime() {
		return this.isAccountTime;
	}
	
	public void setIsAccountTime(String isAccountTime) {
		this.isAccountTime = isAccountTime;
	}
  
	public String getLimitType() {
		return this.limitType;
	}
	
	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}
  
	public String getTimeLimit() {
		return this.timeLimit;
	}
	
	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}
  
	public String getExpireOpt() {
		return this.expireOpt;
	}
	
	public void setExpireOpt(String expireOpt) {
		this.expireOpt = expireOpt;
	}



	public void copy(FlowStage other){
		this.setStageId(other.getStageId());  
		//this.setVersion(other.getVersion());  
		//this.setFlowCode(other.getFlowCode());  
		this.stageCode= other.getStageCode();  
		this.stageName= other.getStageName();  
		this.isAccountTime= other.getIsAccountTime();  
		this.limitType= other.getLimitType();  
		this.timeLimit= other.getTimeLimit();  
		this.expireOpt= other.getExpireOpt();

	}
	
	public void copyNotNullProperty(FlowStage other){
  	    
	    if( other.getStageId() != null)
	        this.setStageId(other.getStageId());
		/*if( other.getFlowDefine() != null)
			this.flowDefine = other.getFlowDefine();*/
		if( other.getStageCode() != null)
			this.stageCode= other.getStageCode();  
		if( other.getStageName() != null)
			this.stageName= other.getStageName();  
		if( other.getIsAccountTime() != null)
			this.isAccountTime= other.getIsAccountTime();  
		if( other.getLimitType() != null)
			this.limitType= other.getLimitType();  
		if( other.getTimeLimit() != null)
			this.timeLimit= other.getTimeLimit();  
		if( other.getExpireOpt() != null)
			this.expireOpt= other.getExpireOpt();
	}
	
	public void clearProperties(){
  
		this.flowDefine=null; 
		this.stageCode= null;  
		this.stageName= null;  
		this.isAccountTime= null;  
		this.limitType= null;  
		this.timeLimit= null;  
		this.expireOpt= null;

	}
    
}
