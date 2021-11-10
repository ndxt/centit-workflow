package com.centit.workflow.service.impl;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.FlowOptPageDao;
import com.centit.workflow.dao.OptTeamRoleDao;
import com.centit.workflow.dao.OptVariableDefineDao;
import com.centit.workflow.po.FlowOptPage;
import com.centit.workflow.po.OptTeamRole;
import com.centit.workflow.po.OptVariableDefine;
import com.centit.workflow.service.FlowOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:47:39
 */
@Service
public class FlowOptServiceImpl implements FlowOptService {

    @Autowired
    private FlowOptPageDao flowOptPageDao;

    @Autowired
    private OptTeamRoleDao optTeamRoleDao;

    @Autowired
    private OptVariableDefineDao optVariableDefineDao;


    @Override
    @Transactional
    public void saveOptPage(FlowOptPage flowOptDef) {
        flowOptDef.setUpdateDate(new Date());
        flowOptPageDao.mergeObject(flowOptDef);
    }

    @Override
    @Transactional
    public List<FlowOptPage> listAllOptPageById(String optId) {
        return flowOptPageDao.listObjectsByProperty("optId", optId);
    }


    @Override
    @Transactional
    public List<FlowOptPage> listOptPageById(String optId) {
        List<FlowOptPage> wfOptDefs = flowOptPageDao.listObjectsByFilter(
            "where OPT_ID = ? and PAGE_TYPE != 'A'",
            new Object[] {optId});
        return wfOptDefs;
    }

    @Override
    @Transactional
    public List<FlowOptPage> listOptAutoRunById(String optId) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("optId",optId);
        properties.put("pageType","A");
        List<FlowOptPage> wfOptDefs = flowOptPageDao.listObjectsByProperties(properties);
        return wfOptDefs;
    }

    @Override
    @Transactional
    public FlowOptPage getOptPageByCode(String optCode) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("optCode",optCode);
        FlowOptPage flowOptDef = flowOptPageDao.getObjectByProperties(properties);
        return flowOptDef;
    }

    @Override
    @Transactional
    public void deleteOptPageByCode(String optCode) {
        flowOptPageDao.deleteObjectById(optCode);
    }

    @Override
    @Transactional
    public List<FlowOptPage> listOptPage(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowOptPageDao.listObjectsByProperties(filterMap,pageDesc);
    }


    @Override
    public String getOptDefSequenceId() {
        return flowOptPageDao.getOptDefSequenceId();
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
