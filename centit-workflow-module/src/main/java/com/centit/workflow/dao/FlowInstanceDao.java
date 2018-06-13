package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.database.orm.OrmDaoUtils;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.FlowInstance;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowInstanceDao extends BaseDaoImpl<FlowInstance,Long> {
    public Map<String, String> getFilterField() {
        if( filterField == null){
            filterField = new HashMap<String, String>();

            filterField.put("flowInstId" , CodeBook.EQUAL_HQL_ID);
            filterField.put("flowId" , CodeBook.LIKE_HQL_ID);
            filterField.put("version" , CodeBook.LIKE_HQL_ID);

            filterField.put("(date)createTimeBeg",
                    " createTime>= :createTimeBeg");
            filterField.put("(date)createTimeEnd",
                    " createTime<= createTimeEnd  ");
            filterField.put("(date)lastUpdateTime" , "lastUpdateTime = :lastUpdateTime");

            filterField.put("instState" , CodeBook.EQUAL_HQL_ID);
            filterField.put("isSubInst" , CodeBook.LIKE_HQL_ID);
            filterField.put("flowOptName" , CodeBook.LIKE_HQL_ID);
            filterField.put("flowOptTag" , CodeBook.LIKE_HQL_ID);
            filterField.put("preInstId" , CodeBook.LIKE_HQL_ID);
            filterField.put("preNodeInstId" , CodeBook.LIKE_HQL_ID);
            filterField.put("unitCode" , CodeBook.LIKE_HQL_ID);
            filterField.put("userCode" , CodeBook.LIKE_HQL_ID);
            filterField.put("(long)nodeId" , "flowInstId in (select flowInstId from NodeInstance where nodeState='N' and nodeId=:nodeId)" );
            filterField.put("optCode", "flowInstId in "+
               "(select a.flowInstId from NodeInstance a,NodeInfo b where a.nodeId=b.nodeId and a.nodeState='N' and b.optCode=:optCode)" );

            filterField.put("nocom" , "instState <> :nocom");
            filterField.put("NP_warning" , "flowInstId in (select flowInstId from FlowWarning ) ");

            filterField.put(CodeBook.ORDER_BY_HQL_ID , "createTime desc,flowInstId desc");

        }
        return filterField;
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public long getNextFlowInstId(){
        return  DatabaseOptUtils.getSequenceNextValue(this,"S_FLOWINSTNO");
    }

    /**
     * 更新流程实例状态
     * @param instid 实例编号
     * @param state
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public void updtFlowInstState(long instid,String state){
        FlowInstance flowInst = this.getObjectById(instid);
        flowInst.setInstState(state);
        this.updateObject(flowInst);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public FlowInstance getObjectCascadeById(long instid){
      return jdbcTemplate.execute(
        (ConnectionCallback<FlowInstance>) conn ->
          OrmDaoUtils.getObjectCascadeById(conn, instid, FlowInstance.class));

      /*FlowInstance flowInstance = super.getObjectById(instid);
        return super.fetchObjectReferences(flowInstance);*/
    }
    /**
     *  获取用户参与 流程实例 按照时间倒序排列
     * @param userCode 用户代码
     * @param pageDesc 分页描述
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowInstance> listUserAttachFlowInstance(String userCode, String flowPhase, Map<String, Object> filterMap, PageDesc pageDesc) {
        //TODO 这个方法有严重问题，直接删除 或者重新设计  VUserAttachFlow 视图不存在
        String whereSql = " where flow_Inst_Id in (select distinct u.flow_Inst_Id from VUserAttachFlow u ";
        if (filterMap.get("oper") != null && filterMap.get("oper").equals("all")) {
            whereSql += " )";
        } else {
            whereSql += " where u.user_Code="
                    + QueryUtils.buildStringForQuery(userCode)
                    + " and u.flow_Phase in (";

            if (flowPhase != null && flowPhase.equals("fw")) {
                whereSql += "'fw','yh','qf','zwh','pb','ysp','gz'";
            } else {
                whereSql += "'sw','pf','ys' ";
            }
            whereSql += " ) )";
        }
        return this.listObjectsByFilter(whereSql,filterMap,pageDesc);
    }

    // 不计时N、计时T(有期限)、暂停P  忽略(无期限) F
    // expireOptSign == 0未处理  1 已通知  ,2..6 已通知2..5次（暂时不启动重复通知）6:不处理    7：已挂起  8 已终止 9 已完成
    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowInstance> listNearExpireFlowInstance(long leaveLimit) {
        String conditionSql = "where FLOW_INST_ID = ? " +
                " and inst_State='N' and is_Timer='T'";
        //and expireOptSign<6 暂时没有这个字段
        return  this.listObjectsByFilter(conditionSql,new Object[]{leaveLimit});
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public void updateTimeConsume(long consumeTime)
    {
        String baseSql = "update WF_FLOW_INSTANCE set TIME_LIMIT =TIME_LIMIT- ? " +
                "where inst_State='N' and is_Timer='T' and time_Limit is not null  ";
        this.getJdbcTemplate().update(baseSql,new Object[]{consumeTime});
    }

    /**
     * 查询所有活动的流程
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowInstance> listAllActiveFlowInst(PageDesc pageDesc){
        return this.listObjectsByFilter("where inst_State = 'N'",new Object[]{},pageDesc);
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowInstance> listAllActiveTimerFlowInst(){
        String whereSql = "where inst_State = 'N' and is_Timer='T'";
        return this.listObjectsByFilter(whereSql,(Object[]) null);
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowInstance> listAllActiveTimerFlowInst(PageDesc pageDesc){
        return this.listObjectsByFilter("where inst_State = 'N' and is_Timer='T'",new Object[]{},pageDesc);
    }

    /**
     * 查询某人操作定时任务的流程
     * @param userCode
     * @param isTimer
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowInstance> listFlowInstByTimer(String userCode, String isTimer, PageDesc pageDesc){
        return super.listObjectsByFilter(
                " where last_Update_User = ? and is_Timer =? order by last_Update_Time ",
                new Object[]{userCode,isTimer},pageDesc);
    }

    /**
     * 更新流程实例时钟状态
     * @param instid 实例编号
     * @param isTimer 不计时N、计时T(有期限)、暂停P  忽略(无期限) F
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public void updateFlowTimerState(long instid,String isTimer,String mangerUserCode){
        FlowInstance flowInst = this.getObjectById(instid);
        flowInst.setIsTimer(isTimer);
        flowInst.setLastUpdateUser(mangerUserCode);
        flowInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        this.updateObject(flowInst);
    }

    /**
     * 检查节点是否有其他没有提交的子流程
     * @param nodeInstId
     * @param curSubFlowId
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public long calcOtherSubflowSum(Long nodeInstId,Long curSubFlowId){
        String baseSql = "select count(1) as otherFlows from WF_FLOW_INSTANCE  "
                + "where INST_STATE='N' and IS_SUB_INST='Y' "
                + " and PRE_NODE_INST_ID=? and FLOW_INST_ID <> ?";
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
}
