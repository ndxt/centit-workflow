package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.workflow.po.FlowTeamRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017-10-9.
 */
@Repository
public class FlowTeamRoleDao extends BaseDaoImpl<FlowTeamRole,Long>{
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }


    @Transactional
    public Map<String,String> getRoleByFlowCode(String flowCode,Long version){
        Map<String,String> roleMap = new LinkedHashMap<>();
        List<FlowTeamRole> flowTeamRoles = this.listObjectsByFilter("where flow_code = ? and version = ? order by TEAM_ROLE_ORDER asc",new Object[]{flowCode,version});
        if(flowTeamRoles == null || flowTeamRoles.size() == 0){
            return roleMap;
        }
        //roleMap=flowTeamRoles.stream().collect(Collectors.toMap(FlowTeamRole::getRoleCode,FlowTeamRole::getRoleName));
        flowTeamRoles.forEach(role -> roleMap.put(role.getRoleCode(),role.getRoleName())
        );
        return roleMap;
    }

    @Transactional
    public FlowTeamRole getItemRole(String flowCode,Long version, String roleCode){
        return this.getObjectByProperties(
            CollectionsOpt.createHashMap("flowCode",flowCode,"version",version,"roleCode",roleCode)
        );
    }
}
