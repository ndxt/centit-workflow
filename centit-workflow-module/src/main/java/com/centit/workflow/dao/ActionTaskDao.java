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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
            "[ :flowOptTag| and FLOW_OPT_TAG = :flowOptTag] " +
            "[ :stageArr | and STAGE_CODE in (:stageArr) ] "+
            "[ :(like)flowOptName| and FLOW_OPT_NAME like :flowOptName] " +
            "[ :(like)flowName| and FLOW_NAME like :flowName] "   +
            "[ :userCode| and USER_CODE = :userCode] " +
            "[ :nodeInstId| and NODE_INST_ID = :nodeInstId] " +
            "[ :flowCode| and FLOW_CODE = :flowCode] " +
            "[ :stageCode| and STAGE_CODE = :stageCode] " +
            "[ :nodeName| and NODE_NAME = :nodeName] " +
            "[ :osId| and os_id  in (:osId)] " +
            "[ :nodeCode| and NODE_CODE in  (:nodeCode)] " +
            "[ :notNodeCode| and NODE_CODE not in  (:notNodeCode)] " +
             " order by CREATE_TIME desc " ;


    private final static String actionTaskBaseSql = "select TASK_ID,NODE_INST_ID," +
            "ASSIGN_TIME,EXPIRE_TIME,USER_CODE,ROLE_TYPE,ROLE_CODE,TASK_STATE,IS_VALID,AUTH_DESC" +
            "from WF_ACTION_TASK " +
            "where 1=1 ";

    private final static String dynamicSql = "select w.flow_inst_id,w.flow_code,w.version,w.flow_opt_name,w.flow_opt_tag," +
        "  a.node_inst_id,a.unit_code,a.user_code,c.node_code, " +
        " c.node_name,c.node_type,c.opt_type as NODE_OPT_TYPE,c.opt_param,"+
        " w.create_time,w.promise_time,a.time_limit,c.opt_code, " +
        " c.expire_opt,c.stage_code as flowStage,'' as GRANTOR,a.last_update_user," +
        " a.last_update_time,w.inst_state,c.opt_code as opt_url "+
        "from wf_node_instance a " +
        "left join wf_flow_instance w " +
        " on a.flow_inst_id = w.flow_inst_id " +
        "left join wf_node c " +
        " on a.node_Id = c.node_id " +
        "where a.node_state = 'N' " +
        " and w.inst_state = 'N' " +
        " and a.task_assigned = 'D' " +
        " and c.role_type='gw' "+
        " [ :stageArr | and c.STAGE_CODE in (:stageArr) ] "+
        "[:(like) flowOptName| and w.FLOW_OPT_NAME like :flowOptName] " +
        "[ :unitCode| and ( a.unit_code = :unitCode or a.unit_code is null )] " +
        "[ :inUnitCodes| and ( a.unit_code in (:inUnitCodes) or a.unit_code is null )] " +
        "[ :userStation| and c.role_code = :userStation] " +
        "[ :stageCode| and c.STAGE_CODE = :stageCode] "+
        "[ :flowCode| and w.FLOW_CODE = :flowCode] " +
        "[ :nodeCode| and a.NODE_CODE = :nodeCode] " +
        "[ :nodeInstId| and a.node_inst_id = :nodeInstId] " +
        " ORDER by a.create_time desc";

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
    /*@Transactional(propagation= Propagation.MANDATORY)
    public long getNextTaskId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_ACTIONTASKNO");
    }
    */
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
    public List<ActionTask> getActionTaskByNodeidAndUser(String nodeInstId , String userCode){
        String whereSql = "where NODE_INST_ID=? and USER_CODE=? and IS_VALID='T'";

        return this.listObjectsByFilter(whereSql, new Object[]{nodeInstId,userCode});
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public List<ActionTask> listActionTaskByNode(String nodeInstId){
       // String baseHQL = "from WfActionTask t join WfNodeInstance i on i.nodeinstid = t.nodeinstid where i.nodestate <> 'C' and t.usercode = ?";
       //return this.listObjects(baseHQL,userCode);
        Map<String,Object> filterMap = new HashMap<>();
        filterMap.put("nodeInstId",nodeInstId);
        String baseSQL = "SELECT t.* FROM WF_ACTION_TASK T where T.node_inst_id = ?";
        return this.listObjectsBySql(baseSQL,filterMap);
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public String getTaskGrantor(String nodeInstId ,String userCode){
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
    public boolean hasOptPower(String nodeInstId ,String userCode,String grantorCode){

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

    @Transactional
    public List<UserTask> queryStaticTask(String userCode){
        String sql = "select t.flow_inst_id flowInstId," +
            "t.node_Inst_Id nodeInstId," +
            "t.flow_opt_name flowOptName," +
            "t.flow_opt_tag flowOptTag," +
            "t.user_Code userCode," +
            "t.unit_Code unitCode," +
            "t.opt_param opt_param " +
            "from v_user_task_list t where t.user_code = ?";//TODO 字段按需补全
        return this.getJdbcTemplate().query(sql,new Object[]{userCode},new BeanPropertyRowMapper(UserTask.class));
    }

    @Transactional
    public List<UserTask> queryDynamicTask(Map<String, Object> searchColumn, PageDesc pageDesc){

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(dynamicSql,searchColumn);

        JSONArray jsonArray = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,queryAndNamedParams.getQuery(),
            queryAndNamedParams.getParams(),pageDesc);
        return jsonArray == null?null:jsonArray.toJavaList(UserTask.class);
    }
}
