package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName FlowRole/审批角色表
 * @Date 2019/7/22 15:32
 * @Version 1.0
 */
@Entity
@Table(name = "WF_FLOW_ROLE")
public class FlowRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ROLE_CODE")
    private String roleCode;

    @Column(name = "ROLE_NAME")
    private String roleName;
    @Column(name = "role_level")
    private String roleLevel;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "ROLE_LEVEL")
    private Integer roleLevel;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    private List<FlowRoleDefine> flowRoleDefineList;

    public List<FlowRoleDefine> getFlowRoleDefineList() {
        return flowRoleDefineList;
    }

    public void setFlowRoleDefineList(List<FlowRoleDefine> flowRoleDefineList) {
        this.flowRoleDefineList = flowRoleDefineList;
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

    public Integer getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(Integer roleLevel) {
        this.roleLevel = roleLevel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
