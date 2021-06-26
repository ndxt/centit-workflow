package com.centit.workflow.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.RoleFormulaDao;
import com.centit.workflow.po.RoleFormula;
import com.centit.workflow.service.RoleFormulaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName FlowRoleServiceImpl
 * @Date 2019/7/22 16:28
 * @Version 1.0
 */
@Service
public class RoleFormulaServiceImpl implements RoleFormulaService {

    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @Autowired
    private RoleFormulaDao flowRoleDao;

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
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        context.addUserParam("C", userCode);
        context.addUnitParam("C", unitCode);
        context.addUnitParam("N", unitCode);
        Set<String> sUsers = UserUnitCalcEngine.calcOperators(
            context, formula);

        List<IUserInfo> userInfos = new ArrayList<>();
        for (String uc : sUsers) {
            userInfos.add(context.getUserInfoByCode(uc));
        }
        // 根据userOrder增加排序
        Collections.sort(userInfos, new Comparator<IUserInfo>() {
            @Override
            public int compare(IUserInfo o1, IUserInfo o2) {
                Long i = o1.getUserOrder() - o2.getUserOrder();
                return new Long(i).intValue();
            }

        });
        return (JSONArray) JSONArray.toJSON(userInfos);
    }

    @Override
    public JSONArray viewFormulaUnits(String formula, String userCode, String unitCode) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        context.addUserParam("C", userCode);
        context.addUnitParam("C", unitCode);
        context.addUnitParam("N", unitCode);
        Set<String> sUnits = UserUnitCalcEngine.calcUnitsByExp(
            context, formula);

        List<IUnitInfo> userInfos = new ArrayList<>();
        for (String uc : sUnits) {
            userInfos.add(context.getUnitInfoByCode(uc));
        }
        return (JSONArray) JSONArray.toJSON(userInfos);
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
    public List<? extends IUserInfo> listAllUserInfo() {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        return context.listAllUserInfo();
    }

    @Override
    public List<? extends IUserInfo> listUserInfo(String prefix) {
        List<? extends IUserInfo> allUsers = listAllUserInfo();
        List<IUserInfo> selUsers = new ArrayList<>();
        for (IUserInfo user : allUsers) {
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
    public List<? extends IUnitInfo> listAllUnitInfo() {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        List<? extends IUnitInfo> unitInfos = context.listAllUnitInfo();
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
    public List<? extends IUnitInfo> listSubUnit(String unitCode) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        return context.listSubUnit(unitCode);
        /*CollectionsOpt.sortAsTree(unitInfos,
            (p,c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()));*/
    }

}
