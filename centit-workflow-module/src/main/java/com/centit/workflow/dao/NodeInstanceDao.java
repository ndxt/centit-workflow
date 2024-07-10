package com.centit.workflow.dao;

import com.alibaba.fastjson2.JSONArray;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class NodeInstanceDao extends BaseDaoImpl<NodeInstance, String> {

    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("nodeInstId", CodeBook.EQUAL_HQL_ID);
        filterField.put("flowInstId", CodeBook.EQUAL_HQL_ID);
        filterField.put("nodeId", CodeBook.EQUAL_HQL_ID);
        filterField.put("(date)createTime", "createTime >= :createTime");
        filterField.put("(date)lastUpdateTime", "lastUpdateTime >= :createTime");
        filterField.put("lastUpdateUser", CodeBook.EQUAL_HQL_ID);
        filterField.put("startTime", CodeBook.EQUAL_HQL_ID);
        filterField.put("nodeState", CodeBook.EQUAL_HQL_ID);
        filterField.put("roleCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("subFlowInstId", CodeBook.EQUAL_HQL_ID);
        filterField.put("unitCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("transId", CodeBook.EQUAL_HQL_ID);
        filterField.put("runToken", CodeBook.EQUAL_HQL_ID);
        filterField.put("taskAssigned", CodeBook.EQUAL_HQL_ID);
        filterField.put("userCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("osId", "flow_inst_id in (select a.flow_inst_id from wf_flow_instance a " +
            " where a.OS_ID  = :osId)" );
        filterField.put("topUnit", "flow_inst_id in (select a.flow_inst_id from wf_flow_instance a " +
            " where a.top_unit  = :topUnit)" );

        filterField.put(CodeBook.SELF_ORDER_BY, "nodeInstId desc");
        return filterField;
    }

    @Transactional
    public void updtNodeTimerStatus(String nodeInstId, String state) {
        String sql = "update WF_NODE_INSTANCE set TIMER_STATUS = ? where node_Inst_Id = ?";
        DatabaseOptUtils.doExecuteSql(this, sql, new Object[]{ state, nodeInstId});
    }

    @Transactional
    public void updtNodeInstParam(String nodeInstId, String nodeParam) {
        String sql = "update WF_NODE_INSTANCE set NODE_PARAM = ? where node_Inst_Id = ?";
        DatabaseOptUtils.doExecuteSql(this, sql, new Object[]{ nodeParam, nodeInstId});
    }

    /**
     * 根据节点实例编号，更新当前节点运行状态，
     *
     * @param nodeInstId 节点实例编号
     * @param state      状态代码
     */
    @Transactional
    public void updtNodeInstState(String nodeInstId, String state) {
        String sql = "update WF_NODE_INSTANCE set node_State = ? where node_Inst_Id = ?";
        DatabaseOptUtils.doExecuteSql(this, sql, new Object[]{ state, nodeInstId});
    }

    @Transactional
    public List<NodeInstance> listNodeInstByState(String flowInstId, String nodeState) {
        return this.listObjectsByFilter("where node_State= ? and flow_Inst_Id= ?",
            new Object[]{nodeState, flowInstId});
    }

    @Transactional
    public List<NodeInstance> listNodesWithoutOpt() {
        return this.listObjectsByFilter("where (node_State='N' or node_State='R') and  " +
                "node_Inst_Id not in (select node_Inst_Id from v_user_task_list) and " +
                "flow_Inst_Id in (select flow_Inst_Id from WF_FLOW_INSTANCE where inst_State='N' )order by node_Inst_Id desc",
            new Object[]{});
    }

    //expireOptSign == 0未处理  1 已通知  ,2..6 已通知2..5次（暂时不启动重复通知） 6:不处理    7：已挂起  8 已终止 9 已完成
    @Transactional
    public List<NodeInstance> listExpireNodeInstance() {
        return this.listObjectsByFilter(" join WF_FLOW_INSTANCE b on (a.FLOW_INST_ID=b.FLOW_INST_ID)" +
                " where b.inst_State='N' and a.deadline_time < ? and a.node_State='N'" +
                " and (a.TIMER_STATUS='T' or a.TIMER_STATUS='W') ",
            new Object[]{DatetimeOpt.currentUtilDate()}, "a");
    }

    @Transactional
    public List<NodeInstance> listNeedSubmitSyncNodeInstance() {
        return this.listObjectsByFilter(" join WF_FLOW_INSTANCE b on (a.FLOW_INST_ID=b.FLOW_INST_ID)" +
                " where b.inst_State='N' and a.deadline_time < ? and a.node_State='T' and a.TIMER_STATUS='S'",
            new Object[]{DatetimeOpt.currentUtilDate()}, "a");
    }

    @Transactional
    public List<NodeInstance> listWarningNodeInstance() {
        return this.listObjectsByFilter(" join WF_FLOW_INSTANCE b on (a.FLOW_INST_ID=b.FLOW_INST_ID)" +
                " where b.inst_State='N' and a.warning_time < ? and a.node_State='N' and a.TIMER_STATUS='T'",
            new Object[]{DatetimeOpt.currentUtilDate()}, "a");
    }

    /**
     * 查询最后更改的节点
     *
     * @param userCode
     * @param state
     * @return
     */
    @Transactional
    public List<NodeInstance> listLastUpdateNodeInst(String userCode, String state) {
        return this.listObjectsByFilter(" where last_Update_User = ? and node_State = ? " +
                "order by last_Update_Time ",
            new Object[]{userCode, state});
    }

    /**
     * 查询某人操作计时的节点
     *
     * @param userCode
     * @return
     */
    @Transactional
    public List<NodeInstance> listNodeInstByTimerStatus(String userCode, String timerStatus, PageDesc pageDesc) {
        return this.listObjectsByFilterAsJson(" where last_Update_User = ? and TIMER_STATUS = ? " +
                "order by last_Update_Time ",
            new Object[]{userCode, timerStatus}, pageDesc).toJavaList(NodeInstance.class);
    }


    @Transactional
    public List<NodeInstance> listActiveTimerNodeByFlow(String flowInstId) {
        return this.listObjectsByFilter(" where node_state in ('N','W','S') and flow_Inst_Id = ? " +
                "and TIMER_STATUS in ('T','W') ",
            new Object[]{flowInstId});
    }

    @Transactional
    public List<NodeInstance> listActiveTimerNodeByFlowStage(String flowInstId, String flowStageCode) {
        return this.listObjectsByFilter(" where flow_Inst_Id = ? and STAGE_CODE = ? and node_state in ('N','W','S')",
            new Object[]{flowInstId, flowStageCode});
    }

    @Transactional
    public int countActiveTimerNodeByFlowStage(String flowInstId, String flowStageCode) {
        Object obj = DatabaseOptUtils.getScalarObjectQuery(this,
            "select count(*) as activeNodes from WF_NODE_INSTANCE where flow_Inst_Id = ? and STAGE_CODE = ? and node_state in ('N','W','S')",
            new Object[]{flowInstId, flowStageCode});
        return NumberBaseOpt.castObjectToInteger(obj,0);
    }

    /**
     * 获取流程实例的节点信息（流程中所有的业务节点 和 节点实例）
     * @return
     */
    @Transactional
    public JSONArray viewFlowNodes(String flowInstId, String  flowCode, long version) {
        String sql = "select n.NODE_ID, n.NODE_CODE, n.NODE_NAME, n.NODE_DESC, " +
            " t.NODE_INST_ID, t.NODE_STATE, t.CREATE_TIME, t.LAST_UPDATE_TIME, t.last_update_user " +
            " from wf_node n " +
            " left join (select * from wf_node_instance where FLOW_INST_ID = ? ) t " +
            " on n.NODE_ID = t.NODE_ID  " +
            " where n.NODE_TYPE = 'C' and n.FLOW_CODE = ? and n.VERSION = ?" +
            " order by t.last_update_time is null, t.last_update_time asc ,NODE_STATE desc";

        return DatabaseOptUtils.listObjectsBySqlAsJson(this, sql,
                    new Object[] {flowInstId, flowCode, version});
    }


    @Transactional
    public JSONArray viewFlowNodeState(String flowInstId, String  flowCode, long version, Map<String, Object> searchColumn) {
        String sql = "select n.NODE_ID, n.NODE_CODE, n.NODE_NAME, n.NODE_DESC, " +
            " t.NODE_SUMS, t.NODE_STATE, t.CREATE_TIME, t.LAST_UPDATE_TIME, t.last_update_user " +
            " from wf_node n " +
            " left join (select NODE_ID, count(*) as NODE_SUMS," +
            " min(CREATE_TIME) as CREATE_TIME, max(LAST_UPDATE_TIME) as LAST_UPDATE_TIME, max(NODE_STATE) as NODE_STATE " +
            " from wf_node_instance where FLOW_INST_ID = :flowInstId " +
            " group by NODE_ID) t " +
            " on n.NODE_ID = t.NODE_ID  " +
            " where n.NODE_TYPE = 'C' and n.FLOW_CODE = :flowCode and n.VERSION = :version" +
            "[ :(startWith)nodeCodeStart | and c.NODE_CODE like :nodeCodeStart]" +
            "[ :stageArr | and n.STAGE_CODE in (:stageArr) ]" +
            "[ :optId| and n.OPT_ID = :optId]" +
            "[ :optCode| and n.OPT_CODE = :optCode]" +
            "[ :stageCode| and n.STAGE_CODE = :stageCode]" +
            " order by n.NODE_CODE";
        QueryAndNamedParams qap = QueryUtils.translateQuery(sql, searchColumn);
        qap.getParams().put("flowInstId", flowInstId);
        qap.getParams().put("flowCode", flowCode);
        qap.getParams().put("version", version);

        return DatabaseOptUtils.listObjectsByNamedSqlAsJson(this, qap.getQuery(), qap.getParams());
    }

    @Transactional
    public JSONArray viewFlowNodes(String  flowCode, long version, Map<String, Object> searchColumn) {

        String sql = "select n.NODE_ID, n.NODE_CODE, n.NODE_NAME, n.NODE_DESC "+
            " from wf_node n "+
            " where n.NODE_TYPE = 'C' and n.FLOW_CODE = :flowCode and n.VERSION = :version" +
            "[ :(startWith)nodeCodeStart | and c.NODE_CODE like :nodeCodeStart]" +
            "[ :stageArr | and n.STAGE_CODE in (:stageArr) ]" +
            "[ :optId| and n.OPT_ID = :optId]" +
            "[ :optCode| and n.OPT_CODE = :optCode]" +
            "[ :stageCode| and n.STAGE_CODE = :stageCode]" +
            " order by n.NODE_CODE";
        QueryAndNamedParams qap = QueryUtils.translateQuery(sql, searchColumn);
        qap.getParams().put("flowCode", flowCode);
        qap.getParams().put("version", version);
        return DatabaseOptUtils.listObjectsByNamedSqlAsJson(this, qap.getQuery(), qap.getParams());
    }

    public void updateNodeStateById(FlowInstance wfFlowInst) {
        String sql = "update WF_NODE_INSTANCE set NODE_STATE=?, LAST_UPDATE_TIME=?,LAST_UPDATE_USER=? where NODE_STATE = 'N' and FLOW_INST_ID=?";
        this.getJdbcTemplate().update(sql, new Object[]{wfFlowInst.getInstState(),
            wfFlowInst.getLastUpdateTime(), wfFlowInst.getLastUpdateUser(), wfFlowInst.getFlowInstId()});

    }

   /* public void saveOptIdeaForAutoSubmit(Map<String,Object> paraMap){
        String sql="insert into opt_idea_info (PROC_ID, NODE_INST_ID, FLOW_INST_ID, UNIT_CODE, UNIT_NAME, USER_CODE,USER_NAME,TRANS_DATE,IDEA_CODE,TRANS_IDEA,FLOW_PHASE,NODE_CODE) values (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";
        DatabaseOptUtils.doExecuteSql(this, sql,new Object[]{paraMap.get("procId"),paraMap.get("nodeInstId"),paraMap.get("flowInstId"),paraMap.get("unitCode"),paraMap.get("unitName"),paraMap.get("userCode"),paraMap.get("userName"),
            paraMap.get("transDate"),paraMap.get("ideaCode"),paraMap.get("transIdea"),paraMap.get("flowPhase"),paraMap.get("nodeCode"),});
    }*/

    private final static String queryNodeInstance =
        "select b.FLOW_INST_ID, b.NODE_INST_ID, b.UNIT_CODE, b.USER_CODE, b.ROLE_TYPE," +
        "b.ROLE_CODE, c.NODE_CODE, c.NODE_NAME, c.NODE_TYPE, b.NODE_STATE, " +
        "b.NODE_ID, c.OPT_TYPE as NODE_OPT_TYPE, c.OPT_PARAM, b.CREATE_TIME, b.deadline_time, " +
        "c.OPT_CODE, c.EXPIRE_OPT, b.STAGE_CODE, b.GRANTOR, b.LAST_UPDATE_USER," +
        "b.LAST_UPDATE_TIME, b.TIMER_STATUS, c.OPT_ID, b.PREV_NODE_INST_ID, b.pause_time, " +
        "b.warning_time, c.Time_Limit as promise_Time, b.RUN_TOKEN, b.NODE_PARAM " +
        "from wf_node_instance b join WF_NODE c on (b.NODE_ID = c.NODE_ID) " +
        "where 1=1 [ :flowInstId| and b.FLOW_INST_ID = :flowInstId]"  +
        "[ :nodeState| and b.NODE_STATE = :nodeState]" +
        "[ :stageArr | and c.STAGE_CODE in (:stageArr) ]" +
        "[ :userCode| and b.USER_CODE = :userCode]" +
        "[ :unitCode| and b.UNIT_CODE = :unitCode]" +
        "[ :(DATETIME)beginTime| and b.CREATE_TIME >= :beginTime]" +
        "[ :(DATETIME)endTime| and b.CREATE_TIME <= :endTime]" +
        "[ :optId| and c.OPT_ID = :optId]" +
        "[ :optCode| and c.OPT_CODE = :optCode]" +
        "[ :nodeInstId| and b.NODE_INST_ID = :nodeInstId]" +
        "[ :stageCode| and c.STAGE_CODE = :stageCode]" +
        "[ :nodeName| and c.NODE_NAME = :nodeName]" +
        "[ :nodeNames| and c.NODE_NAME in (:nodeNames)]" +
        "[ :nodeCode| and c.NODE_CODE = :nodeCode]" +
        "[ :(startWith)nodeCodeStart | and c.NODE_CODE like :nodeCodeStart]" +
        "[ :nodeCodes| and c.NODE_CODE in (:nodeCodes)]" +
        "[ :notNodeCode| and c.NODE_CODE <> :notNodeCode]" +
        "[ :notNodeCodes| and c.NODE_CODE not in (:notNodeCodes)]";

    @Transactional
    public JSONArray listNodeInstances(Map<String, Object> searchColumn, PageDesc pageDesc) {
        return DatabaseOptUtils.listObjectsByParamsDriverSqlAsJson(this, queryNodeInstance,
            searchColumn, pageDesc);
    }

}
