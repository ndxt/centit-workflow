package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.database.orm.OrmDaoUtils;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.NodeInstance;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public class NodeInstanceDao extends BaseDaoImpl<NodeInstance,Long> {

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put("nodeInstId", CodeBook.EQUAL_HQL_ID);
            filterField.put("flowInstId", CodeBook.EQUAL_HQL_ID);
            filterField.put("nodeId", CodeBook.EQUAL_HQL_ID);
            filterField.put("(date)createTime" , "createTime like :createTime");
            filterField.put("(date)lastUpdateTime" , "lastUpdateTime like :createTime");
            filterField.put("lastUpdateUser", CodeBook.EQUAL_HQL_ID);
            filterField.put("startTime", CodeBook.EQUAL_HQL_ID);
            filterField.put("nodeState", CodeBook.EQUAL_HQL_ID);
            filterField.put("subFlowInstId", CodeBook.EQUAL_HQL_ID);
            filterField.put("unitCode", CodeBook.EQUAL_HQL_ID);
            filterField.put("transId", CodeBook.EQUAL_HQL_ID);
            filterField.put("runToken", CodeBook.EQUAL_HQL_ID);
            filterField.put("taskAssigned", CodeBook.EQUAL_HQL_ID);
            filterField.put("userCode", CodeBook.EQUAL_HQL_ID);

            filterField.put(CodeBook.ORDER_BY_HQL_ID, "nodeInstId desc");

        }
        return filterField;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public long getNextNodeInstId() {
        return DatabaseOptUtils.getSequenceNextValue(this,"S_NODEINSTNO");
    }

    /**
     * 根据节点实例编号，更新当前节点运行状态，
     * @param nodeinstid 节点实例编号
     * @param state 状态代码
    */
    @Transactional(propagation= Propagation.MANDATORY)
    public void updtNodeInstState(long nodeinstid, String state) {
        NodeInstance nodeInst = this.getObjectById(nodeinstid);
        nodeInst.setNodeState(state);
        this.updateObject(nodeInst);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listNodeInstByState(long flowInstId, String nodeState) {
        return this.listObjectsByFilter("where node_State= ? and flow_Inst_Id= ?",
                new Object[]{nodeState,flowInstId,});
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public NodeInstance getObjectCascadeById(long nodeInstid){
        return jdbcTemplate.execute(
            (ConnectionCallback<NodeInstance>) conn ->
                OrmDaoUtils.getObjectCascadeById(conn, nodeInstid, NodeInstance.class));
        /*NodeInstance flowInstance = super.getObjectById(nodeInstid);
        return super.fetchObjectReferences(flowInstance);*/
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listNodesWithoutOpt() {
        return this.listObjectsByFilter("where (node_State='N' or node_State='R') and  " +
                        "node_Inst_Id not in (select node_Inst_Id from v_user_task_list) and " +
                        "flow_Inst_Id in (select flow_Inst_Id from WF_FLOW_INSTANCE where inst_State='N' )order by node_Inst_Id desc",
                new Object[]{});
    }
    //expireOptSign == 0未处理  1 已通知  ,2..6 已通知2..5次（暂时不启动重复通知） 6:不处理    7：已挂起  8 已终止 9 已完成
    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listNearExpireNodeInstance(long leaveTime) {
        return this.listObjectsByFilter(" where time_Limit <= ? and node_State='N' and " +
                "(is_Timer='T' or is_Timer='R')",new Object[]{leaveTime});
    }


    /**
     * 查询最后更改的节点
     * @param userCode
     * @param state
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listLastUpdateNodeInst(String userCode, String state){
        return this.listObjectsByFilter(" where last_Update_User = ? and node_State = ? " +
                        "order by last_Update_Time ",
                new Object[]{userCode,state});
    }

    /**
     * 查询某人操作计时的节点
     * @param userCode
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listNodeInstByTimer(String userCode, String isTimer, PageDesc pageDesc){
        return this.listObjectsByFilter(" where last_Update_User = ? and is_Timer = ? " +
                        "order by last_Update_Time ",
                new Object[]{userCode,isTimer},pageDesc);
    }

    /**
     * 更新节点实例时钟状态
     * @param instid 实例编号
     * @param isTimer 不计时N、计时T(有期限)、暂停P  忽略(无期限) F
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public void updateNodeTimerState(long instid,String isTimer,String mangerUserCode){
        NodeInstance nodeInst = this.getObjectById(instid);
        nodeInst.setIsTimer(isTimer);
        nodeInst.setLastUpdateUser(mangerUserCode);
        nodeInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        this.updateObject(nodeInst);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listActiveTimerNodeByFlow(long flowInstId){
        return this.listObjectsByFilter(" where flow_Inst_Id = ? and is_Timer = 'T' ",
                new Object[]{flowInstId});
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listActiveTimerNodeByFlowStage(long flowInstId, String flowStage){
        return this.listObjectsByFilter(" where flow_Inst_Id = ? and flow_Stage = ? and is_Timer = 'T'",
                new Object[]{flowInstId,flowStage});
    }

    public void saveOptIdeaForAutoSubmit(Map<String,Object> paraMap){
        String sql="insert into opt_idea_info (PROC_ID, NODE_INST_ID, FLOW_INST_ID, UNIT_CODE, UNIT_NAME, USER_CODE,USER_NAME,TRANS_DATE,IDEA_CODE,TRANS_IDEA,FLOW_PHASE,NODE_CODE) values (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";
        DatabaseOptUtils.doExecuteSql(this, sql,new Object[]{paraMap.get("procId"),paraMap.get("nodeInstId"),paraMap.get("flowInstId"),paraMap.get("unitCode"),paraMap.get("unitName"),paraMap.get("userCode"),paraMap.get("userName"),
            paraMap.get("transDate"),paraMap.get("ideaCode"),paraMap.get("transIdea"),paraMap.get("flowPhase"),paraMap.get("nodeCode"),});
    }
}
