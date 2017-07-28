package com.centit.workflow.client.po;

/**
 * FAddressBook entity.
 * 
 * @author codefan@hotmail.com
 */ 
public class FlowOrganizeId implements java.io.Serializable {
	private static final long serialVersionUID =  1L;

	private Long flowInstId;

	private String unitCode;

	private String roleCode;

	// Constructors
	/** default constructor */
	public FlowOrganizeId() {
	}
	/** full constructor */
	public FlowOrganizeId(Long flowInstId, String unitCode, String roleCode) {

		this.flowInstId = flowInstId;
		this.unitCode = unitCode;
		this.roleCode = roleCode;	
	}

  
	public Long getFlowInstId() {
		return this.flowInstId;
	}

	public void setFlowInstId(Long flowInstId) {
		this.flowInstId = flowInstId;
	}
  
	public String getUnitCode() {
		return this.unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
  
	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}


	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof FlowOrganizeId))
			return false;
		
		FlowOrganizeId castOther = (FlowOrganizeId) other;
		boolean ret = true;
  
		ret = ret && ( this.getFlowInstId() == castOther.getFlowInstId() ||
					   (this.getFlowInstId() != null && castOther.getFlowInstId() != null
							   && this.getFlowInstId().equals(castOther.getFlowInstId())));
  
		ret = ret && ( this.getUnitCode() == castOther.getUnitCode() ||
					   (this.getUnitCode() != null && castOther.getUnitCode() != null
							   && this.getUnitCode().equals(castOther.getUnitCode())));
  
		ret = ret && ( this.getRoleCode() == castOther.getRoleCode() ||
					   (this.getRoleCode() != null && castOther.getRoleCode() != null
							   && this.getRoleCode().equals(castOther.getRoleCode())));

		return ret;
	}
	
	public int hashCode() {
		int result = 17;
  
		result = 37 * result +
		 	(this.getFlowInstId() == null ? 0 :this.getFlowInstId().hashCode());
  
		result = 37 * result +
		 	(this.getUnitCode() == null ? 0 :this.getUnitCode().hashCode());
  
		result = 37 * result +
		 	(this.getRoleCode() == null ? 0 :this.getRoleCode().hashCode());
	
		return result;
	}
}
