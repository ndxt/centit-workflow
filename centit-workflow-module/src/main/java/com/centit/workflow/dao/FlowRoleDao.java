package com.centit.workflow.dao;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
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

    public List<FlowRole> listUserFlowRoles(Map<String,Object> map){
        String sql="select t.role_code,t.role_name,t.role_level from wf_flow_role t join wf_flow_role_define r " +
            "on r.role_code=t.role_code where 1=1 " +
            "[ :enCode| and (r.related_type='xz' and r.related_code in (:enCode)) ] " +
            "[ :jsRole| or (r.related_type='js' and r.related_code in (:jsRole)) ] " ;
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql,map);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(),queryAndNamedParams.getParams());
        return dataList.toJavaList(FlowRole.class);
    }
}
