package com.centit.workflow.po;

import com.centit.support.algorithm.StringBaseOpt;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;


/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */
@Entity
@Table(name = "WF_FLOW_VARIABLE")
public class FlowVariable implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private FlowVariableId cid;

    @Column(name = "VAR_VALUE")
    @ApiModelProperty(value = "变量值", required = true)
    private String varValue;

    @Column(name = "VAR_TYPE")
    //E:集合 S:单个字符串
    @ApiModelProperty(value = "变量类型")
    private String varType;

    // Constructors
    /**
     * default constructor
     */
    public FlowVariable() {
    }

    public static String FLOW_VARIABLE_TYPE_COLLECTION = "E";
    public static String FLOW_VARIABLE_TYPE_SINGLE = "S";
    /**
     * full constructor
     *
     * @param id 主键
     * @param varValue 数值
     * @param varType  E:集合 S:单个字符串
     */
    public FlowVariable(FlowVariableId id
        , String varValue, String varType) {
        this.cid = id;
        this.varValue = varValue;
        this.varType = varType;
    }

    /**
     * @param flowInstId 流程节点
     * @param runToken 令牌
     * @param varName 变量名
     * @param varValue 值
     * @param varType    E:集合 S:单个字符串
     */
    public FlowVariable(String flowInstId, String runToken, String varName, String varValue, String varType) {
        this.cid = new FlowVariableId(flowInstId, runToken, varName);
        this.varValue = varValue;
        this.varType = varType;
    }

    public FlowVariableId getCid() {
        return this.cid;
    }

    public void setCid(FlowVariableId id) {
        this.cid = id;
    }

    @ApiModelProperty(value = "流程实例编号", required = true)
    public String getFlowInstId() {
        if (this.cid == null)
            this.cid = new FlowVariableId();
        return this.cid.getFlowInstId();
    }

    public void setFlowInstId(String flowInstId) {
        if (this.cid == null)
            this.cid = new FlowVariableId();
        this.cid.setFlowInstId(flowInstId);
    }

    @ApiModelProperty(value = "运行令牌")
    public String getRunToken() {
        if (this.cid == null)
            this.cid = new FlowVariableId();
        return this.cid.getRunToken();
    }

    public void setRunToken(String runToken) {
        if (this.cid == null)
            this.cid = new FlowVariableId();
        this.cid.setRunToken(runToken);
    }

    @ApiModelProperty(value = "变量name", required = true)
    public String getVarName() {
        if (this.cid == null)
            this.cid = new FlowVariableId();
        return this.cid.getVarName();
    }

    public void setVarName(String varName) {
        if (this.cid == null)
            this.cid = new FlowVariableId();
        this.cid.setVarName(varName);
    }

    // Property accessors
    public String getVarValue() {
        return this.varValue;
    }


    public List<String> getVarList() {
        if (FLOW_VARIABLE_TYPE_COLLECTION.equals(varType)) {
            return StringBaseOpt.objectToStringList(this.varValue);
        }
        List<String> sVS = new ArrayList<>();
        sVS.add(varValue);
        return sVS;
    }

    public void setVarValue(String varValue) {
        this.varValue = varValue;
    }

    /**
     * E:集合 S:单个字符串
     */
    public String getVarType() {
        return this.varType;
    }

    /**
     * E:集合 S:单个字符串
     * @param varType
     */
    public void setVarType(String varType) {
        this.varType = varType;
    }


    public void copy(FlowVariable other) {
        this.setFlowInstId(other.getFlowInstId());
        this.setRunToken(other.getRunToken());
        this.setVarName(other.getVarName());
        this.varValue = other.getVarValue();
        this.varType = other.getVarType();

    }

    public void copyNotNullProperty(FlowVariable other) {
        if (other.getFlowInstId() != null)
            this.setFlowInstId(other.getFlowInstId());
        if (other.getRunToken() != null)
            this.setRunToken(other.getRunToken());
        if (other.getVarName() != null)
            this.setVarName(other.getVarName());
        if (other.getVarValue() != null)
            this.varValue = other.getVarValue();
        if (other.getVarType() != null)
            this.varType = other.getVarType();
    }

    public void clearProperties() {
        this.varValue = null;
        this.varType = null;
    }
}
