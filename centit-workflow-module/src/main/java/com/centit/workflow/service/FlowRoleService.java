package com.centit.workflow.service;


import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowRole;
import com.centit.workflow.po.FlowRoleDefine;

import java.util.List;
import java.util.Map;

public interface FlowRoleService {

    public List<FlowRole> listFlowRoles(Map<String, Object> filterMap, PageDesc pageDesc);

    public FlowRole getFlowRoleByCode(String roleCode);

    public void saveFlowRole(FlowRole flowRole);

    public void deleteFlowRoleByCode(String roleCode);

    List<FlowRoleDefine> getFlowRoleDefineListByCode(String roleCode);

    void deleteFlowRoleDefineById(String id);

    void saveFlowRoleDefine(FlowRoleDefine flowRoleDefine);
}
