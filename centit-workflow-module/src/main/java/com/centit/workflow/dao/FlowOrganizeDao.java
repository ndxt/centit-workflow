package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
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

            filterField.put("flowInstId" , "cid.flowInstId=:flowInstId");
            filterField.put("unitCode" , "cid.userCode=:userCode");
            filterField.put("roleCode" , "cid.roleCode=:roleCode");
            filterField.put("authDesc" , CodeBook.LIKE_HQL_ID);
            filterField.put("authTime" , CodeBook.EQUAL_HQL_ID);

            filterField.put(CodeBook.ORDER_BY_HQL_ID , "unitOrder");

        }
        return filterField;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public void deleteFlowOrganize(long flowInstId, String roleCode) {
        DatabaseOptUtils.doExecuteHql(this,"delete FlowOrganize where cid.flowInstId=? and cid.roleCode=?",new Object[]{flowInstId, roleCode});
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public void deleteFlowOrganize(long flowInstId, String roleCode,String authDesc) {
        DatabaseOptUtils.doExecuteHql(this,"delete FlowOrganize where cid.flowInstId=? and cid.roleCode=? and authDesc = ?",new Object[]{flowInstId, roleCode,authDesc});
    }
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public List<FlowOrganize> listFlowOrganize(long flowInstId)
    {
        //Map<String,String> filterDesc = new HashMap<String,String>();
        //filterDesc.put("flowinstid",new Long(flowInstId).toString());
        return this.listObjects("From FlowOrganize where cid.flowInstId=? order by unitOrder",flowInstId);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowOrganize> listFlowOrganizeByRole(long flowInstId, String roleCode)
    {
        return this.listObjects("From FlowOrganize where cid.flowInstId = ? and cid.roleCode = ? order by unitOrder",
                    new Object[]{flowInstId, roleCode});
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowOrganize> listFlowOrganize(long flowInstId, String roleCode, String authDesc)
    {
        return this.listObjects("From FlowOrganize " +
                        "where cid.flowInstId = ? and cid.roleCode = ? and authDesc = ? order by unitOrder",
                new Object[]{flowInstId, roleCode, authDesc});
    }
}
