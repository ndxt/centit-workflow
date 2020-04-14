package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.FlowVariableId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowVariableDao extends BaseDaoImpl<FlowVariable,FlowVariableId>
{
        //public static final Logger logger = LoggerFactory.getLogger(WfFlowVariableDao.class);

    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("flowInstId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("runToken" , CodeBook.EQUAL_HQL_ID);
        filterField.put("varName" , CodeBook.EQUAL_HQL_ID);
        filterField.put("varValue" , CodeBook.LIKE_HQL_ID);
        filterField.put("varType" , CodeBook.LIKE_HQL_ID);
        return filterField;
    }

    @Transactional
    public List<FlowVariable> listFlowVariables(String flowInstId){
        return this.listObjectsByFilter("where FLOW_INST_ID = ? order by run_Token",new Object[]{flowInstId});
    }

    @Transactional
    public List<FlowVariable> viewFlowVariablesByVarname(String flowInstId,
                                                         String varname) {
        return this.listObjectsByFilter("where FLOW_INST_ID = ? and var_Name=? order by run_Token",
                new Object[] { flowInstId, varname });
    }

    @Transactional
    public List<FlowVariable> listFlowDefaultVariables(String flowInstId, String flowCode, long version){
        List<Object[]>  ja = DatabaseOptUtils.listObjectsBySql(this,
            " select VARIABLE_NAME, VARIABLE_TYPE, DEFAULT_VALUE" +
            " from WF_FLOW_VARIABLE_DEFINE" +
            " where FLOW_CODE = ? and VERSION = ?",new Object[]{flowCode, version});
        if(ja == null || ja.size()==0){
           return null;
        }
        List<FlowVariable> variables = new ArrayList<>(ja.size());
        for(Object[] json : ja){
            String svarValue = StringBaseOpt.castObjectToString(json[2]);
            if(StringUtils.isNotBlank(svarValue)) {
                variables.add(new FlowVariable(flowInstId, "T",
                    StringBaseOpt.castObjectToString(json[0]),
                    StringBaseOpt.castObjectToString(json[1]),
                    svarValue));
            }
        }
        return variables;
    }
}
