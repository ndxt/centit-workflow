package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.workflow.po.RoleFormula;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FlowRoleDao
 * @Date 2019/7/22 15:54
 * @Version 1.0
 */
@Repository
public class RoleFormulaDao extends BaseDaoImpl<RoleFormula, String> {

    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    /**
     * 读取工作定义的业务操作
     * @return Map String, String
     */
    public Map<String, String> listAllRoleMsg(String topUnit) {
        List<RoleFormula> flowRoles = this.listObjectsByProperties(CollectionsOpt.createHashMap("topUnit", topUnit));
        Map<String, String> roleMap = new HashMap<>();
        for (RoleFormula flowRole : flowRoles) {
            roleMap.put(flowRole.getFormulaCode(),flowRole.getFormulaName());
        }
        return roleMap;
    }

}
