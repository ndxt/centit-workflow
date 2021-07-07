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
public class ActionTaskDao extends BaseDaoImpl<ActionTask, String>
{
    /*private final static String userTaskFinBaseSql = "select FLOW_INST_ID, FLOW_CODE,VERSION,FLOW_OPT_NAME," +
            "FLOW_OPT_TAG,NODE_INST_ID,UNIT_CODE,USER_CODE,ROLE_TYPE," +
            "ROLE_CODE,AUTH_DESC,NODE_CODE,NODE_NAME,NODE_TYPE," +
            "NODE_OPT_TYPE,OPT_PARAM,CREATE_TIME,PROMISE_TIME,TIME_LIMIT," +
            "OPT_CODE,EXPIRE_OPT,STAGE_CODE,GRANTOR,LAST_UPDATE_USER," +
            "LAST_UPDATE_TIME,INST_STATE, OPT_URL, NODE_PARAM" +
            "from V_USER_TASK_LIST_FIN " +
            "where 1=1 [ :flowInstId| and FLOW_INST_ID = :flowInstId] " +
            "[ :userCode| and USER_CODE = :userCode] " +
            "[ :osId| and OS_ID = :osId] " +
            "[ :optId| and OPT_ID = :optId] " +
            "[ :optCode| and OPT_CODE = :optCode] " +
            "[ :nodeCode| and NODE_CODE = :nodeCode] " +
            "[ :nodeInstId| and NODE_INST_ID = :nodeInstId] " +
            "[ :flowCode| and FLOW_CODE = :flowCode] " +
            "[ :stageCode| and STAGE_CODE = :stageCode] ";*/

    private final static String userCompleteTaskBaseSql = "select t.FLOW_INST_ID, t.FLOW_CODE, t.VERSION, t.FLOW_OPT_NAME, " +
        "t.FLOW_OPT_TAG, t.UNIT_CODE, t.USER_CODE, " +
        "t.CREATE_TIME, t.PROMISE_TIME, t.TIME_LIMIT, " +
        "n.NODE_NAME, t.LAST_UPDATE_TIME, t.INST_STATE, " +
        "t.LAST_UPDATE_USER, t.USER_CODE as CREATOR_CODE " +
        " from wf_flow_instance t join wf_flow_define f on f.FLOW_CODE=t.FLOW_CODE and f.VERSION=t.VERSION" +
        " left join (select group_concat(DISTINCT Node_Name) as node_name,FLOW_INST_ID from v_user_task_list GROUP BY FLOW_INST_ID) n " +
        " on n.FLOW_INST_ID=t.FLOW_INST_ID " +
        " where t.flow_inst_id in  (select w.flow_inst_id from wf_node_instance w join wf_node n " +
        "on n.node_id=w.node_id where w.NODE_STATE in ('C', 'F', 'P') [ :userCode| and w.last_update_user=:userCode] " +
        "  [ :nodeCode| and n.node_code in (:nodeCode)]  [ :(like)nodeName| and n.node_Name like :nodeName]  )" +
        " [ :(like)flowOptName| and t.flow_Opt_Name like :flowOptName] " +
        " [ :(like)flowName| and f.flow_Name like :flowName]  " +
        "  [ :osId| and f.os_id = :osId] " +
        "  [ :osIds| and f.os_id in (:osIds)] " +
        " order by t.last_update_time desc ";

    private final static String userTaskBaseSql = "select FLOW_INST_ID, FLOW_CODE,VERSION,FLOW_OPT_NAME," +
            "FLOW_OPT_TAG,NODE_INST_ID,UNIT_CODE,USER_CODE,ROLE_TYPE," +
            "ROLE_CODE,AUTH_DESC,NODE_CODE,NODE_NAME,NODE_TYPE," +
            "NODE_OPT_TYPE,OPT_PARAM,CREATE_TIME,PROMISE_TIME,TIME_LIMIT," +
            "OPT_CODE,EXPIRE_OPT,STAGE_CODE,GRANTOR,LAST_UPDATE_USER," +
            "LAST_UPDATE_TIME,INST_STATE,OPT_URL,OS_ID, NODE_PARAM, CREATOR_CODE, " +
            "flow_promise_time, flow_time_limit " +
            "from V_USER_TASK_LIST " +
            "where 1=1 [ :flowInstId| and FLOW_INST_ID = :flowInstId] " +
            "[ :flowOptTag| and FLOW_OPT_TAG = :flowOptTag] " +
            "[ :stageArr | and STAGE_CODE in (:stageArr) ] "+
            "[ :(like)flowOptName| and FLOW_OPT_NAME like :flowOptName] " +
            "[ :userCode| and USER_CODE = :userCode] " +
            "[ :grantor| and GRANTOR = :grantor] " +
            "[ :osId| and OS_ID = :osId] " +
            "[ :optId| and OPT_ID = :optId] " +
            "[ :optCode| and OPT_CODE = :optCode] " +
            "[ :osIds| and OS_ID  in (:osIds)] " +
            "[ :nodeInstId| and NODE_INST_ID = :nodeInstId] " +
            "[ :flowCode| and FLOW_CODE = :flowCode] " +
            "[ :stageCode| and STAGE_CODE = :stageCode] " +
            "[ :nodeName| and NODE_NAME in (:nodeName)] " +
            "[ :nodeNames| and NODE_NAME in (:nodeNames)] " +
            "[ :nodeCode| and NODE_CODE in  (:nodeCode)] " +
            "[ :nodeCodes| and NODE_CODE in  (:nodeCodes)] " +
            "[ :notNodeCode| and NODE_CODE not in  (:notNodeCode)] " +
            "[ :notNodeCodes| and NODE_CODE not in  (:notNodeCodes)] " +
             " order by CREATE_TIME desc " ;


    /*private final static String actionTaskBaseSql = "select TASK_ID,NODE_INST_ID," +
            "ASSIGN_TIME,EXPIRE_TIME,USER_CODE,ROLE_TYPE,ROLE_CODE,TASK_STATE,IS_VALID,AUTH_DESC" +
            "from WF_ACTION_TASK " +
            "where 1=1 ";*/

    private final static String dynamicSql = "select w.flow_inst_id,w.flow_code,w.version,w.flow_opt_name,w.flow_opt_tag," +
        "  a.node_inst_id,a.unit_code,a.user_code,c.node_code, " +
        " c.node_name,c.node_type,c.opt_type as NODE_OPT_TYPE,c.opt_param,"+
        " w.create_time,w.promise_time,a.time_limit,c.opt_code, " +
        " c.expire_opt,c.stage_code,'' as GRANTOR,a.last_update_user," +
        " a.last_update_time,w.inst_state,c.opt_code as opt_url, a.NODE_PARAM, w.USER_CODE as CREATOR_CODE "+
        "from wf_node_instance a " +
        "join wf_flow_instance w " +
        " on a.flow_inst_id = w.flow_inst_id " +
        "join wf_node c " +
        " on a.node_Id = c.node_id " +
        "join wf_flow_define f on(w.FLOW_CODE = f.FLOW_CODE and w.VERSION = f.version) "+
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
        "[ :osId| and f.OS_ID = :osId] " +
        //"[ :optId| and OPT_ID = :optId] " +
        //"[ :optCode| and OPT_CODE = :optCode] " +
        "[ :osIds| and f.OS_ID  in (:osIds)] " +
        " ORDER by a.create_time desc";

    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        //filterField.put("taskId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("osId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("optId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("optCode" , CodeBook.EQUAL_HQL_ID);
        filterField.put("nodeInstId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("assignTime" , CodeBook.EQUAL_HQL_ID);
        filterField.put("expireTime" , CodeBook.EQUAL_HQL_ID);
        filterField.put("userCode" , CodeBook.EQUAL_HQL_ID);
        filterField.put("roleType" , CodeBook.EQUAL_HQL_ID);
        filterField.put("roleCode" , CodeBook.EQUAL_HQL_ID);
        filterField.put("taskState" , CodeBook.EQUAL_HQL_ID);

        return filterField;
    }

    /**
     *  根据用户编码获取用户已办任务列表
     * @param filter
     * @param pageDesc
     * @return
     */
    @Transactional
    public List<UserTask> listUserTaskFinByFilter(Map<String,Object> filter, PageDesc pageDesc){

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(userCompleteTaskBaseSql ,filter);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
                queryAndNamedParams.getQuery(),queryAndNamedParams.getParams(),pageDesc);

        return dataList == null? null : dataList.toJavaList(UserTask.class);
    }

    @Transactional
    public List<UserTask> listUserTaskByFilter(Map<String,Object> filter, PageDesc pageDesc){

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(userTaskBaseSql,filter);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery() ,queryAndNamedParams.getParams(),pageDesc);

        return dataList == null? null : dataList.toJavaList(UserTask.class);
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public String checkTaskGrantor(String nodeInstId, String userCode, String grantorCode){
        String baseSQL = "select GRANTOR from V_USER_TASK_LIST where NODE_INST_ID = ? and USER_CODE =? ";
        List<String> utl = this.getJdbcTemplate().queryForList(baseSQL,
                new Object[]{nodeInstId, userCode}, String.class);
        if(utl==null || utl.size()==0) {
            return null;
        }
        /**
         * 优先已自己的身份执行
         */
        String grantor = "";
        for(String task : utl){
            if(StringUtils.equals(task, userCode)){
                 return userCode;
            } else if(StringUtils.equals(task, grantorCode) ||
                (StringUtils.isBlank(grantor)  && StringUtils.isNotBlank(task) ) ){
                grantor = task;
            }
        }
        if(StringUtils.isNotBlank(grantor)){
            return grantor;
        }
        return userCode;
    }


    @Transactional
    public List<UserTask> queryStaticTask(String userCode){
        String sql = "select t.flow_inst_id ," +
            "t.node_Inst_Id," +
            "t.flow_opt_name," +
            "t.flow_opt_tag," +
            "t.user_Code," +
            "t.unit_Code," +
            "t.opt_param," +
            "t.NODE_PARAM " +
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
