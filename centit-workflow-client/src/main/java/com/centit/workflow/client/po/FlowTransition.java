package com.centit.workflow.client.po;


/**
 * create by scaffold
 * @author codefan@hotmail.com
 */ 
public class FlowTransition implements java.io.Serializable {
	private static final long serialVersionUID =  1L;

	private Long transId;

	private String transClass;
	private String transName;
	private String transDesc;
	private Long startNodeId;
	private Long endNodeId;
	private String transCondition;
    private String limitType;
    private String timeLimit;
    private String isAccountTime;
    private String canIgnore;

    
    
    
    private FlowInfo flowDefine;
    
    
    // Constructors
	/** default constructor */
	public FlowTransition() {
	    this.canIgnore="T"; 
	    isAccountTime ="I";
	}
	/** minimal constructor */
	public FlowTransition(
		Long transid		
		) {
	    this.canIgnore="T"; 
	    isAccountTime ="I";
		this.transId = transid;
			
	}

/** full constructor */
	public FlowTransition(
	 Long transid		
	,Long  version,String  wfcode,String  transclass,String  transname,String  transdesc,
	 Long  startnodeid,Long  endnodeid,String  transcondition,String limitType,String timelimit) {
	
	
		this.transId = transid;
	
		this.getFlowDefine().setVersion(version);
		this.getFlowDefine().setFlowCode(wfcode);
		this.transClass = transclass;
		this.transName = transname;
		this.transDesc = transdesc;
		this.startNodeId = startnodeid;
		this.endNodeId = endnodeid;
		this.transCondition = transcondition;
		this.limitType = limitType;
		this.timeLimit = timelimit;
		this.canIgnore="T"; 
		this.isAccountTime ="I";
	}
	
    /**
     * 期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、 F 每实例固定期限 fix 、C 节点固定期限  cycle、H 继承上一个节点剩余时间 hierarchical。
     * @return
     */
    public String getLimitType() {
        return limitType;
    }
    /**
     * 期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、 F 每实例固定期限 fix 、C 节点固定期限  cycle、H 继承上一个节点剩余时间 hierarchical。
     * @param limitType
     */
    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public String getTimeLimit() {
        return this.timeLimit;
    }

    public void setTimeLimit(String timelimit) {
        this.timeLimit = timelimit;
    }
  
	public Long getTransid() {
		return this.transId;
	}

	public void setTransid(Long transid) {
		this.transId = transid;
	}
	// Property accessors
  
	public String getFlowCode() {
	    if(this.flowDefine==null)
            return null;
        return this.flowDefine.getFlowCode();
    }

    public Long getVersion() {
        if(this.flowDefine==null)
            return null;
        return this.flowDefine.getVersion();
    }

  
	public String getTransClass() {
		return this.transClass;
	}
	
	public void setTransClass(String transClass) {
		this.transClass = transClass;
	}
  
	public String getTransName() {
		return this.transName;
	}
	
	public void setTransName(String transName) {
		this.transName = transName;
	}
  
	public String getTransDesc() {
		return this.transDesc;
	}
	
	public void setTransDesc(String transDesc) {
		this.transDesc = transDesc;
	}
  
	public Long getStartnodeid() {
		return this.startNodeId;
	}
	
	public void setStartnodeid(Long startnodeid) {
		this.startNodeId = startnodeid;
	}
  
	public Long getEndnodeid() {
		return this.endNodeId;
	}
	
	public void setEndnodeid(Long endnodeid) {
		this.endNodeId = endnodeid;
	}
  
	public String getTranscondition() {
		return this.transCondition;
	}
	
	public void setTranscondition(String transcondition) {
		this.transCondition = transcondition;
	}
	/**
	 * 改是否记入时间 T/F  I 忽略
	 * @return
	 */
    public String getIsAccountTime() {
        return isAccountTime;
    }
    /**
     * 改是否记入时间 T/F  I 忽略
     * @param isAccountTime
     */
    public void setIsAccountTime(String isAccountTime) {
        this.isAccountTime = isAccountTime;
    }
    /**
     * T可以忽略 F 不可以忽略  是否可以忽略运行
     * @return
     */
    public String getCanIgnore() {
        return canIgnore;
    }
    /**
     * T可以忽略 F 不可以忽略  是否可以忽略运行
     * @param canIgnore
     */
    public void setCanIgnore(String canIgnore) {
        this.canIgnore = canIgnore;
    }

    public void copy(FlowTransition other){
        //this.setTransid(other.getTransid());  
        this.setFlowDefine(other.getFlowDefine()); 
        this.transClass = other.getTransClass();
        this.transName = other.getTransName();
        this.transDesc = other.getTransDesc();
        this.startNodeId = other.getStartnodeid();
        this.endNodeId = other.getEndnodeid();
        this.transCondition = other.getTranscondition();
        this.limitType = other.getLimitType();
        this.timeLimit = other.getTimeLimit();
        
        this.isAccountTime= other.getIsAccountTime();  
        this.canIgnore= other.getCanIgnore();  
    }
    
    public void copyNotNullProperty(FlowTransition other){
  
        /*if( other.getTransid() != null)
            this.setTransid(other.getTransid());*/
      
        if( other.getFlowDefine() != null)
            this.setFlowDefine(other.getFlowDefine()); 

        if( other.getTransClass() != null)
            this.transClass = other.getTransClass();
        if( other.getTransName() != null)
            this.transName = other.getTransName();
        if( other.getTransDesc() != null)
            this.transDesc = other.getTransDesc();
        if( other.getStartnodeid() != null)
            this.startNodeId = other.getStartnodeid();
        if( other.getEndnodeid() != null)
            this.endNodeId = other.getEndnodeid();
        if( other.getTranscondition() != null)
            this.transCondition = other.getTranscondition();
        if( other.getLimitType() != null)
            this.limitType= other.getLimitType();  
        if( other.getTimeLimit() != null)
            this.limitType= other.getTimeLimit();  
        
        if( other.getIsAccountTime() != null)
            this.isAccountTime= other.getIsAccountTime();  
        if( other.getCanIgnore() != null)
            this.canIgnore= other.getCanIgnore();  
    }
    
  public void clearProperties(){
        
       
        this.flowDefine = null; 
        this.transClass =null;
        this.transName = null;
        this.transDesc = null;
        this.startNodeId =null;
        this.endNodeId = null;
        this.transCondition = null;
        this.limitType =null;
        this.timeLimit = null;
        
        this.isAccountTime= "I";  
        this.canIgnore="T";  
    }
  
    public FlowInfo getFlowDefine() {
        return flowDefine;
    }
    public void setFlowDefine(FlowInfo flowDefine) {
        this.flowDefine = flowDefine;
    }
    
}
