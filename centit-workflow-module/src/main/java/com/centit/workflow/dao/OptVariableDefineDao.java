package com.centit.workflow.dao;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.OptVariableDefine;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Repository
public class OptVariableDefineDao extends BaseDaoImpl<OptVariableDefine, String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    @Deprecated
    @Transactional
    public Long getNextFlowVariableId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_OPTVARIABLE");
    }

    @Transactional
    public void saveNewObject(OptVariableDefine o) {
        if(o.getOptVariableId() == null || "".equals(o.getOptVariableId())){
            o.setOptVariableId(UuidOpt.getUuidAsString32());
        }
        super.saveNewObject(o);
    }

    @Transactional
    public List<OptVariableDefine> getOptVariableByFlowCode(String flowCode, long version) {
        String sql = "select var.* from WF_OPT_VARIABLE_DEFINE var " +
            "left join WF_FLOW_DEFINE define " +
            "on var.OPT_ID=define.OPT_ID " +
            "where 1=1 " +
            "[:flowCode | and define.FLOW_CODE=:flowCode] " +
            "[:version | and define.VERSION=:version] ";
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql, CollectionsOpt.createHashMap("flowCode", flowCode, "version", version));
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(),queryAndNamedParams.getParams());

        return null == dataList ? Collections.emptyList() : dataList.toJavaList(OptVariableDefine.class);
    }

    @Transactional
    public List<OptVariableDefine> getOptVariableByOptId(String optId){
        return this.listObjectsByFilter("where OPT_ID = ?",new Object[]{optId});
    }
}
