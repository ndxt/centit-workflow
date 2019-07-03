package com.centit.workflow.dao;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.ActionTask;
import com.centit.workflow.po.UserTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 流程任务数据操作类
 *
 * @author ljy
 * @version $Rev$ <br>
 *          $Id$
 */
@Repository
public class ActionTaskDao extends BaseDaoImpl<ActionTask,Long>
{
    private final static String userTaskFinBaseSql = "select FLOW_INST_ID, FLOW_CODE,VERSION,FLOW_OPT_NAME," +
            "FLOW_OPT_TAG,NODE_INST_ID,UNIT_CODE,USER_CODE,ROLE_TYPE," +
            "ROLE_CODE,AUTH_DESC,NODE_CODE,NODE_NAME,NODE_TYPE," +
            "NODE_OPT_TYPE,OPT_PARAM,CREATE_TIME,PROMISE_TIME,TIME_LIMIT," +
            "OPT_CODE,EXPIRE_OPT,STAGE_CODE,GRANTOR,LAST_UPDATE_USER," +
            "LAST_UPDATE_TIME,INST_STATE,OPT_URL " +
            "from V_USER_TASK_LIST_FIN " +
            "where 1=1 [ :flowInstId| and FLOW_INST_ID = :flowInstId] " +
            "[ :userCode| and USER_CODE = :userCode] " +
            "[ :nodeCode| and NODE_CODE = :nodeCode] " +
            "[ :nodeInstId| and NODE_INST_ID = :nodeInstId] " +
            "[ :flowCode| and FLOW_CODE = :flowCode] " +
            "[ :stageCode| and STAGE_CODE = :stageCode] ";

    private final static String userTaskBaseSql = "select FLOW_INST_ID, FLOW_CODE,VERSION,FLOW_OPT_NAME," +
            "FLOW_OPT_TAG,NODE_INST_ID,UNIT_CODE,USER_CODE,ROLE_TYPE," +
            "ROLE_CODE,AUTH_DESC,NODE_CODE,NODE_NAME,NODE_TYPE," +
            "NODE_OPT_TYPE,OPT_PARAM,CREATE_TIME,PROMISE_TIME,TIME_LIMIT," +
            "OPT_CODE,EXPIRE_OPT,STAGE_CODE as flowStage,GRANTOR,LAST_UPDATE_USER," +
            "LAST_UPDATE_TIME,INST_STATE,OPT_URL,OPT_NAME,os_id,flow_name,apply_time  " +
            "from V_USER_TASK_LIST " +
            "where 1=1 [ :flowInstId| and FLOW_INST_ID = :flowInstId] " +
            " [ :stageArr | and STAGE_CODE in (:stageArr) ] "+
            "[:(like)flowOptName| and FLOW_OPT_NAME like :flowOptName] " +
            "[:(like)flowName| and FLOW_NAME like :flowName] "   +
            "[ :userCode| and USER_CODE = :userCode] " +
            "[ :nodeCode| and NODE_CODE = :nodeCode] " +
            "[ :nodeInstId| and NODE_INST_ID = :nodeInstId] " +
            "[ :flowCode| and FLOW_CODE = :flowCode] " +
            "[ :stageCode| and STAGE_CODE = :stageCode] " +
            "[ :osId| and os_id = :osId] " +
             " order by CREATE_TIME desc " ;


    private final static String actionTaskBaseSql = "select TASK_ID,NODE_INST_ID," +
            "ASSIGN_TIME,EXPIRE_TIME,USER_CODE,ROLE_TYPE,ROLE_CODE,TASK_STATE,IS_VALID,AUTH_DESC" +
            "from WF_ACTION_TASK " +
            "where 1=1 ";

     public Map<String, String> getFilterField() {
        if( filterField == null){
            filterField = new HashMap<>();
            filterField.put(

                "taskId" , CodeBook.EQUAL_HQL_ID);
            filterField.put("nodeInstId" , CodeBook.EQUAL_HQL_ID);
            filterField.put("assignTime" , CodeBook.EQUAL_HQL_ID);
            filterField.put("expireTime" , CodeBook.EQUAL_HQL_ID);
            filterField.put("userCode" , CodeBook.EQUAL_HQL_ID);
            filterField.put("roleType" , CodeBook.EQUAL_HQL_ID);
            filterField.put("roleCode" , CodeBook.EQUAL_HQL_ID);
            filterField.put("taskState" , CodeBook.EQUAL_HQL_ID);
        }
        return filterField;
    }

    /**
     * 生成主键编号
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public long getNextTaskId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_ACTIONTASKNO");
    }

    /**
     *  根据用户编码获取用户已办任务列表
     * @param filter
     * @param pageDesc
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<UserTask> listUserTaskFinByFilter(Map<String,Object> filter, PageDesc pageDesc){

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(userTaskFinBaseSql ,filter);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
                queryAndNamedParams.getQuery(),queryAndNamedParams.getParams(),pageDesc);

        return dataList == null? null : dataList.toJavaList(UserTask.class);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<UserTask> listUserTaskByFilter(Map<String,Object> filter, PageDesc pageDesc){

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(userTaskBaseSql,filter);
        //String querySql = queryAndNamedParams.getQuery()+" order by CREATE_TIME desc ";
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery() ,queryAndNamedParams.getParams(),pageDesc);

        return dataList == null? null : dataList.toJavaList(UserTask.class);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<ActionTask> getActionTaskByNodeidAndUser(long nodeInstId , String userCode){
        String whereSql = "where NODE_INST_ID=? and USER_CODE=? and IS_VALID='T'";

        return this.listObjectsByFilter(whereSql, new Object[]{nodeInstId,userCode});
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public List<ActionTask> listActionTaskByNode(String userCode){
       // String baseHQL = "from WfActionTask t join WfNodeInstance i on i.nodeinstid = t.nodeinstid where i.nodestate <> 'C' and t.usercode = ?";
       //return this.listObjects(baseHQL,userCode);
        Map<String,Object> filterMap = new HashMap<>();
        filterMap.put("userCode",userCode);
        String baseSQL = "SELECT * FROM WF_ACTION_TASK T JOIN FLOW_NODE_INSTANCE I ON I.NODE_INST_ID = T.NODE_INST_ID WHERE I.NODE_STATE <> 'C' AND T.USER_CODE = ?";
        return this.listObjectsBySql(baseSQL,filterMap);
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public String getTaskGrantor(long nodeInstId ,String userCode){
        String baseSQL = "select GRANTOR  from V_USER_TASK_LIST where NODE_INST_ID = ? and USER_CODE =? ";
        List<String> utl = this.getJdbcTemplate().queryForList(baseSQL,
                new Object[]{nodeInstId,userCode} ,String.class);
        if(utl==null || utl.size()==0)
            return null;
        /**
         * 优先已自己的身份执行
         */
        String grantor = userCode;
        for(String task : utl){
            if(StringUtils.isBlank(task))
                 return userCode;
            else
                grantor = task;
        }
        return grantor;
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public boolean hasOptPower(long nodeInstId ,String userCode,String grantorCode){

        String baseSQL = "select GRANTOR  from V_USER_TASK_LIST where NODE_INST_ID = ? and USER_CODE =? ";
        List<String> utl = this.getJdbcTemplate().queryForList(baseSQL,
                new Object[]{nodeInstId,userCode} ,String.class);
        if(utl==null || utl.size()==0)
            return false;
        /**
         * 优先已自己的身份执行
         */
        if(grantorCode !=null && ! grantorCode.equals(userCode)){
            for(String task : utl){
                if(grantorCode.equals(task))
                    return true;

            }
            return false;
        }
        return true;
    }

}
