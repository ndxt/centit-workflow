package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowOptDef;
import com.centit.workflow.po.FlowRole;
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
public class FlowRoleDao extends BaseDaoImpl<FlowRole,String> {

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
        List<FlowRole> flowRoles = this.listObjects();
        Map<String, String> roleMap = new HashMap<>();
        for (FlowRole flowRole : flowRoles) {
            roleMap.put(flowRole.getRoleCode(),flowRole.getRoleName());
        }

        return roleMap;
    }
}
