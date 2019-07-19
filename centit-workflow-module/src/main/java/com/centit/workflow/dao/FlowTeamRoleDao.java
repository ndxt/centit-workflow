package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.algorithm.UuidOpt;
import com.centit.workflow.po.FlowTeamRole;
import com.centit.workflow.po.NodeInstance;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by chen_rj on 2017-10-9.
 */
@Repository
public class FlowTeamRoleDao extends BaseDaoImpl<FlowTeamRole,Long>{
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public Long getNextTeamRoleId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_OPTTEAMROLE");
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(FlowTeamRole o) {
        if(o.getFlowTeamRoleId() == null || "".equals(o.getFlowTeamRoleId())){
            o.setFlowTeamRoleId(UuidOpt.getUuidAsString32());
//            o.setFlowTeamRoleId(getNextTeamRoleId());
        }
        super.saveNewObject(o);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public Map<String,String> getRoleByFlowCode(String flowCode,Long version){
        Map<String,String> roleMap = new LinkedHashMap<>();
        List<FlowTeamRole> flowTeamRoles = this.listObjectsByFilter("where flow_code = ? and version = ? order by TEAM_ROLE_ORDER asc",new Object[]{flowCode,version});
        if(flowTeamRoles == null || flowTeamRoles.size() == 0){
            return roleMap;
        }
        //roleMap=flowTeamRoles.stream().collect(Collectors.toMap(FlowTeamRole::getRoleCode,FlowTeamRole::getRoleName));
        flowTeamRoles.forEach(role->{
            roleMap.put(role.getRoleCode(),role.getRoleName());
        });
        return roleMap;
    }
}
