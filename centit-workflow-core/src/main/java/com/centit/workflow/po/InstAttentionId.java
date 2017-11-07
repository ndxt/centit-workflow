package com.centit.workflow.po;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * FAddressBook entity.
 * 
 * @author codefan@hotmail.com
 */ 
@Embeddable
public class InstAttentionId implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    @Column(name = "USER_CODE")
    private String userCode;

    @Column(name = "FLOW_INST_ID")
    private Long flowInstId;

    // Constructors
    /** default constructor */
    public InstAttentionId() {
    }
    /** full constructor */
    public InstAttentionId(String userCode, Long flowInstId) {

        this.userCode = userCode;
        this.flowInstId = flowInstId;
    }

  
    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
  
    public Long getFlowInstId() {
        return this.flowInstId;
    }

    public void setFlowInstId(Long flowInstId) {
        this.flowInstId = flowInstId;
    }


    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof InstAttentionId))
            return false;

        InstAttentionId castOther = (InstAttentionId) other;
        boolean ret = true;
  
        ret = ret && ( this.getUserCode() == castOther.getUserCode() ||
                       (this.getUserCode() != null && castOther.getUserCode() != null
                               && this.getUserCode().equals(castOther.getUserCode())));
  
        ret = ret && ( this.getFlowInstId() == castOther.getFlowInstId() ||
                       (this.getFlowInstId() != null && castOther.getFlowInstId() != null
                               && this.getFlowInstId().equals(castOther.getFlowInstId())));

        return ret;
    }

    public int hashCode() {
        int result = 17;
  
        result = 37 * result +
             (this.getUserCode() == null ? 0 :this.getUserCode().hashCode());
  
        result = 37 * result +
             (this.getFlowInstId() == null ? 0 :this.getFlowInstId().hashCode());

        return result;
    }
}
