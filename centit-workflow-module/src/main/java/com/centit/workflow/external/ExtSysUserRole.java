package com.centit.workflow.external;

import com.centit.framework.model.basedata.IUserRole;

/**
 * Created by codefan on 17-9-12.
 */
public class ExtSysUserRole implements IUserRole {
    private String userCode;
    private String roleCode;
    private String obtainType;
    private String inheritedFrom;

    public ExtSysUserRole(){

    }

    @Override
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public String getObtainType() {
        return obtainType;
    }

    public void setObtainType(String obtainType) {
        this.obtainType = obtainType;
    }

    @Override
    public String getInheritedFrom() {
        return inheritedFrom;
    }

    public void setInheritedFrom(String inheritedFrom) {
        this.inheritedFrom = inheritedFrom;
    }
}
