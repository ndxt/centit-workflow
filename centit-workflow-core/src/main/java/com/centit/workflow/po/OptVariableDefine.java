package com.centit.workflow.po;

import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 和流程业务关联
 */
@Data
@Entity
@Table(name = "WF_OPT_VARIABLE_DEFINE")
@ApiModel("流程变量定义表")
public class OptVariableDefine implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @ValueGenerator(strategy = GeneratorType.UUID)
    @Column(name = "OPT_VARIABLE_ID")
    @ApiModelProperty(value = "主键id")
    private String optVariableId;

    @NotNull
    @Column(name = "OPT_ID")
    @ApiModelProperty(value = "关联的流程业务id")
    private String optId;

    @NotNull
    @Column(name = "VARIABLE_NAME")
    @ApiModelProperty(value = "变量名")
    private String variableName;

    @Column(name = "VARIABLE_DESC")
    @ApiModelProperty(value = "变量中文描述")
    private String variableDesc;

    @Column(name = "VARIABLE_TYPE")
    @ApiModelProperty(value = "变量类型：E:集合 S:单值")
    private String variableType;

    @Column(name = "DEFAULT_VALUE")
    @ApiModelProperty(value = "变量默认值")
    private String defaultValue;

    @Column(name = "MODIFY_TIME")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, condition = GeneratorCondition.ALWAYS,
        value = "today()")
    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;


    public void copyNotNullProperty(OptVariableDefine other){
        if( other.getOptVariableId() != null)
            this.setOptVariableId(other.getOptVariableId());
        if( other.getOptId() != null)
            this.optId= other.getOptId();
        if( other.getVariableName() != null)
            this.variableName= other.getVariableName();
        if( other.getVariableDesc() != null)
            this.variableDesc = other.getVariableDesc();
        if( other.getVariableType() != null)
            this.variableType = other.getVariableType();
    }

    public void copy(OptVariableDefine other){
        this.setOptVariableId(other.getOptVariableId());
        this.variableType= other.getVariableType();
        this.variableName= other.getVariableName();
        this.modifyTime= other.getModifyTime();
        this.optId= other.getOptId();
        this.variableDesc = other.getVariableDesc();
    }
}
