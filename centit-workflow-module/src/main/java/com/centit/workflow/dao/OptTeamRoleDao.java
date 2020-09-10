package com.centit.workflow.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.OptTeamRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Repository
public class OptTeamRoleDao extends BaseDaoImpl<OptTeamRole, String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }


    @Transactional
    public Map<String,String> getRoleByFlowCode(String flowCode, Long version) {
        Map<String,String> roleMap = new LinkedHashMap<>();
        String sql = "select role.* from WF_OPT_TEAM_ROLE role " +
            "left join WF_FLOW_DEFINE define " +
            "on role.OPT_ID=define.OPT_ID " +
            "where 1=1 " +
            "[ :flowCode | and define.FLOW_CODE = :flowCode ] " +
            "[ :version | and define.VERSION = :version ] " +
            "order by TEAM_ROLE_ORDER asc";
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql, CollectionsOpt.createHashMap("flowCode", flowCode, "version", version));
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(),queryAndNamedParams.getParams());

        if (dataList == null) {
            return roleMap;
        }
        List<OptTeamRole> optTeamRoles = dataList.toJavaList(OptTeamRole.class);
        optTeamRoles.forEach(role -> roleMap.put(role.getRoleCode(), role.getRoleName()));
        return roleMap;
    }

    @Transactional
    public OptTeamRole getItemRole(String flowCode, Long version, String roleCode) {
        String sql = "select role.* from WF_OPT_TEAM_ROLE role " +
            "left join WF_FLOW_DEFINE define " +
            "on role.OPT_ID=define.OPT_ID " +
            "where 1=1 " +
            "[ :flowCode | and define.FLOW_CODE = :flowCode] " +
            "[ :version | and define.VERSION = :version] " +
            "[ :roleCode | and role.ROLE_CODE = :roleCode]";
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql, CollectionsOpt.createHashMap("flowCode", flowCode, "version", version, "roleCode", roleCode));
        JSONObject data = DatabaseOptUtils.getObjectBySqlAsJson(this,
            queryAndNamedParams.getQuery(),queryAndNamedParams.getParams());
        if (null != data ) {
            return data.toJavaObject(OptTeamRole.class);
        }
        return null;
    }

    @Override
    public int updateObject(OptTeamRole o) {
        return super.updateObjectWithNullField(o);
    }

    @Transactional
    public Map<String,String> getRoleByOptId(String optId) {
        Map<String,String> roleMap = new LinkedHashMap<>();
        List<OptTeamRole> optTeamRoles = this.listObjectsByFilter("where opt_id = ? order by TEAM_ROLE_ORDER asc",new Object[]{optId});
        if(optTeamRoles == null || optTeamRoles.size() == 0){
            return roleMap;
        }
        optTeamRoles.forEach(role -> roleMap.put(role.getRoleCode(), role.getRoleName()));
        return roleMap;
    }

    @Transactional
    public OptTeamRole getItemRole(String optId, String roleCode) {
        return this.getObjectByProperties(
            CollectionsOpt.createHashMap("optId", optId, "roleCode", roleCode)
        );
    }
}
