package com.centit.workflow.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */ 
@Entity
@Table(name = "WF_INST_ATTENTION")
public class InstAttention implements java.io.Serializable {
	private static final long serialVersionUID =  1L;
	@EmbeddedId
	private InstAttentionId cid;

	@Column(name = "ATT_SET_TIME")
	private Date  attSetTime;
    @Column(name = "ATT_SET_USER")
	private String  attSetUser;
    @Column(name = "ATT_SET_MEMO")
	private String  attSetMemo;

	// Constructors
	/** default constructor */
	public InstAttention() {
	}
	/** minimal constructor */
	public InstAttention(InstAttentionId id
				
		) {
		this.cid = id; 
			
			
	}

/** full constructor */
	public InstAttention(InstAttentionId id
			
	    , Date  attSetTime, String  attSetUser, String  attSetMemo) {
		this.cid = id; 
			
	
		this.attSetTime= attSetTime;
		this.attSetUser= attSetUser;
		this.attSetMemo= attSetMemo;		
	}
    public InstAttention(String userCode, Long flowInstId, Date  attSetTime, String  attSetUser) {
            this.cid = new InstAttentionId(userCode, flowInstId);
            this.attSetTime= attSetTime;
            this.attSetUser= attSetUser;
        }
	
	public InstAttentionId getCid() {
		return this.cid;
	}
	
	public void setCid(InstAttentionId id) {
		this.cid = id;
	}
  
	public String getUserCode() {
		if(this.cid==null)
			this.cid = new InstAttentionId();
		return this.cid.getUserCode();
	}
	
	public void setUserCode(String userCode) {
		if(this.cid==null)
			this.cid = new InstAttentionId();
		this.cid.setUserCode(userCode);
	}
  
	public Long getFlowInstId() {
		if(this.cid==null)
			this.cid = new InstAttentionId();
		return this.cid.getFlowInstId();
	}
	
	public void setFlowInstId(Long flowInstId) {
		if(this.cid==null)
			this.cid = new InstAttentionId();
		this.cid.setFlowInstId(flowInstId);
	}
	
	

	// Property accessors
  
	public Date getAttSetTime() {
		return this.attSetTime;
	}
	
	public void setAttSetTime(Date attSetTime) {
		this.attSetTime = attSetTime;
	}
  
	public String getAttSetUser() {
		return this.attSetUser;
	}
	
	public void setAttSetUser(String attSetUser) {
		this.attSetUser = attSetUser;
	}
  
	public String getAttSetMemo() {
		return this.attSetMemo;
	}
	
	public void setAttSetMemo(String attSetMemo) {
		this.attSetMemo = attSetMemo;
	}



	public void copy(InstAttention other){
  
		this.setUserCode(other.getUserCode());  
		this.setFlowInstId(other.getFlowInstId());
  
		this.attSetTime= other.getAttSetTime();  
		this.attSetUser= other.getAttSetUser();  
		this.attSetMemo= other.getAttSetMemo();

	}
	
	public void copyNotNullProperty(InstAttention other){
  
	if( other.getUserCode() != null)
		this.setUserCode(other.getUserCode());  
	if( other.getFlowInstId() != null)
		this.setFlowInstId(other.getFlowInstId());
  
		if( other.getAttSetTime() != null)
			this.attSetTime= other.getAttSetTime();  
		if( other.getAttSetUser() != null)
			this.attSetUser= other.getAttSetUser();  
		if( other.getAttSetMemo() != null)
			this.attSetMemo= other.getAttSetMemo();

	}
	
	public void clearProperties(){
  
		this.attSetTime= null;  
		this.attSetUser= null;  
		this.attSetMemo= null;

	}
}
