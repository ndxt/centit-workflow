package com.centit.workflow.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
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
}
