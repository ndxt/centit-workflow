package com.centit.workflow.po;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * FAddressBook entity.
 *
 * @author codefan@hotmail.com
 */
@Embeddable
public class FlowVariableId implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**flowInstId 或者 flowGroupId */
    @Column(name = "FLOW_INST_ID")
    @ApiModelProperty(value = "流程实例编号", required = true)
    private String flowInstId;
    @Column(name = "RUN_TOKEN")
    @ApiModelProperty(value = "运行令牌")
    private String runToken;
    @Column(name = "VAR_NAME")
    @ApiModelProperty(value = "变量name", required = true)
    private String varName;

    // Constructors

    /**
     * default constructor
     */
    public FlowVariableId() {
    }

    /**
     * full constructor
     */
    public FlowVariableId(String flowInstId, String runToken, String varName) {

        this.flowInstId = flowInstId;
        this.runToken = FlowVariable.trimNodeToken(runToken);
        this.varName = varName;
    }


    public String getFlowInstId() {
        return this.flowInstId;
    }

    public void setFlowInstId(String flowInstId) {
        this.flowInstId = flowInstId;
    }

    public String getRunToken() {
        return this.runToken;
    }

    public void setRunToken(String runToken) {
        this.runToken = FlowVariable.trimNodeToken(runToken);
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

        ret = ret && (this.getFlowInstId() == castOther.getFlowInstId() ||
            (this.getFlowInstId() != null && castOther.getFlowInstId() != null
                && this.getFlowInstId().equals(castOther.getFlowInstId())));

        ret = ret && (this.getRunToken() == castOther.getRunToken() ||
            (this.getRunToken() != null && castOther.getRunToken() != null
                && this.getRunToken().equals(castOther.getRunToken())));

        ret = ret && (this.getVarName() == castOther.getVarName() ||
            (this.getVarName() != null && castOther.getVarName() != null
                && this.getVarName().equals(castOther.getVarName())));

        return ret;
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result +
            (this.getFlowInstId() == null ? 0 : this.getFlowInstId().hashCode());

        result = 37 * result +
            (this.getRunToken() == null ? 0 : this.getRunToken().hashCode());

        result = 37 * result +
            (this.getVarName() == null ? 0 : this.getVarName().hashCode());

        return result;
    }
}
