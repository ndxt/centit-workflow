package com.centit.workflow.dao;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.ApprRoleDefine;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ApprRoleDefineDao extends BaseDaoImpl<ApprRoleDefine,String> {

    //    @Transactional
//    public long getNextId() {
//        return DatabaseOptUtils.getSequenceNextValue(this, "S_ApprRoleDefine_ID");
//    }
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    public List<ApprRoleDefine> getApprRoleDefineByRoleCode(String roleCode) {

        String sql = "select * from WF_FLOW_ROLE_DEFINE r where r.role_code = '"+roleCode+"'";
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql, null);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(), queryAndNamedParams.getParams());
        if (dataList != null && dataList.size() > 0) {
            return dataList.toJavaList(ApprRoleDefine.class);
        } else {
            return null;
        }
    }
}
