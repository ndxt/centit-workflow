package com.centit.workflow.client.po;


import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */ 
public class StageInstance implements java.io.Serializable {
	private static final long serialVersionUID =  1L;
	private StageInstanceId cid;
	private String  stageCode;
	private Long  promiseTime;
	private Long  timeLimit;
	private String  stageName;
    
	private String stageBegin;
    private Date beginTime;
    private Date  lastUpdateTime;

    
	// Constructors
	/** default constructor */
	public StageInstance() {
	    this.stageBegin= "0";
	}
	/** minimal constructor */
	public StageInstance(StageInstanceId id) {
		this.cid = id; 	
		this.stageBegin= "0";
	}

/** full constructor */
	public StageInstance(StageInstanceId id, String stageCode,
						 Long  promiseTime, Long  timeLimit, Long  expireOptSign,
						 String stageBegin, Date beginTime, Date lastUpdateTime) {
		this.cid = id; 
			
		this.stageCode = stageCode;
		this.promiseTime= promiseTime;
		this.timeLimit= timeLimit;
        this.stageBegin= stageBegin;
        this.beginTime= beginTime;
        this.lastUpdateTime= lastUpdateTime;  
    }

	public StageInstanceId getCid() {
		return this.cid;
	}
	
	public void setCid(StageInstanceId id) {
		this.cid = id;
	}
  
	public Long getFlowInstId() {
		if(this.cid==null)
			this.cid = new StageInstanceId();
		return this.cid.getFlowInstId();
	}
	
	public void setFlowInstId(Long flowInstId) {
		if(this.cid==null)
			this.cid = new StageInstanceId();
		this.cid.setFlowInstId(flowInstId);
	}
  
	public Long getStageId() {
		if(this.cid==null)
			this.cid = new StageInstanceId();
		return this.cid.getStageId();
	}
	
	public void setStageId(Long stageId) {
		if(this.cid==null)
			this.cid = new StageInstanceId();
		this.cid.setStageId(stageId);
	}

	// Property accessors
	public String getStageCode() {
        return this.stageCode;
    }
    
    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }
    
	public Long getPromiseTime() {
		return this.promiseTime;
	}
	
//	public String getPromiseTimeStr() {
//        if(promiseTime==null)
//            return "";
//        WorkTimeSpan wts = new WorkTimeSpan();
//        wts.fromNumber(promiseTime);
//        return wts.getTimeSpanDesc();
//    }
	
	public void setPromiseTime(Long promiseTime) {
		this.promiseTime = promiseTime;
	}
  
	public Long getTimeLimit() {
		return this.timeLimit;
	}
		
//	public String getTimeLimitStr() {
//        if(timeLimit==null)
//            return "";
//        WorkTimeSpan wts = new WorkTimeSpan();
//        wts.fromNumber(timeLimit);
//        return wts.getTimeSpanDesc();
//	}
	
	public void setTimeLimit(Long timeLimit) {
		this.timeLimit = timeLimit;
	}
  
	public String getStageBegin() {
        return stageBegin;
    }
    public void setStageBegin(String stageBegin) {
        this.stageBegin = stageBegin;
    }
    public Date getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    public String getStageName() {
        return stageName;
    }
    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
    
	public void copy(StageInstance other){
		this.setFlowInstId(other.getFlowInstId());  
		this.setStageId(other.getStageId());
		this.stageCode = other.getStageCode();
		this.promiseTime= other.getPromiseTime();  
		this.timeLimit= other.getTimeLimit();  

		this.stageBegin= other.getStageBegin();
        this.beginTime=  other.getBeginTime();
        this.lastUpdateTime= other.getLastUpdateTime();  
	}
	
	public void copyNotNullProperty(StageInstance other){
    	if( other.getFlowInstId() != null)
    		this.setFlowInstId(other.getFlowInstId());  
    	if( other.getStageId() != null)
    		this.setStageId(other.getStageId());
    	
        if( other.getStageCode() != null)
            this.stageCode = other.getStageCode();
 		if( other.getPromiseTime() != null)
			this.promiseTime= other.getPromiseTime();  
		if( other.getTimeLimit() != null)
			this.timeLimit= other.getTimeLimit();  
		
		if( other.getStageBegin() != null)
		    this.stageBegin= other.getStageBegin();
		if( other.getBeginTime() != null)
		    this.beginTime=  other.getBeginTime();
		if( other.getLastUpdateTime() != null)
		    this.lastUpdateTime= other.getLastUpdateTime();  
	}
	
	public void clearProperties(){
	    this.stageCode = null;
		this.promiseTime= null;  
		this.timeLimit= null;  
		this.stageBegin= "0";
        this.beginTime=  null;
        this.lastUpdateTime= null;  
	}
    
}
