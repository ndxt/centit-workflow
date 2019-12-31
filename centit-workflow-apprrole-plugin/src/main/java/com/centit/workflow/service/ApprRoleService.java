package com.centit.workflow.service;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.ApprRole;
import com.centit.workflow.po.ApprRoleDefine;

import java.util.List;
import java.util.Map;

public interface ApprRoleService {

    List<ApprRole> listApprRoles(Map<String, Object> filterMap, PageDesc pageDesc);

    ApprRole getApprRoleByCode(String roleCode);

    void saveApprRole(ApprRole ApprRole);

    void deleteApprRoleByCode(String roleCode);

    void updateApprRole(ApprRole apprRole);

    List<ApprRoleDefine> getApprRoleDefineListByCode(String roleCode);

    void deleteApprRoleDefineById(String id);

    void saveApprRoleDefine(ApprRoleDefine apprRoleDefine);

    void updateFormula(String roleCode);

}
