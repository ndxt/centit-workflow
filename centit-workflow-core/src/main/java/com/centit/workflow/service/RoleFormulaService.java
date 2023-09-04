package com.centit.workflow.service;


import com.alibaba.fastjson2.JSONArray;
import com.centit.framework.model.basedata.UnitInfo;
import com.centit.framework.model.basedata.UserInfo;
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

    JSONArray viewFormulaUnits(String formula, String userCode, String unitCode);

    JSONArray viewRoleFormulaUsers(String formulaCode, String userCode, String unitCode);

    List<UserInfo> listAllUserInfo(String topUnit);

    List<UserInfo> listUserInfo(String prefix, String topUnit);

    List<UnitInfo> listAllUnitInfo(String topUnit);

    List<UnitInfo> listSubUnit(String unitCode);
}
