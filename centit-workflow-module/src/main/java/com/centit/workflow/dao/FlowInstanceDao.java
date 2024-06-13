package com.centit.workflow.dao;

import com.alibaba.fastjson2.JSONArray;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowInstance;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowInstanceDao extends BaseDaoImpl<FlowInstance, String> {
    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("flowInstId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("flowInstIds" , "flow_Inst_Id in (:flowInstIds)");
        filterField.put("flowCode" , CodeBook.EQUAL_HQL_ID);
        filterField.put("version" , CodeBook.EQUAL_HQL_ID);
        filterField.put("osId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("optId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("(date)createTimeBeg",
            " createTime>= :createTimeBeg");
        filterField.put("(date)createTimeEnd",
            " createTime<= createTimeEnd  ");
        filterField.put("(date)lastUpdateTime" , "lastUpdateTime = :lastUpdateTime");

        filterField.put("instState" , CodeBook.EQUAL_HQL_ID);
        filterField.put("isSubInst" , CodeBook.EQUAL_HQL_ID);
        filterField.put("flowOptName" , CodeBook.LIKE_HQL_ID);
        filterField.put("flowOptTag" , CodeBook.LIKE_HQL_ID);
        filterField.put("preInstId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("preNodeInstId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("unitCode" , CodeBook.EQUAL_HQL_ID);
        filterField.put("userCode" , CodeBook.EQUAL_HQL_ID);
        filterField.put("(long)nodeId" , "flowInstId in (select flowInstId from NodeInstance where nodeState='N' and nodeId=:nodeId)" );
        filterField.put("optCode", "flowInstId in "+
            "(select a.flowInstId from NodeInstance a,NodeInfo b where a.nodeId=b.nodeId and a.nodeState='N' and b.optCode=:optCode)" );

        filterField.put("nocom" , "instState <> :nocom");
        filterField.put("NP_warning" , "flowInstId in (select flowInstId from FlowWarning ) ");

        filterField.put(CodeBook.ORDER_BY_HQL_ID , "createTime desc,flowInstId desc");
        return filterField;
    }

    @Transactional
    public List<FlowInstance> listExpireFlowInstance() {
        return this.listObjectsByFilter(" where deadline_time < ? and inst_State='N'" +
                " and (TIMER_STATUS='T' or TIMER_STATUS='W') ",
            new Object[]{DatetimeOpt.currentUtilDate()});
    }

    @Transactional
    public List<FlowInstance> listWarningFLowInstance() {
        return this.listObjectsByFilter(" where warning_time < ? and inst_State='N' and TIMER_STATUS='T'",
            new Object[]{DatetimeOpt.currentUtilDate()});
    }

    @Transactional
    public void updtFlowTimerStatus(String flowInstId, String state) {
        String sql = "update WF_FLOW_INSTANCE set TIMER_STATUS = ? where FLOW_INST_ID = ?";
        DatabaseOptUtils.doExecuteSql(this, sql, new Object[]{ state, flowInstId});
    }

    /**
     * 查询某人操作定时任务的流程
     * @param userCode
     * @param timerStatus
     * @return
     */
    @Transactional
    public List<FlowInstance> listFlowInstByTimerStatus(String userCode, String timerStatus, PageDesc pageDesc){
        return super.listObjectsByFilterAsJson(
            " where last_Update_User = ? and TIMER_STATUS =? order by last_Update_Time ",
            new Object[]{userCode,timerStatus},pageDesc).toJavaList(FlowInstance.class);
    }

    /**
     * 检查节点是否有其他没有提交的子流程
     * @param nodeInstId 父流程 ID
     * @param curSubFlowId 子流程ID
     * @return 数量
     */
    @Transactional
    public long calcOtherSubflowSum(String nodeInstId,String curSubFlowId){
        String baseSql = "select count(1) as otherFlows from WF_FLOW_INSTANCE  "
            + "where INST_STATE='N' and IS_SUB_INST='Y' "
            + " and PRE_NODE_INST_ID = ? and FLOW_INST_ID <> ?";//大小于
        Object obj = this.getJdbcTemplate().queryForObject(baseSql,new Object[]{nodeInstId,curSubFlowId},Long.class);
        if (obj == null)
            return 0;
        if (obj instanceof Long)
            return ((Long) obj).longValue();
        if (obj instanceof String)
            return Long.valueOf(obj.toString()).longValue();
        if (obj instanceof BigDecimal)
            return ((BigDecimal) obj).longValue();
        return 0;
    }

    @Transactional
    public void updateFlowInstOptInfo(String flowInstId, String flowOptName,String flowOptTag){
        String sql="update WF_FLOW_INSTANCE set FLOW_OPT_NAME=?, FLOW_OPT_TAG=? where FLOW_INST_ID=?";
        this.getJdbcTemplate().update(sql,new Object[]{flowOptName,flowOptTag,flowInstId});
    }

    @Transactional
    public void updateFlowInstOptName(String flowInstId, String flowOptName){
        String sql="update WF_FLOW_INSTANCE set FLOW_OPT_NAME=? where FLOW_INST_ID=?";
        this.getJdbcTemplate().update(sql,new Object[]{flowOptName,flowInstId});
    }

    @Transactional
    public void updateFlowInstOptInfoAndUser(String flowInstId, String flowOptName,String flowOptTag,String userCode,String unitCode){
        String sql="update WF_FLOW_INSTANCE set FLOW_OPT_NAME=?, FLOW_OPT_TAG=?, user_code=?, unit_code=? where FLOW_INST_ID=?";
        this.getJdbcTemplate().update(sql,new Object[]{flowOptName,flowOptTag,userCode,unitCode,flowInstId});
    }

    public List<FlowInstance> listAllFlowInstByOptTag(String flowOptTag) {
        return this.listObjectsByFilter("where FLOW_OPT_TAG=? ",
            new Object[]{flowOptTag});
    }

    public void updtFlowInstInfo(FlowInstance wfFlowInst) {
        String sql="update WF_FLOW_INSTANCE set FLOW_INST_ID=?, INST_STATE=?, " +
            "LAST_UPDATE_TIME=?, LAST_UPDATE_USER=? where FLOW_INST_ID=?";
        this.getJdbcTemplate().update(sql,new Object[]{wfFlowInst.getFlowInstId(),wfFlowInst.getInstState(),
            wfFlowInst.getLastUpdateTime(),wfFlowInst.getLastUpdateUser(),wfFlowInst.getFlowInstId()});
    }

    /**
     * 统计工作流实例个数
     * @param map
     * @return
     */
    public JSONArray countFlowInstances(Map<String,Object> map){
        String sql = "SELECT FLOW_CODE,count(1) FROM wf_flow_instance " +
            "WHERE 1 = 1 [ :flowCode | AND FLOW_CODE in ( :flowCode ) ]  GROUP BY FLOW_CODE";
        return DatabaseOptUtils.listObjectsByParamsDriverSqlAsJson(this, sql, map);
    }
}
