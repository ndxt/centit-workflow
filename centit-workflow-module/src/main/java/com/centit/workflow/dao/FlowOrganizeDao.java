package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowOrganize;
import com.centit.workflow.po.FlowOrganizeId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowOrganizeDao extends BaseDaoImpl<FlowOrganize,FlowOrganizeId> {
    //public static final Logger logger = LoggerFactory.getLogger(WfOrganizeDao.class);

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();

            filterField.put("flowInstId" , "flowInstId=:flowInstId");
            filterField.put("unitCode" , "userCode=:userCode");
            filterField.put("roleCode" , "roleCode=:roleCode");
            filterField.put("authDesc" , CodeBook.LIKE_HQL_ID);
            filterField.put("authTime" , CodeBook.EQUAL_HQL_ID);

            filterField.put(CodeBook.ORDER_BY_HQL_ID , "unitOrder");

        }
        return filterField;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public void deleteFlowOrganize(long flowInstId, String roleCode) {
        this.getJdbcTemplate().update("delete from WF_ORGANIZE where FLOW_INST_ID = ? and ROLE_CODE = ?",
                new Object[]{flowInstId, roleCode});
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public void deleteFlowOrganize(long flowInstId, String roleCode,String authDesc) {
        this.getJdbcTemplate().update("delete from WF_ORGANIZE where FLOW_INST_ID = ? and ROLE_CODE = ? " +
                "and AUTH_DESC = ?",new Object[]{flowInstId, roleCode,authDesc});
    }
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public List<FlowOrganize> listFlowOrganize(long flowInstId)
    {
        return this.listObjectsByFilter("where FLOW_INST_ID = ? order by unit_Order",
                new Object[]{flowInstId});
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowOrganize> listFlowOrganizeByRole(long flowInstId, String roleCode)
    {
        return this.listObjectsByFilter("where FLOW_INST_ID = ? and ROLE_CODE = ? order by unit_Order",
                new Object[]{flowInstId, roleCode});
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowOrganize> listFlowOrganize(long flowInstId, String roleCode, String authDesc)
    {
        return this.listObjectsByFilter("where FLOW_INST_ID = ? and ROLE_CODE = ? and auth_Desc = ? " +
                        "order by unit_Order",
                new Object[]{flowInstId, roleCode, authDesc});
    }
}
