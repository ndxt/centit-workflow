package com.centit.workflow.service.impl;

import com.centit.support.algorithm.UuidOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.ApprRoleDao;
import com.centit.workflow.dao.ApprRoleDefineDao;
import com.centit.workflow.po.ApprRole;
import com.centit.workflow.po.ApprRoleDefine;
import com.centit.workflow.service.ApprRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ApprRoleServiceImpl implements ApprRoleService {

    @Autowired
    private ApprRoleDao apprRoleDao;

    @Autowired
    private ApprRoleDefineDao apprRoleDefineDao;

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
    }

    @Override
    @Transactional
    public void deleteApprRoleByCode(String roleCode) {
        apprRoleDao.deleteObjectById(roleCode);
    }

    @Override
    public List<ApprRoleDefine> getApprRoleDefineListByCode(String roleCode) {
        return apprRoleDefineDao.listObjectsByProperty("roleCode", roleCode);
//        return apprRoleDefineDao.getApprRoleDefineByRoleCode(roleCode);
    }

    @Override
    @Transactional
    public void deleteApprRoleDefineById(String id) {
        apprRoleDefineDao.deleteObjectById(id);
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
}
