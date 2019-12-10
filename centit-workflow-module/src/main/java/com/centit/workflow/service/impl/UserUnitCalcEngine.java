package com.centit.workflow.service.impl;

import com.centit.framework.components.SysUnitFilterEngine;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.support.common.ObjectException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by codefan on 17-9-11.
 *
 * 有两种情况 一种直接调用 系统的用户和机构引擎
 * 另一种是 通过jdbc调用 外部数据 ， 需要实现 JdbcUserUnitFilterCalcContext
 */
public abstract class UserUnitCalcEngine {
    private static final Logger logger = LoggerFactory.getLogger(SysUserFilterEngine.class);
/*
    public static UserUnitFilterCalcContext createWorkFlowUUFCC(){
        if("system".equalsIgnoreCase(SysParametersUtils.getStringValue("wf.userunit.engine.type"))){
            return new SystemUserUnitFilterCalcContext();
        } else {
            JdbcUserUnitFilterCalcContext uufcc = new JdbcUserUnitFilterCalcContext();
            uufcc.loadExternalSystemData();
            return uufcc;
        }
    }*/

    public static String calcSingleUnitByExp(
        UserUnitFilterCalcContext ecc, String unitExp) {
        if (unitExp == null)
            return null;
        //UserUnitFilterCalcContext ecc = createWorkFlowUUFCC();
        ecc.setFormula(unitExp);
        return SysUnitFilterEngine.calcSingleUnitByExp(ecc);
    }

    public static Set<String> calcUnitsByExp(
                    UserUnitFilterCalcContext ecc, String unitExp) {
        if (StringUtils.isBlank(unitExp)) {
            return null;
        }
        //UserUnitFilterCalcContext ecc = createWorkFlowUUFCC();
        ecc.setFormula(unitExp);
        Set<String> units = SysUnitFilterEngine.calcUnitsExp(ecc);
        if (ecc.hasError()) {
            logger.error(unitExp +":"+ ecc.getLastErrMsg());
            throw new ObjectException(ecc, ObjectException.FORMULA_GRAMMAR_ERROE,
                unitExp +":"+ ecc.getLastErrMsg());
        }
        return units;
    }

    public static Set<String> calcOperators(
                    UserUnitFilterCalcContext ecc,
                    String roleExp) {
        if (StringUtils.isBlank(roleExp)) {
            return null;
        }
        //UserUnitFilterCalcContext ecc = createWorkFlowUUFCC();
        ecc.setFormula(roleExp);
        Set<String> sUsers = SysUserFilterEngine.calcRolesExp(ecc);
        if (ecc.hasError()) {
            logger.error(roleExp +":"+ ecc.getLastErrMsg());
            throw new ObjectException(ecc, ObjectException.FORMULA_GRAMMAR_ERROE,
                roleExp +":"+ ecc.getLastErrMsg());
        }
        return sUsers;
    }
}
