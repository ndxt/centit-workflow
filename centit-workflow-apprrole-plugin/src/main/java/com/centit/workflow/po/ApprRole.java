package com.centit.workflow.po;

import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "WF_FLOW_ROLE")
public class ApprRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ROLE_CODE")
    @ValueGenerator(strategy = GeneratorType.UUID)
    private String roleCode;

    @Column(name = "ROLE_NAME")
    private String roleName;

    @Column(name = "ROLE_STATE")
    private String roleState;

    private List<ApprRoleDefine> apprRoleDefineList;

    public List<ApprRoleDefine> getApprRoleDefineList() {
        return apprRoleDefineList;
    }

    public void setApprRoleDefineList(List<ApprRoleDefine> apprRoleDefineList) {
        this.apprRoleDefineList = apprRoleDefineList;
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

    public String getRoleState() {
        return roleState;
    }

    public void setRoleState(String roleState) {
        this.roleState = roleState;
    }
}

