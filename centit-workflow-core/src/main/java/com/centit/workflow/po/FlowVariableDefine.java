package com.centit.workflow.po;

import javax.validation.constraints.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chen_rj on 2017-9-19.
 * 这个表更名为 WF_FLOW_VARIABLE_DEFINE
 *
 * 和这个对应 WF_FLOW_VARIABLE
 */
@Entity
@Table(name = "WF_FLOW_VARIABLE_DEFINE")
public class FlowVariableDefine implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Column(name = "FLOW_VARIABLE_ID")
    private String flowVariableId;
    @NotNull
    @Column(name = "FLOW_CODE")
    private String flowCode;
    @NotNull
    @Column(name = "VARIABLE_NAME")
    private String variableName;
    @Column(name = "VARIABLE_TYPE")
    private String variableType;
    @Column(name = "VARIABLE_ORDER")
    private Integer variableOrder;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

    public String getFlowVariableId() {
        return flowVariableId;
    }

    public void setFlowVariableId(String flowVariableId) {
        this.flowVariableId = flowVariableId;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public Integer getVariableOrder() {
        return variableOrder;
    }

    public void setVariableOrder(Integer variableOrder) {
        this.variableOrder = variableOrder;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
