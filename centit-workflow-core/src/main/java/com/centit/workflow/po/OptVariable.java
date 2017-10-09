package com.centit.workflow.po;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chen_rj on 2017-9-19.
 */
@Entity
@Table(name = "OPT_VARIABLE")
public class OptVariable implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Column(name = "OPT_VARIABLE_ID")
    private String optVariableId;
    @NotNull
    @Column(name = "OPT_ID")
    private String optId;
    @NotNull
    @Column(name = "OPT_VARIABLE_NAME")
    private String optVariableName;
    @Column(name = "OPT_VARIABLE_TYPE")
    private String optVariableType;
    @Column(name = "OPT_VARIABLE_ORDER")
    private Integer optVariableOrder;
    @Column(name = "GMT_CREATE_TIME")
    private Date gmtCreateTime;
    @Column(name = "GMT_MODIFY_TIME")
    private Date gmtModifyTime;

    public String getOptVariableId() {
        return optVariableId;
    }

    public void setOptVariableId(String optVariableId) {
        this.optVariableId = optVariableId;
    }

    public String getOptId() {
        return optId;
    }

    public void setOptId(String optId) {
        this.optId = optId;
    }

    public String getOptVariableName() {
        return optVariableName;
    }

    public void setOptVariableName(String optVariableName) {
        this.optVariableName = optVariableName;
    }

    public String getOptVariableType() {
        return optVariableType;
    }

    public void setOptVariableType(String optVariableType) {
        this.optVariableType = optVariableType;
    }

    public Integer getOptVariableOrder() {
        return optVariableOrder;
    }

    public void setOptVariableOrder(Integer optVariableOrder) {
        this.optVariableOrder = optVariableOrder;
    }

    public Date getGmtCreateTime() {
        return gmtCreateTime;
    }

    public void setGmtCreateTime(Date gmtCreateTime) {
        this.gmtCreateTime = gmtCreateTime;
    }

    public Date getGmtModifyTime() {
        return gmtModifyTime;
    }

    public void setGmtModifyTime(Date gmtModifyTime) {
        this.gmtModifyTime = gmtModifyTime;
    }
}
