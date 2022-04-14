package com.centit.workflow.service.impl;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.OptTeamRoleDao;
import com.centit.workflow.dao.OptVariableDefineDao;
import com.centit.workflow.po.OptTeamRole;
import com.centit.workflow.po.OptVariableDefine;
import com.centit.workflow.service.FlowOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:47:39
 */
@Service
public class FlowOptServiceImpl implements FlowOptService {

    @Autowired
    private OptTeamRoleDao optTeamRoleDao;

    @Autowired
    private OptVariableDefineDao optVariableDefineDao;

    @Override
    public String getOptDefSequenceId() {
        return null;
    }

    @Override
    public List<OptTeamRole> listOptTeamRolesByFilter(Map<String, Object> filter, PageDesc pageDesc) {
        return optTeamRoleDao.listObjects(filter, pageDesc);
    }

    @Override
    public OptTeamRole getOptTeamRoleById(String roleId) {
        return optTeamRoleDao.getObjectById(roleId);
    }

    @Override
    @Transactional
    public void saveOptTeamRole(OptTeamRole optTeamRole) {
        optTeamRoleDao.saveNewObject(optTeamRole);
    }

    @Override
    @Transactional
    public void updateOptTeamRole(OptTeamRole optTeamRole) {
        optTeamRoleDao.updateObject(optTeamRole);
    }

    @Override
    @Transactional
    public void deleteOptTeamRoleById(String roleId) {
        optTeamRoleDao.deleteObjectById(roleId);
    }

    @Override
    public List<OptVariableDefine> listOptVariableDefinesByFilter(Map<String, Object> filter, PageDesc pageDesc) {
        return optVariableDefineDao.listObjects(filter, pageDesc);
    }

    @Override
    public OptVariableDefine getOptVariableDefineById(String variableId) {
        return optVariableDefineDao.getObjectById(variableId);
    }

    @Override
    @Transactional
    public void saveOptVariableDefine(OptVariableDefine optVariableDefine) {
        optVariableDefineDao.saveNewObject(optVariableDefine);
    }

    @Override
    @Transactional
    public void updateOptVariableDefine(OptVariableDefine optVariableDefine) {
        optVariableDefineDao.updateObject(optVariableDefine);
    }

    @Override
    @Transactional
    public void deleteOptVariableDefineById(String variableId) {
        optVariableDefineDao.deleteObjectById(variableId);
    }

}
