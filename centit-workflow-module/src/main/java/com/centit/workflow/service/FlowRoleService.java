package com.centit.workflow.service;


import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowRole;
import com.centit.workflow.po.FlowTeamDefine;

import java.util.List;
import java.util.Map;

public interface FlowRoleService {

    List<FlowRole> listFlowRoles(Map<String, Object> filterMap, PageDesc pageDesc);

    FlowRole getFlowRoleByCode(String roleCode);

    void saveFlowRole(FlowRole flowRole);

    void deleteFlowRoleByCode(String roleCode);

    List<FlowTeamDefine> getFlowRoleDefineListByCode(String roleCode);

    void deleteFlowRoleDefineById(String id);

    void saveFlowRoleDefine(FlowTeamDefine flowRoleDefine);
}
