package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
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
@Entity
@Table(name = "WF_FLOW_TEAM_ROLE")
public class FlowTeamRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Column(name = "FLOW_TEAM_ROLE_ID")
    private String flowTeamRoleId;
    @NotNull
    @Column(name = "FLOW_CODE")
    private String flowCode;
    @NotNull
    @Column(name = "ROLE_CODE")
    private String roleCode;
    @NotNull
    @Column(name = "ROLE_NAME")
    private String roleName;
    @Column(name = "TEAM_ROLE_ORDER")
    @OrderBy(value = "ASC")
    private Integer teamRoleOrder;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getFlowTeamRoleId() {
        return flowTeamRoleId;
    }

    public void setFlowTeamRoleId(String flowTeamRoleId) {
        this.flowTeamRoleId = flowTeamRoleId;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getTeamRoleOrder() {
        return teamRoleOrder;
    }

    public void setTeamRoleOrder(Integer teamRoleOrder) {
        this.teamRoleOrder = teamRoleOrder;
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

    public void copyNotNullProperty(FlowTeamRole other){

        if( other.getFlowTeamRoleId() != null)
            this.setFlowTeamRoleId(other.getFlowTeamRoleId());
        /*if( other.getFlowDefine() != null)
            this.flowDefine = other.getFlowDefine();*/
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
        this.createTime= other.getCreateTime();
        this.modifyTime= other.getModifyTime();

    }
}
