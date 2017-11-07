package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowWorkTeam;
import com.centit.workflow.po.FlowWorkTeamId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowWorkTeamDao extends BaseDaoImpl<FlowWorkTeam,FlowWorkTeamId>
{
    public Map<String, String> getFilterField() {
        if( filterField == null){
            filterField = new HashMap<String, String>();
            filterField.put("flowInstid" , "flowInstId=:flowInstId");
            filterField.put("userCode" , "userCode=:userCode");
            filterField.put("roleCode" , "roleCode=:roleCode");
            filterField.put("authDesc" , CodeBook.LIKE_HQL_ID);
            filterField.put("authTime" , CodeBook.EQUAL_HQL_ID);
            filterField.put(CodeBook.ORDER_BY_HQL_ID , "userOrder");

        }
        return filterField;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public void deleteFlowWorkTeam(long flowInstId, String roleCode) {
        List<FlowWorkTeam> team = listFlowWorkTeamByRole(flowInstId,  roleCode);
        if(team==null || team.size()==0)
            return ;
        for(FlowWorkTeam t:team)
            this.deleteObject(t);
        //DatabaseOptUtils.doExecuteHql(this,"delete WfTeam where id.flowInstId=? and id.roleCode=?",new Object[]{flowInstId, roleCode});
    }
    
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public List<FlowWorkTeam> listFlowWorkTeam(long flowInstId)
    {
        return this.listObjectsByFilter("where flow_Inst_Id = ? order by role_Code, user_Order",new Object[]{flowInstId});
    }
    
    /**
     * 链接 F_Userinfo 是为了排序
     * @param flowInstId
     * @param roleCode
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public List<FlowWorkTeam> listFlowWorkTeamByRole(long flowInstId, String roleCode)
    {
        return this.listObjectsByFilter("where flow_Inst_Id = ? and role_Code = ? order by user_Order",new Object[]{flowInstId,roleCode});
    }
    
    /**
     * 链接 F_Userinfo 是为了排序
     * @param flowInstId
     * @param roleCode
     * @param authdesc
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowWorkTeam> listFlowWorkTeam(long flowInstId, String roleCode, String authdesc)
    {
        return this.listObjectsByFilter("where flow_Inst_Id = ? and role_Code = ? and auth_Desc = ? " +
                "order by user_Order",new Object[]{flowInstId,roleCode,authdesc});
    }
}
