package com.centit.workflow.service.impl;

import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.SysUnitFilterEngine;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.components.UserUnitFilterCalcContext;
import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.workflow.external.JdbcUserUnitFilterCalcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Created by codefan on 17-9-11.
 *
 * 有两种情况 一种直接调用 系统的用户和机构引擎
 * 另一种是 通过jdbc调用 外部数据 ， 需要实现 JdbcUserUnitFilterCalcContext
 */
public abstract class UserUnitCalcEngine {
    private static final Logger logger = LoggerFactory.getLogger(SysUserFilterEngine.class);

    public static UserUnitFilterCalcContext createWorkFlowUUFCC(){
        if("system".equalsIgnoreCase(SysParametersUtils.getStringValue("wf.userunit.engine.type"))){
            return new SystemUserUnitFilterCalcContext();
        } else {
            JdbcUserUnitFilterCalcContext uufcc = new JdbcUserUnitFilterCalcContext();
            uufcc.loadExternalSystemData();
            return uufcc;
        }
    }

    public static String calcSingleUnitByExp(String unitExp,
                Map<String, Set<String>> unitParams , UserUnitVariableTranslate varTrans) {
        if (unitExp == null)
            return null;
        UserUnitFilterCalcContext ecc = createWorkFlowUUFCC();
        ecc.setFormula(unitExp);
        ecc.setVarTrans(varTrans);
        ecc.addAllUnitParam(unitParams);
        return SysUnitFilterEngine.calcSingleUnitByExp(ecc);
    }

    public static Set<String> calcUnitsByExp(String unitExp,
                                                   Map<String, Set<String>> unitParams,
                                                   UserUnitVariableTranslate varTrans) {
        if (unitExp == null)
            return null;
        UserUnitFilterCalcContext ecc = createWorkFlowUUFCC();
        ecc.setFormula(unitExp);
        ecc.setVarTrans(varTrans);
        ecc.addAllUnitParam(unitParams);
        Set<String> untis = SysUnitFilterEngine.calcUnitsExp(ecc);

        if (ecc.hasError())
            logger.error(ecc.getLastErrMsg());

        return untis;
    }

    public static Set<String> calcOperators(String roleExp, Map<String, Set<String>> unitParams,
                                                  Map<String, Set<String>> userParams,
                                                  Map<String, Integer> rankParams,
                                                  UserUnitVariableTranslate varTrans) {
        if (roleExp == null)
            return null;
        UserUnitFilterCalcContext ecc = createWorkFlowUUFCC();
        ecc.setFormula(roleExp);
        ecc.setVarTrans(varTrans);
        // if(lastSameNodeUnit!=null)
        ecc.addAllUnitParam(unitParams);
        ecc.addAllUserParam(userParams);
        ecc.addAllRankParam(rankParams);

        Set<String> sUsers = SysUserFilterEngine.calcRolesExp(ecc);
        if (sUsers == null || ecc.hasError())
            logger.error(ecc.getLastErrMsg());
        return sUsers;
    }
}
