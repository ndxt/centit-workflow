package com.centit.workflow.po;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * FAddressBook entity.
 *
 * @author codefan@hotmail.com
 */
@Embeddable
public class FlowInfoId implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    @Column(name = "VERSION")
    @NotNull
    @Range( max = 9999)
    private Long version;

    @Column(name = "FLOW_CODE")
    @NotBlank
    @Length(max = 32)
    private String flowCode;

    // Constructors
    /** default constructor */
    public FlowInfoId() {
    }
    /** full constructor */
    public FlowInfoId(Long version, String wfcode) {
        this.version = version;
        this.flowCode = wfcode;
    }


    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String wfcode) {
        this.flowCode = wfcode;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof FlowInfoId))
            return false;

        FlowInfoId castOther = (FlowInfoId) other;
        boolean ret = true;

        ret = ret && ( this.getVersion() == castOther.getVersion() ||
                       (this.getVersion() != null && castOther.getVersion() != null
                               && this.getVersion().equals(castOther.getVersion())));

        ret = ret && ( this.getFlowCode() == castOther.getFlowCode() ||
                       (this.getFlowCode() != null && castOther.getFlowCode() != null
                               && this.getFlowCode().equals(castOther.getFlowCode())));

        return ret;
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result +
            (this.getVersion() == null ? 0 :this.getVersion().hashCode());

        result = 37 * result +
            (this.getFlowCode() == null ? 0 :this.getFlowCode().hashCode());

        return result;
    }
}
