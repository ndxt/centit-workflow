package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.workflow.po.ApprRole;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ApprRoleDao extends BaseDaoImpl<ApprRole,String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    /**
     * 读取工作定义的业务操作
     *
     *
     * @return
     */
    public Map<String, String> listAllRoleMsg() {
//        FlowInfo flowDef = this.flowDefineDao.getFlowDefineByID(flowCode, version);
        //FlowOptInfo flowOptInfo = flowOptInfoDao.getObjectById(flowDef.getOptId());
        List<ApprRole> apprRoles = this.listObjects();
        Map<String, String> roleMap = new HashMap<>();
        for (ApprRole flowRole : apprRoles) {
            roleMap.put(flowRole.getRoleCode(),flowRole.getRoleName());
        }

        return roleMap;
    }
}

