package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowWorkTeam;
import com.centit.workflow.po.FlowWorkTeamId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowWorkTeamDao extends BaseDaoImpl<FlowWorkTeam, FlowWorkTeamId>
{
    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<String, String>();
        filterField.put("flowInstid" , "flowInstId=:flowInstId");
        filterField.put("userCode" , "userCode=:userCode");
        filterField.put("roleCode" , "roleCode=:roleCode");
        filterField.put("authDesc" , CodeBook.LIKE_HQL_ID);
        filterField.put("authTime" , CodeBook.EQUAL_HQL_ID);
        filterField.put(CodeBook.ORDER_BY_HQL_ID , "userOrder");
        return filterField;
    }

    @Transactional
    public void deleteFlowWorkTeam(String flowInstId, String roleCode) {
        List<FlowWorkTeam> team = listFlowWorkTeamByRole(flowInstId,  roleCode);
        if(team==null || team.size()==0)
            return ;
        for(FlowWorkTeam t:team)
            this.deleteObject(t);
        //DatabaseOptUtils.doExecuteHql(this,"delete WfTeam where id.flowInstId=? and id.roleCode=?",new Object[]{flowInstId, roleCode});
    }

    @Transactional
    public List<FlowWorkTeam> listFlowWorkTeam(String flowInstId)
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
    @Transactional
    public List<FlowWorkTeam> listFlowWorkTeamByRole(String flowInstId, String roleCode)
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
    @Transactional
    public List<FlowWorkTeam> listFlowWorkTeam(String flowInstId, String roleCode, String authdesc)
    {
        return this.listObjectsByFilter("where flow_Inst_Id = ? and role_Code = ? and auth_Desc = ? " +
                "order by user_Order",new Object[]{flowInstId,roleCode,authdesc});
    }
}
