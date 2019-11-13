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
 * 这个更名为 WF_FLOW_TEAM_ROLE
 * 和流程关联
 */
@Data
@Entity
@Table(name = "WF_FLOW_TEAM_ROLE")
public class FlowTeamRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @ValueGenerator(strategy = GeneratorType.UUID)
    @Column(name = "FLOW_TEAM_ROLE_ID")
    private String flowTeamRoleId;

    @NotNull
    @Column(name = "FLOW_CODE")
    private String flowCode;

    @Column(name = "VERSION")
    @NotNull(message = "字段不能为空")
    @Range( max = 9999, message = "版本号不能大于{max}")
    private Long version;

    @NotNull
    @Column(name = "ROLE_CODE")
    private String roleCode;

    @NotNull
    @Column(name = "ROLE_NAME")
    private String roleName;

    /**
     * 办件角色的约束范围
     */
    @Column(name = "FORMULA_CODE")
    private String formulaCode;

    @Column(name = "TEAM_ROLE_ORDER")
    @OrderBy(value = "ASC")
    private Integer teamRoleOrder;

    @Column(name = "MODIFY_TIME")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, condition = GeneratorCondition.ALWAYS,
        value = "today()")
    private Date modifyTime;

    @JSONField(serialize=false)
    private FlowInfo flowDefine;

    public void copyNotNullProperty(FlowTeamRole other){

        if( other.getFlowTeamRoleId() != null)
            this.setFlowTeamRoleId(other.getFlowTeamRoleId());
        if( other.getFormulaCode() != null)
            this.formulaCode = other.getFormulaCode();
        if( other.getFlowCode() != null)
            this.flowCode= other.getFlowCode();
        if( other.getRoleCode() != null)
            this.roleCode= other.getRoleCode();
        if( other.getRoleName() != null)
            this.roleName= other.getRoleName();
        if( other.getTeamRoleOrder() != null)
            this.teamRoleOrder= other.getTeamRoleOrder();
    }

    public void copy(FlowTeamRole other){
        this.setFlowTeamRoleId(other.getFlowTeamRoleId());
        //this.setVersion(other.getVersion());
        //this.setFlowCode(other.getFlowCode());
        this.roleName= other.getRoleName();
        this.roleCode= other.getRoleCode();
        this.teamRoleOrder= other.getTeamRoleOrder();
        this.flowCode= other.getFlowCode();
        this.formulaCode= other.getFormulaCode();
        this.modifyTime= other.getModifyTime();
    }
}
