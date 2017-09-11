package com.centit.workflow.po;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * FAddressBook entity.
 * 
 * @author codefan@hotmail.com
 */ 
@Embeddable
public class FlowVariableId implements java.io.Serializable {
	private static final long serialVersionUID =  1L;
	 @Column(name = "FLOW_INST_ID")
	private Long flowInstId;
	 @Column(name = "RUN_TOKEN")
	private String runToken;
	 @Column(name = "VAR_NAME")
	private String varName;

	// Constructors
	/** default constructor */
	public FlowVariableId() {
	}
	/** full constructor */
	public FlowVariableId(Long flowInstId, String runToken, String varName) {

		this.flowInstId = flowInstId;
		this.runToken = runToken;
		this.varName = varName;	
	}

  
	public Long getFlowInstId() {
		return this.flowInstId;
	}

	public void setFlowInstId(Long flowInstId) {
		this.flowInstId = flowInstId;
	}
  
	public String getRunToken() {
		return this.runToken;
	}

	public void setRunToken(String runToken) {
		this.runToken = runToken;
	}
  
	public String getVarName() {
		return this.varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}


	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof FlowVariableId))
			return false;
		
		FlowVariableId castOther = (FlowVariableId) other;
		boolean ret = true;
  
		ret = ret && ( this.getFlowInstId() == castOther.getFlowInstId() ||
					   (this.getFlowInstId() != null && castOther.getFlowInstId() != null
							   && this.getFlowInstId().equals(castOther.getFlowInstId())));
  
		ret = ret && ( this.getRunToken() == castOther.getRunToken() ||
					   (this.getRunToken() != null && castOther.getRunToken() != null
							   && this.getRunToken().equals(castOther.getRunToken())));
  
		ret = ret && ( this.getVarName() == castOther.getVarName() ||
					   (this.getVarName() != null && castOther.getVarName() != null
							   && this.getVarName().equals(castOther.getVarName())));

		return ret;
	}
	
	public int hashCode() {
		int result = 17;
  
		result = 37 * result +
		 	(this.getFlowInstId() == null ? 0 :this.getFlowInstId().hashCode());
  
		result = 37 * result +
		 	(this.getRunToken() == null ? 0 :this.getRunToken().hashCode());
  
		result = 37 * result +
		 	(this.getVarName() == null ? 0 :this.getVarName().hashCode());
	
		return result;
	}
}
