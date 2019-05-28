package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.validator.constraints.Range;

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
    private Long flowVariableId;
    @NotNull
    @Column(name = "FLOW_CODE")
    private String flowCode;
    @NotNull
    @Column(name = "VARIABLE_NAME")
    private String variableName;
    @Column(name = "VARIABLE_TYPE")
    //E:集合 S:单个字符串
    private String variableType;
    @Column(name = "VARIABLE_ORDER")
    private Integer variableOrder;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

    @Column(name = "VERSION")
    @NotNull(message = "字段不能为空")
    @Range( max = 9999, message = "版本号不能大于{max}")
    private Long version;

    @JSONField(serialize=false)
    private FlowInfo flowDefine;

    public FlowInfo getFlowDefine() {
        return flowDefine;
    }
    public void setFlowDefine(FlowInfo flowDefine) {
        this.flowDefine = flowDefine;
    }

    public void setFlowVariableId(Long flowVariableId) {
        this.flowVariableId = flowVariableId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getFlowVariableId() {
        return flowVariableId;
    }

    public void setFlowVariableId(long flowVariableId) {
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

    public void copyNotNullProperty(FlowVariableDefine other){

        if( other.getFlowVariableId() != null)
            this.setFlowVariableId(other.getFlowVariableId());
        /*if( other.getFlowDefine() != null)
            this.flowDefine = other.getFlowDefine();*/
        if( other.getFlowCode() != null)
            this.flowCode= other.getFlowCode();
        if( other.getVariableName() != null)
            this.variableName= other.getVariableName();
        if( other.getVariableType() != null)
            this.variableType = other.getVariableType();
        if( other.getVariableOrder() != null)
            this.variableOrder= other.getVariableOrder();
    }

    public void copy(FlowVariableDefine other){
        this.setFlowVariableId(other.getFlowVariableId());
        //this.setVersion(other.getVersion());
        //this.setFlowCode(other.getFlowCode());
        this.variableOrder= other.getVariableOrder();
        this.variableType= other.getVariableType();
        this.variableName= other.getVariableName();
        this.createTime= other.getCreateTime();
        this.modifyTime= other.modifyTime;
        this.flowCode= other.getFlowCode();

    }
}
