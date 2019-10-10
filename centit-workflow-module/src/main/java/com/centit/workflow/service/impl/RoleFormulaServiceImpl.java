package com.centit.workflow.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.basedata.IUserRole;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.RoleFormulaDao;
import com.centit.workflow.po.RoleFormula;
import com.centit.workflow.service.RoleFormulaService;
import com.centit.workflow.service.UserUnitFilterCalcContextFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName FlowRoleServiceImpl
 * @Date 2019/7/22 16:28
 * @Version 1.0
 */
@Service
public class RoleFormulaServiceImpl implements RoleFormulaService {

    @Resource
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @Resource
    private RoleFormulaDao flowRoleDao;

    //审批角色列表
    @Override
    @Transactional
    public List<RoleFormula> listRoleFormulas(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowRoleDao.listObjectsByProperties(filterMap,pageDesc);
    }

    @Override
    @Transactional
    public RoleFormula getRoleFormulaByCode(String roleCode) {
        return flowRoleDao.getObjectById(roleCode);
    }

    @Override
    @Transactional
    public void saveRoleFormula(RoleFormula flowRole) {
        flowRoleDao.mergeObject(flowRole);
    }

    @Override
    @Transactional
    public void deleteRoleFormulaByCode(String roleCode) {
        flowRoleDao.deleteObjectById(roleCode);
    }

    @Override
    public JSONArray viewFormulaUsers(String formula) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        Set<String> users = UserUnitCalcEngine.calcOperators(
            context,
            formula, null, null, null,null);
        // 这个又 和系统用户 绑定了，这个是不对的；这部分内容应该需要重构
        return (JSONArray) JSONArray.toJSON(
            CodeRepositoryUtil.getUserInfosByCodes(users));
    }

    @Override
    public JSONArray viewRoleFormulaUsers(String formulaCode) {
        RoleFormula flowRole = flowRoleDao.getObjectById(formulaCode);
        if(flowRole==null){
            return null;
        }
        return viewFormulaUsers(flowRole.getRoleFormula());
    }


}
