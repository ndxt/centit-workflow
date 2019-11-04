package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chen_rj on 2017-9-19.
 * 这个表更名为 WF_FLOW_VARIABLE_DEFINE
 *
 * 和这个对应 WF_FLOW_VARIABLE
 */
@Data
@Entity
@Table(name = "WF_FLOW_VARIABLE_DEFINE")
public class FlowVariableDefine implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @Column(name = "FLOW_VARIABLE_ID")
    private String flowVariableId;

    @NotNull
    @Column(name = "FLOW_CODE")
    private String flowCode;

    @Column(name = "VERSION")
    @NotNull(message = "字段不能为空")
    @Range( max = 9999, message = "版本号不能大于{max}")
    private Long version;

    /**
     * 变量名
     */
    @NotNull
    @Column(name = "VARIABLE_NAME")
    private String variableName;

    /**
     * 变量中文描述
     */
    @Column(name = "VARIABLE_DESC")
    private String variableDesc;

    @Column(name = "VARIABLE_TYPE")
    //E:集合 S:单值
    private String variableType;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    @Column(name = "MODIFY_TIME")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, condition = GeneratorCondition.ALWAYS,
    value = "today()")
    private Date modifyTime;

    @OneToOne(targetEntity = FlowInfo.class)
    @JoinColumns({
        @JoinColumn(name="flowCode", referencedColumnName = "flowCode"),
        @JoinColumn(name="version", referencedColumnName = "version")
    })
    @JSONField(serialize=false)
    private FlowInfo flowDefine;

    public void copyNotNullProperty(FlowVariableDefine other){
        if( other.getFlowVariableId() != null)
            this.setFlowVariableId(other.getFlowVariableId());
        /*if( other.getFlowDefine() != null)
            this.flowDefine = other.getFlowDefine();*/
        if( other.getFlowCode() != null)
            this.flowCode= other.getFlowCode();
        if( other.getVariableName() != null)
            this.variableName= other.getVariableName();
        if( other.getVariableDesc() != null)
            this.variableDesc = other.getVariableDesc();
        if( other.getVariableType() != null)
            this.variableType = other.getVariableType();
    }

    public void copy(FlowVariableDefine other){
        this.setFlowVariableId(other.getFlowVariableId());
        //this.setVersion(other.getVersion());
        //this.setFlowCode(other.getFlowCode());
        this.variableType= other.getVariableType();
        this.variableName= other.getVariableName();
        this.modifyTime= other.getModifyTime();
        this.flowCode= other.getFlowCode();
        this.variableDesc = other.getVariableDesc();
    }
}
