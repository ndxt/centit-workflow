package com.centit.workflow.service.impl;

import com.centit.support.algorithm.UuidOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.ApprRoleDao;
import com.centit.workflow.dao.ApprRoleDefineDao;
import com.centit.workflow.dao.RoleFormulaDao;
import com.centit.workflow.po.ApprRole;
import com.centit.workflow.po.ApprRoleDefine;
import com.centit.workflow.po.RoleFormula;
import com.centit.workflow.service.ApprRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApprRoleServiceImpl implements ApprRoleService {

    @Autowired
    private ApprRoleDao apprRoleDao;

    @Autowired
    private ApprRoleDefineDao apprRoleDefineDao;

    @Autowired
    private RoleFormulaDao roleFormulaDao;

    //审批角色列表
    @Override
    @Transactional
    public List<ApprRole> listApprRoles(Map<String, Object> filterMap, PageDesc pageDesc) {
        return apprRoleDao.listObjectsByProperties(filterMap,pageDesc);
    }

    @Override
    @Transactional
    public ApprRole getApprRoleByCode(String roleCode) {
        return apprRoleDao.getObjectById(roleCode);
    }

    @Override
    @Transactional
    public void saveApprRole(ApprRole apprRole) {
        if(apprRole == null){
            return;
        }
        if(apprRole.getRoleCode() == null || apprRole.getRoleCode().isEmpty()){
            apprRole.setRoleCode(UuidOpt.getUuidAsString32());
        }
        apprRoleDao.mergeObject(apprRole);

        RoleFormula formula = new RoleFormula();
        formula.setFormulaCode(apprRole.getRoleCode());
        formula.setFormulaName(apprRole.getRoleName());
        formula.setRoleLevel(0);
        roleFormulaDao.mergeObject(formula);
    }

    @Override
    @Transactional
    public void deleteApprRoleByCode(String roleCode) {
        apprRoleDao.deleteObjectById(roleCode);
        roleFormulaDao.deleteObjectById(roleCode);
    }

    @Override
    @Transactional
    public void updateApprRole(ApprRole apprRole) {
        apprRoleDao.updateObject(apprRole);

        RoleFormula formula = new RoleFormula();
        formula.setFormulaCode(apprRole.getRoleCode());
        formula.setFormulaName(apprRole.getRoleName());
        formula.setRoleLevel(0);
        roleFormulaDao.updateObject(formula);
    }

    @Override
    public List<ApprRoleDefine> getApprRoleDefineListByCode(String roleCode) {
        return apprRoleDefineDao.listObjectsByProperty("roleCode", roleCode);
//        return apprRoleDefineDao.getApprRoleDefineByRoleCode(roleCode);
    }

    @Override
    @Transactional
    public void deleteApprRoleDefineById(String id) {
        ApprRoleDefine apprRoleDefine = apprRoleDefineDao.getObjectById(id);
        apprRoleDefineDao.deleteObjectById(id);
        updateFormula(apprRoleDefine.getRoleCode());

    }

    @Override
    @Transactional
    public void saveApprRoleDefine(ApprRoleDefine apprRoleDefine) {
        if(apprRoleDefine == null){
            return;
        }
        if(apprRoleDefine.getId() == null || apprRoleDefine.getId().isEmpty()){
            apprRoleDefine.setId(UuidOpt.getUuidAsString32());
        }
        apprRoleDefineDao.mergeObject(apprRoleDefine);
    }

    @Override
    @Transactional
    public void updateFormula(String roleCode) {
        String formulaString = getFormula(roleCode);
        RoleFormula formula = roleFormulaDao.getObjectById(roleCode);
        formula.setRoleFormula(formulaString);
        roleFormulaDao.updateObject(formula);
    }

    /**
     * 根据 审批角色明细 生成 权限表达式 形如 XZ('10','11')||RO('16', '17')
     * @param roleCode
     * @return
     */
    private String getFormula(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            return null;
        }

        List<ApprRoleDefine> roleDefines = apprRoleDefineDao.listObjectsByProperty("roleCode", roleCode);
        Map<String, String> roleTypeAndCode = new HashMap<>();
        for (ApprRoleDefine roleDefine : roleDefines) {
            if (!roleTypeAndCode.containsKey(roleDefine.getRelatedType())) {
                roleTypeAndCode.put(roleDefine.getRelatedType(), "'" + roleDefine.getRelatedCode() + "'");
            } else {
                String formulaCode = roleTypeAndCode.get(roleDefine.getRelatedType());
                roleTypeAndCode.put(roleDefine.getRelatedType(), formulaCode + "," + "'" + roleDefine.getRelatedCode() + "'");
            }
        }

        if (!roleTypeAndCode.isEmpty()) {
            String formula = "";
            for (Map.Entry<String, String> ent : roleTypeAndCode.entrySet()) {
                String itemExp = ent.getKey().equalsIgnoreCase("js") ? "D(N)RO" : "D(N)XZ"; // 用户角色 or 行政职务
                formula += itemExp + "(" + ent.getValue() + ")||";
            }
            return formula.substring(0, formula.lastIndexOf("||"));
        }

        return null;
    }

    /**
     * 将审批角色表里的旧数据 同步到 权限表达式表
     * @return
     */
    @Override
    @Transactional
    public boolean syncApprRoleToFormula() {
        List<ApprRole> apprRoles = apprRoleDao.listObjects();
        for (ApprRole apprRole : apprRoles) {
            RoleFormula formula = roleFormulaDao.getObjectById(apprRole.getRoleCode());
            if (null == formula) {
                RoleFormula newFormula = new RoleFormula();
                newFormula.setFormulaCode(apprRole.getRoleCode());
                newFormula.setFormulaName(apprRole.getRoleName());
                newFormula.setRoleLevel(0);
                newFormula.setRoleFormula(getFormula(apprRole.getRoleCode()));
                roleFormulaDao.saveNewObject(newFormula);
            }
        }

        apprRoleDao.updateNodeSPToSF();
        apprRoleDao.updateNodeUnitExp();
//        apprRoleDao.updateNodeUnitExp2();

        return true;
    }
}
