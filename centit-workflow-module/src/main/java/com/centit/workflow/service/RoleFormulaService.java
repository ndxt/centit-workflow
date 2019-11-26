package com.centit.workflow.service;


import com.alibaba.fastjson.JSONArray;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleFormula;

import java.util.List;
import java.util.Map;

public interface RoleFormulaService {

    List<RoleFormula> listRoleFormulas(Map<String, Object> filterMap, PageDesc pageDesc);

    RoleFormula getRoleFormulaByCode(String formulaCode);

    void saveRoleFormula(RoleFormula flowRole);

    void deleteRoleFormulaByCode(String formulaCode);

    JSONArray viewFormulaUsers(String formula, String userCode, String unitCode);

    JSONArray viewRoleFormulaUsers(String formulaCode, String userCode, String unitCode);

    List<? extends IUserInfo> listAllUserInfo();

    List<? extends IUserInfo> listUserInfo(String prefix);

    List<? extends IUnitInfo> listAllUnitInfo();

    List<? extends IUnitInfo> listSubUnit(String unitCode);
}
