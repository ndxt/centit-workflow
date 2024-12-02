package com.centit.workflow.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.centit.framework.common.GlobalConstValue;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.basedata.UnitInfo;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.RoleFormulaDao;
import com.centit.workflow.po.RoleFormula;
import com.centit.workflow.service.RoleFormulaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName FlowRoleServiceImpl
 * @Date 2019/7/22 16:28
 * @Version 1.0
 */
@Service
public class RoleFormulaServiceImpl implements RoleFormulaService {

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @Autowired
    private RoleFormulaDao flowRoleDao;


    private String fetchTopUnit(String unitCode) {
        if(StringUtils.isNotBlank(unitCode)) {
            UnitInfo ui = platformEnvironment.loadUnitInfo(unitCode);
            if (ui != null) {
                return ui.getTopUnit();
            }
        }
        return GlobalConstValue.NO_TENANT_TOP_UNIT;
    }

    //审批角色列表
    @Override
    @Transactional
    public List<RoleFormula> listRoleFormulas(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowRoleDao.listObjectsByProperties(filterMap, pageDesc);
    }

    @Override
    @Transactional
    public RoleFormula getRoleFormulaByCode(String formulaCode) {
        return flowRoleDao.getObjectById(formulaCode);
    }

    @Override
    @Transactional
    public void saveRoleFormula(RoleFormula flowRole) {
        flowRoleDao.mergeObject(flowRole);
    }

    @Override
    @Transactional
    public void deleteRoleFormulaByCode(String formulaCode) {
        flowRoleDao.deleteObjectById(formulaCode);
    }

    @Override
    public JSONArray viewFormulaUsers(String formula, String userCode, String unitCode) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext(fetchTopUnit(unitCode));
        context.addUserParam("C", userCode);
        context.addUnitParam("C", unitCode);
        context.addUnitParam("N", unitCode);
        Set<String> sUsers = UserUnitCalcEngine.calcOperators(
            context, formula);
        if(sUsers == null){
            return null;
        }
        List<UserInfo> userInfos = new ArrayList<>();
        for (String uc : sUsers) {
            UserInfo ui = context.getUserInfoByCode(uc);
            if(ui != null) {
                userInfos.add(ui);
            }
        }
        // 根据userOrder增加排序
        userInfos.sort((o1, o2) -> {
            long i = o1.getUserOrder() - o2.getUserOrder();
            return (int) i;
        });
        return JSONArray.copyOf(userInfos);
    }

    @Override
    public JSONArray viewFormulaUnits(String formula, String userCode, String unitCode) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext(fetchTopUnit(unitCode));
        context.addUserParam("C", userCode);
        context.addUnitParam("C", unitCode);
        context.addUnitParam("N", unitCode);
        Set<String> sUnits = UserUnitCalcEngine.calcUnitsByExp(
            context, formula);

        List<UnitInfo> userInfos = new ArrayList<>();
        for (String uc : sUnits) {
            userInfos.add(context.getUnitInfoByCode(uc));
        }
        return JSONArray.copyOf(userInfos);
    }

    @Override
    public JSONArray viewRoleFormulaUsers(String formulaCode, String userCode, String unitCode) {
        RoleFormula flowRole = flowRoleDao.getObjectById(formulaCode);
        if (flowRole == null) {
            return null;
        }
        return viewFormulaUsers(flowRole.getRoleFormula(), userCode, unitCode);
    }

    @Override
    public List<UserInfo> listAllUserInfo(String topUnit) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext(topUnit);
        return context.listAllUserInfo();
    }

    @Override
    public List<UserInfo> listUserInfo(String prefix, String topUnit) {
        List<UserInfo> allUsers = listAllUserInfo(topUnit);
        List<UserInfo> selUsers = new ArrayList<>();
        for (UserInfo user : allUsers) {
            if (user.getUserName().startsWith(prefix)
                || user.getUserName().endsWith(prefix)
                || user.getLoginName().startsWith(prefix)
                || user.getLoginName().endsWith(prefix)) {
                selUsers.add(user);
            }
        }
        return selUsers;
    }

    @Override
    public List<UnitInfo> listAllUnitInfo(String topUnit) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext(topUnit);
        List<UnitInfo> unitInfos = context.listAllUnitInfo();
        CollectionsOpt.sortAsTree(unitInfos,
            (p, c) -> StringUtils.equals(p.getUnitCode(), c.getParentUnit()));
        return unitInfos;
    }

    /**
     * 获得机构所有的子机构
     *
     * @param unitCode 机构代码
     * @return 子机构集合
     */
    @Override
    public List<UnitInfo> listSubUnit(String unitCode) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext(fetchTopUnit(unitCode));
        return context.listSubUnit(unitCode);
        /*CollectionsOpt.sortAsTree(unitInfos,
            (p,c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()));*/
    }

}
