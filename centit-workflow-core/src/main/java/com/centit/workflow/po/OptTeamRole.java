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
@Table(name = "WF_OPT_TEAM_ROLE")
@ApiModel("办件角色表")
public class OptTeamRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @ValueGenerator(strategy = GeneratorType.UUID)
    @Column(name = "OPT_TEAM_ROLE_ID")
    @ApiModelProperty(value = "主键id")
    private String optTeamRoleId;

    @NotNull
    @Column(name = "OPT_ID")
    @ApiModelProperty(value = "关联的流程业务id")
    private String optId;

    @NotNull
    @Column(name = "ROLE_CODE")
    @ApiModelProperty(value = "办件角色code")
    private String roleCode;

    @NotNull
    @Column(name = "ROLE_NAME")
    @ApiModelProperty(value = "办件角色名称")
    private String roleName;

    @Column(name = "FORMULA_CODE")
    @ApiModelProperty(value = "办件角色的约束范围")
    private String formulaCode;

    @Column(name = "TEAM_ROLE_ORDER")
    @OrderBy(value = "ASC")
    @ApiModelProperty(value = "办件角色排序")
    private Long teamRoleOrder;

    @Column(name = "MODIFY_TIME")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, condition = GeneratorCondition.ALWAYS,
        value = "today()")
    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;


    public void copyNotNullProperty(OptTeamRole other){

        if( other.getOptTeamRoleId() != null)
            this.setOptTeamRoleId(other.getOptTeamRoleId());
        if( other.getFormulaCode() != null)
            this.formulaCode = other.getFormulaCode();
        if( other.getOptId() != null)
            this.optId= other.getOptId();
        if( other.getRoleCode() != null)
            this.roleCode= other.getRoleCode();
        if( other.getRoleName() != null)
            this.roleName= other.getRoleName();
        if( other.getTeamRoleOrder() != null)
            this.teamRoleOrder= other.getTeamRoleOrder();
    }

    public void copy(OptTeamRole other){
        this.setOptTeamRoleId(other.getOptTeamRoleId());
        this.roleName= other.getRoleName();
        this.roleCode= other.getRoleCode();
        this.teamRoleOrder= other.getTeamRoleOrder();
        this.optId= other.getOptId();
        this.formulaCode= other.getFormulaCode();
        this.modifyTime= other.getModifyTime();
    }
}
