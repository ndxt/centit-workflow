package com.centit.workflow.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.RoleFormulaDao;
import com.centit.workflow.po.RoleFormula;
import com.centit.workflow.service.RoleFormulaService;
import com.centit.workflow.service.UserUnitFilterCalcContextFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @Autowired
    private RoleFormulaDao flowRoleDao;

    //审批角色列表
    @Override
    @Transactional
    public List<RoleFormula> listRoleFormulas(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowRoleDao.listObjectsByProperties(filterMap,pageDesc);
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

    @Override
    public List<? extends IUserInfo> listAllUserInfo() {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();

        return context.listAllUserInfo();
    }

    @Override
    public List<? extends IUnitInfo> listAllUnitInfo() {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        List<? extends IUnitInfo> unitInfos = context.listAllUnitInfo();
        CollectionsOpt.sortAsTree(unitInfos,
            (p,c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()));
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
        List<? extends IUnitInfo> unitInfos = context.listSubUnit(unitCode);
        CollectionsOpt.sortAsTree(unitInfos,
            (p,c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()));
        return unitInfos;
    }

}
