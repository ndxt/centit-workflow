package com.centit.workflow.po;

import com.centit.framework.core.dao.DictionaryMap;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * FAddressBook entity.
 *
 * @author codefan@hotmail.com
 */
@Embeddable
public class FlowOrganizeId implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    /**flowInstId 或者 flowGroupId */
    @Column(name = "FLOW_INST_ID")
    private String flowInstId;

    @DictionaryMap(value="unitCode", fieldName="unitName")
    @Column(name = "UNIT_CODE")
    private String unitCode;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    // Constructors
    /** default constructor */
    public FlowOrganizeId() {
    }
    /** full constructor */
    public FlowOrganizeId(String flowInstId, String unitCode, String roleCode) {

        this.flowInstId = flowInstId;
        this.unitCode = unitCode;
        this.roleCode = roleCode;
    }


    public String getFlowInstId() {
        return this.flowInstId;
    }

    public void setFlowInstId(String flowInstId) {
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
