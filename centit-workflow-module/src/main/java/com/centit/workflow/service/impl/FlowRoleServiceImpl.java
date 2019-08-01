package com.centit.workflow.service.impl;


import com.centit.support.algorithm.UuidOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.FlowRoleDao;
import com.centit.workflow.dao.FlowRoleDefineDao;
import com.centit.workflow.po.FlowRole;
import com.centit.workflow.po.FlowRoleDefine;
import com.centit.workflow.service.FlowRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName FlowRoleServiceImpl
 * @Date 2019/7/22 16:28
 * @Version 1.0
 */
@Service
public class FlowRoleServiceImpl implements FlowRoleService {

    @Resource
    private FlowRoleDao flowRoleDao;

    @Resource
    private FlowRoleDefineDao flowRoleDefineDao;

    //审批角色列表
    @Override
    @Transactional
    public List<FlowRole> listFlowRoles(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowRoleDao.listObjectsByProperties(filterMap,pageDesc);
    }

    @Override
    @Transactional
    public FlowRole getFlowRoleByCode(String roleCode) {
        return flowRoleDao.getObjectById(roleCode);
    }

    @Override
    @Transactional
    public void saveFlowRole(FlowRole flowRole) {
        if(flowRole == null){
            return;
        }
        if(flowRole.getRoleCode() == null || flowRole.getRoleCode().isEmpty()){
            flowRole.setRoleCode(UuidOpt.getUuidAsString32());
        }
        flowRoleDao.mergeObject(flowRole);
    }

    @Override
    @Transactional
    public void deleteFlowRoleByCode(String roleCode) {
        flowRoleDao.deleteObjectById(roleCode);
    }

    @Override
    public List<FlowRoleDefine> getFlowRoleDefineListByCode(String roleCode) {
        return flowRoleDefineDao.listObjectsByProperty("roleCode", roleCode);
//        return flowRoleDefineDao.getFlowRoleDefineByRoleCode(roleCode);
    }

    @Override
    @Transactional
    public void deleteFlowRoleDefineById(String id) {
        flowRoleDefineDao.deleteObjectById(id);
    }

    @Override
    @Transactional
    public void saveFlowRoleDefine(FlowRoleDefine flowRoleDefine) {
        if(flowRoleDefine == null){
            return;
        }
        if(flowRoleDefine.getId() == null || flowRoleDefine.getId().isEmpty()){
            flowRoleDefine.setId(UuidOpt.getUuidAsString32());
        }
        flowRoleDefineDao.mergeObject(flowRoleDefine);
    }
}
