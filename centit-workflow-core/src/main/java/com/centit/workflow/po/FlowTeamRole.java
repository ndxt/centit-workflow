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
    private Integer teamRoleOrder;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

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
}
