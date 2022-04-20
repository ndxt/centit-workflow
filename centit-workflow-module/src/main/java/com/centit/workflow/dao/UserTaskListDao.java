package com.centit.workflow.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.DBType;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.po.UserTask;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 流程任务数据操作类
 *
 * @author ljy
 * @version $Rev$ <br>
 * $Id$
 */
@Repository
public class UserTaskListDao extends BaseDaoImpl<NodeInstance, String> {

    private final static String flowInstStateSql = "select aa.FLOW_INST_ID, group_concat(DISTINCT bb.Node_Name) as node_name " +
        "from wf_node_instance cc join wf_flow_instance aa on (aa.FLOW_INST_ID = cc.FLOW_INST_ID) " +
        " join WF_NODE bb on (cc.NODE_ID = BB.NODE_ID)" +
        " group by aa.FLOW_INST_ID ";

    private final static String userCompleteTaskBaseSql = "select t.FLOW_INST_ID, t.FLOW_CODE, t.VERSION, t.FLOW_OPT_NAME, " +
        "t.FLOW_OPT_TAG, t.UNIT_CODE, t.USER_CODE, " +
        "t.CREATE_TIME, t.PROMISE_TIME, t.TIME_LIMIT, " +
        "n.NODE_NAME, t.LAST_UPDATE_TIME, t.INST_STATE, " +
        "t.LAST_UPDATE_USER, t.USER_CODE as CREATOR_CODE " +
        " from wf_flow_instance t join wf_flow_define f on f.FLOW_CODE=t.FLOW_CODE and f.VERSION=t.VERSION" +
        " left join ("+flowInstStateSql+") n " +
        " on n.FLOW_INST_ID=t.FLOW_INST_ID " +
        " where t.flow_inst_id in  (select w.flow_inst_id from wf_node_instance w join wf_node n " +
        "on n.node_id=w.node_id where w.NODE_STATE in ('C', 'F', 'P') [ :userCode| and w.last_update_user=:userCode] " +
        " [ :nodeCode| and n.node_code = :nodeCode] [ :nodeCodes | and n.node_code in (:nodeCodes)]  )" +
        " [ :(like)flowOptName| and t.flow_Opt_Name like :flowOptName] " +
        " [ :(like)flowName| and f.flow_Name like :flowName]  " +
        " [ :osId| and f.os_id = :osId] " +
        " [ :optId| and t.OPT_ID = :optId] " +
        " [ :osIds| and f.os_id in (:osIds)] " +
        " [ :(like)nodeName| and n.node_Name like :nodeName] " +
        " order by t.last_update_time desc ";

    private final static String allAboutNodeTask = "select a.FLOW_INST_ID, a.FLOW_CODE, a.VERSION, a.FLOW_OPT_NAME," +
        "a.FLOW_OPT_TAG, b.NODE_INST_ID, b.UNIT_CODE,b.USER_CODE, b.ROLE_TYPE," +
        "b.ROLE_CODE, '引擎分配' as AUTH_DESC, c.NODE_CODE, c.NODE_NAME, c.NODE_TYPE," +
        "c.OPT_TYPE as NODE_OPT_TYPE,c.OPT_PARAM, b.CREATE_TIME, b.PROMISE_TIME, b.TIME_LIMIT," +
        "c.OPT_CODE,c.EXPIRE_OPT,c.STAGE_CODE,b.GRANTOR,b.LAST_UPDATE_USER," +
        "b.LAST_UPDATE_TIME,b.NODE_STATE as INST_STATE,c.OPT_ID as OPT_URL,c.OS_ID, c.OPT_PARAM as NODE_PARAM, a.USER_CODE as CREATOR_CODE, " +
        "a.PROMISE_TIME as flow_promise_time, a.TIME_LIMIT as flow_time_limit " +
        "from wf_node_instance b join  wf_flow_instance a on (a.FLOW_INST_ID = b.FLOW_INST_ID)" +
        " join WF_NODE c on (b.NODE_ID = c.NODE_ID)" +
        "where b.NODE_INST_ID = ?";

    private final static String userStaticTaskBaseSql = "select a.FLOW_INST_ID, a.FLOW_CODE, a.VERSION, a.FLOW_OPT_NAME," +
        "a.FLOW_OPT_TAG, b.NODE_INST_ID, b.UNIT_CODE,b.USER_CODE, b.ROLE_TYPE," +
        "b.ROLE_CODE, '引擎分配' as AUTH_DESC, c.NODE_CODE, c.NODE_NAME, c.NODE_TYPE," +
        "c.OPT_TYPE as NODE_OPT_TYPE,c.OPT_PARAM, b.CREATE_TIME, b.PROMISE_TIME, b.TIME_LIMIT," +
        "c.OPT_CODE,c.EXPIRE_OPT,c.STAGE_CODE,b.GRANTOR,b.LAST_UPDATE_USER," +
        "b.LAST_UPDATE_TIME,b.NODE_STATE as INST_STATE,c.OPT_ID as OPT_URL,c.OS_ID, c.OPT_PARAM as NODE_PARAM, a.USER_CODE as CREATOR_CODE, " +
        "a.PROMISE_TIME as flow_promise_time, a.TIME_LIMIT as flow_time_limit " +
        "from wf_node_instance b join  wf_flow_instance a on (a.FLOW_INST_ID = b.FLOW_INST_ID)" +
            " join WF_NODE c on (b.NODE_ID = c.NODE_ID)" +
        "where b.node_state = 'N' and a.inst_state = 'N' and b.task_assigned = 'S' [ :flowInstId| and b.FLOW_INST_ID = :flowInstId]" +
        " [ :(splitforin)flowInstIds| and b.FLOW_INST_ID in ( :flowInstIds )]" +
        "[ :flowOptTag| and b.FLOW_OPT_TAG = :flowOptTag]" +
        "[ :stageArr | and c.STAGE_CODE in (:stageArr) ]" +
        "[ :(like)flowOptName| and b.FLOW_OPT_NAME like :flowOptName]" +
        "[ :userCode| and b.USER_CODE = :userCode]" +
        "[ :creatorCode| and a.USER_CODE = :creatorCode]" +
        "[ :unitCode| and b.UNIT_CODE = :unitCode]" +
        "[ :beginTime| and b.CREATE_TIME >= :beginTime]" +
        "[ :endTime| and b.CREATE_TIME <= :endTime]" +
        "[ :osId| and c.OS_ID = :osId]" +
        "[ :optId| and c.OPT_ID = :optId]" +
        "[ :optCode| and c.OPT_CODE = :optCode]" +
        "[ :osIds| and c.OS_ID  in (:osIds)]" +
        "[ :nodeInstId| and b.NODE_INST_ID = :nodeInstId]" +
        "[ :flowCode| and a.FLOW_CODE = :flowCode]" +
        "[ :stageCode| and c.STAGE_CODE = :stageCode]" +
        "[ :nodeName| and c.NODE_NAME in (:nodeName)]" +
        "[ :nodeNames| and c.NODE_NAME in (:nodeNames)]" +
        "[ :nodeCode| and c.NODE_CODE in  (:nodeCode)]" +
        "[ :nodeCodes| and c.NODE_CODE in  (:nodeCodes)]" +
        "[ :notNodeCode| and c.NODE_CODE not in  (:notNodeCode)]" +
        "[ :notNodeCodes| and c.NODE_CODE not in  (:notNodeCodes)]";

    private final static String userGrantorTaskBaseSql = "select a.FLOW_INST_ID, a.FLOW_CODE, a.VERSION, a.FLOW_OPT_NAME," +
        "a.FLOW_OPT_TAG, b.NODE_INST_ID, b.UNIT_CODE,g.GRANTEE as USER_CODE, b.ROLE_TYPE," +
        "b.ROLE_CODE, '权限委托' as AUTH_DESC, c.NODE_CODE, c.NODE_NAME, c.NODE_TYPE," +
        "c.OPT_TYPE as NODE_OPT_TYPE,c.OPT_PARAM, b.CREATE_TIME, b.PROMISE_TIME, b.TIME_LIMIT," +
        "c.OPT_CODE,c.EXPIRE_OPT,c.STAGE_CODE, g.GRANTOR, b.LAST_UPDATE_USER," +
        "b.LAST_UPDATE_TIME,b.NODE_STATE as INST_STATE,c.OPT_ID as OPT_URL,c.OS_ID, c.OPT_PARAM as NODE_PARAM, a.USER_CODE as CREATOR_CODE, " +
        "a.PROMISE_TIME as flow_promise_time, a.TIME_LIMIT as flow_time_limit " +
        "from wf_node_instance b join  wf_flow_instance a on (a.FLOW_INST_ID = b.FLOW_INST_ID)" +
        " join WF_NODE c on (b.NODE_ID = c.NODE_ID)" +
        " join WF_ROLE_RELEGATE g on(g.GRANTOR = b.user_code and (g.unit_code is null or b.unit_code = g.unit_code) " +
          " and (g.ROLE_CODE is null or g.ROLE_CODE = b.ROLE_CODE))" +
        "where b.node_state = 'N' and a.inst_state = 'N' [ :flowInstId| and b.FLOW_INST_ID = :flowInstId]" +
        " [ :(splitforin)flowInstIds| and b.FLOW_INST_ID in ( :flowInstIds )]" +
        "[ :flowOptTag| and b.FLOW_OPT_TAG = :flowOptTag]" +
        "[ :stageArr | and c.STAGE_CODE in (:stageArr) ]" +
        "[ :(like)flowOptName| and b.FLOW_OPT_NAME like :flowOptName]" +
        "[ :userCode| and g.GRANTEE = :userCode]" +
        "[ :grantor| and g.GRANTOR = :grantor]" +
        "[ :creatorCode| and a.USER_CODE = :creatorCode]" +
        "[ :unitCode| and b.UNIT_CODE = :unitCode]" +
        "[ :beginTime| and b.CREATE_TIME >= :beginTime]" +
        "[ :endTime| and b.CREATE_TIME <= :endTime]" +
        "[ :osId| and c.OS_ID = :osId]" +
        "[ :optId| and c.OPT_ID = :optId]" +
        "[ :optCode| and c.OPT_CODE = :optCode]" +
        "[ :osIds| and c.OS_ID  in (:osIds)]" +
        "[ :nodeInstId| and b.NODE_INST_ID = :nodeInstId]" +
        "[ :flowCode| and a.FLOW_CODE = :flowCode]" +
        "[ :stageCode| and c.STAGE_CODE = :stageCode]" +
        "[ :nodeName| and c.NODE_NAME in (:nodeName)]" +
        "[ :nodeNames| and c.NODE_NAME in (:nodeNames)]" +
        "[ :nodeCode| and c.NODE_CODE in  (:nodeCode)]" +
        "[ :nodeCodes| and c.NODE_CODE in  (:nodeCodes)]" +
        "[ :notNodeCode| and c.NODE_CODE not in  (:notNodeCode)]" +
        "[ :notNodeCodes| and c.NODE_CODE not in  (:notNodeCodes)]";


    private final static String userDynamicTaskSqlPart1 =
        "select a.FLOW_INST_ID, a.FLOW_CODE, a.VERSION, a.FLOW_OPT_NAME," +
            "a.FLOW_OPT_TAG, b.NODE_INST_ID, b.UNIT_CODE,b.USER_CODE, b.ROLE_TYPE," +
            "b.ROLE_CODE, '引擎分配' as AUTH_DESC, c.NODE_CODE, c.NODE_NAME, c.NODE_TYPE," +
            "c.OPT_TYPE as NODE_OPT_TYPE,c.OPT_PARAM, b.CREATE_TIME, b.PROMISE_TIME, b.TIME_LIMIT," +
            "c.OPT_CODE,c.EXPIRE_OPT,c.STAGE_CODE,b.GRANTOR,b.LAST_UPDATE_USER," +
            "b.LAST_UPDATE_TIME,b.NODE_STATE as INST_STATE,c.OPT_ID as OPT_URL,c.OS_ID, c.OPT_PARAM as NODE_PARAM, a.USER_CODE as CREATOR_CODE, " +
            "a.PROMISE_TIME as flow_promise_time, a.TIME_LIMIT as flow_time_limit " +
            "from wf_node_instance b join  wf_flow_instance a on (a.FLOW_INST_ID = b.FLOW_INST_ID)" +
            " join WF_NODE c on (b.NODE_ID = c.NODE_ID)" +
            "where b.node_state = 'N' and a.inst_state = 'N' and b.task_assigned = 'D'" ;
            // " and c.role_type='GW' 目前只有这个

    private final static String userDynamicTaskSqlPart2 =
            "[ :flowInstId| and b.FLOW_INST_ID = :flowInstId]" +
            "[ :(splitforin)flowInstIds| and b.FLOW_INST_ID in ( :flowInstIds )]" +
            "[ :flowOptTag| and b.FLOW_OPT_TAG = :flowOptTag]" +
            "[ :stageArr | and c.STAGE_CODE in (:stageArr) ]" +
            "[ :(like)flowOptName| and b.FLOW_OPT_NAME like :flowOptName]" +
            "[ :unitCode| and ( b.unit_code = :unitCode or b.unit_code is null )] " +
            "[ :beginTime| and b.CREATE_TIME >= :beginTime]" +
            "[ :endTime| and b.CREATE_TIME <= :endTime]" +
            "[ :osId| and c.OS_ID = :osId]" +
            "[ :optId| and c.OPT_ID = :optId]" +
            "[ :optCode| and c.OPT_CODE = :optCode]" +
            "[ :osIds| and c.OS_ID  in (:osIds)]" +
            "[ :nodeInstId| and b.NODE_INST_ID = :nodeInstId]" +
            "[ :flowCode| and a.FLOW_CODE = :flowCode]" +
            "[ :stageCode| and c.STAGE_CODE = :stageCode]" +
            "[ :nodeName| and c.NODE_NAME in (:nodeName)]" +
            "[ :nodeNames| and c.NODE_NAME in (:nodeNames)]" +
            "[ :nodeCode| and c.NODE_CODE in  (:nodeCode)]" +
            "[ :nodeCodes| and c.NODE_CODE in  (:nodeCodes)]" +
            "[ :notNodeCode| and c.NODE_CODE not in  (:notNodeCode)]" +
            "[ :notNodeCodes| and c.NODE_CODE not in  (:notNodeCodes)]";

    /**
     * 根据用户编码获取用户已办任务列表
     *
     * @param filter
     * @param pageDesc
     * @return
     */
    @Transactional
    public List<UserTask> listUserCompletedTask(Map<String, Object> filter, PageDesc pageDesc) {
        String sql = userCompleteTaskBaseSql;
        DBType dbType = DBType.mapDBType(CodeRepositoryUtil.getSysConfigValue("jdbc.driver"));
        if (dbType == DBType.Oracle || dbType == DBType.Oscar ||dbType == DBType.DM) {
            sql = userCompleteTaskBaseSql.replace("group_concat", "wm_concat");
        }
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql, filter);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(), queryAndNamedParams.getParams(), pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

    @Transactional
    public UserTask getNodeTaskInfo(String nodeInstId) {
        JSONObject data = DatabaseOptUtils.getObjectBySqlAsJson(this,
            allAboutNodeTask, new Object[]{nodeInstId});

        return data == null ? null : data.toJavaObject(UserTask.class);
    }

    @Transactional
    public List<UserTask> listUserStaticTask(Map<String, Object> filter, PageDesc pageDesc) {

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(
            userStaticTaskBaseSql + " order by b.CREATE_TIME desc ", filter);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(), queryAndNamedParams.getParams(), pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

    @Transactional
    public List<UserTask> listUserGrantorTask(Map<String, Object> filter, PageDesc pageDesc) {

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(
            userGrantorTaskBaseSql + " order by b.CREATE_TIME desc ", filter);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(), queryAndNamedParams.getParams(), pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

    // userStaticTaskBaseSql +" union all " + userGrantorTaskBaseSql;
    @Transactional
    public List<UserTask> listUserStaticAndGrantorTask(Map<String, Object> filter, PageDesc pageDesc) {
        QueryAndNamedParams staticQuery = QueryUtils.translateQuery(userStaticTaskBaseSql , filter);
        String staticSql = staticQuery.getQuery();
        String staticCountSql = QueryUtils.buildGetCountSQLByReplaceFields(staticSql);//rowcounts
        QueryAndNamedParams grantorQuery = QueryUtils.translateQuery(userGrantorTaskBaseSql , filter);
        String grantorSql = grantorQuery.getQuery();
        String grantorCountSql = QueryUtils.buildGetCountSQLByReplaceFields(grantorSql);//rowcounts
        //合并参数
        grantorQuery.getParams().putAll(staticQuery.getParams());

        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            "select * from (" + staticSql +" union all " + grantorSql +") order by b.CREATE_TIME desc",(String[])null,
            "select sum(t.rowcounts) as rowcounts from (" + staticCountSql +" union all " + grantorCountSql +") t",
            grantorQuery.getParams(), pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

    public static String buildDynamicTaskSql(List<? extends IUserUnit> userUnits){
        StringBuilder sqlBuilder = new StringBuilder(3072);
        int uuCount = userUnits.size();
        sqlBuilder.append(userDynamicTaskSqlPart1).append(" and ");
        if(uuCount == 1) {
            sqlBuilder.append("(b.unit_code is null or b.unit_code =:userUnitCode0) and b.role_code = :userStation0");
        }else  if(uuCount > 1) {
            sqlBuilder.append(" (");
            for (int i = 0; i < uuCount; i++) {
                if (i > 0) {
                    sqlBuilder.append(" or ");
                }
                sqlBuilder.append("( (b.unit_code is null or b.unit_code =:userUnitCode").append(i)
                    .append(") and b.role_code = :userStation").append(i).append(")");
            }
            sqlBuilder.append(" ) ");
        }

        sqlBuilder.append(userDynamicTaskSqlPart2);
        return sqlBuilder.toString();
    }

    public static void appendDynamicQueryParams( Map<String, Object> queryMap, List<? extends IUserUnit> userUnits){
        int i=0;
        for(IUserUnit uu : userUnits){
            queryMap.put("userUnitCode"+i, uu.getUnitCode());
            queryMap.put("userStation"+i, uu.getUserStation());
            i++;
        }
    }

    @Transactional
    public List<UserTask> listUserDynamicTask(List<? extends IUserUnit> userUnits, Map<String, Object> filter, PageDesc pageDesc) {
        String querySql = buildDynamicTaskSql(userUnits);
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(querySql + "order by CREATE_TIME desc ", filter);
        Map<String, Object> queryParamMap = CollectionsOpt.unionTwoMap(queryAndNamedParams.getParams(), filter);
        appendDynamicQueryParams(queryParamMap, userUnits);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(), queryParamMap, pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

    @Transactional
    public List<UserTask> listUserAllTask(List<? extends IUserUnit> userUnits, Map<String, Object> filter, PageDesc pageDesc) {

        QueryAndNamedParams staticQuery = QueryUtils.translateQuery(userStaticTaskBaseSql , filter);
        String staticSql = staticQuery.getQuery();
        String staticCountSql = QueryUtils.buildGetCountSQLByReplaceFields(staticSql);//rowcounts
        QueryAndNamedParams grantorQuery = QueryUtils.translateQuery(userGrantorTaskBaseSql , filter);
        String grantorSql = grantorQuery.getQuery();
        String grantorCountSql = QueryUtils.buildGetCountSQLByReplaceFields(grantorSql);//rowcounts
        QueryAndNamedParams dynamicQuery = QueryUtils.translateQuery(buildDynamicTaskSql(userUnits) , filter);
        String dynamicSql = dynamicQuery.getQuery();
        String dynamicCountSql = QueryUtils.buildGetCountSQLByReplaceFields(dynamicSql);//rowcounts

        Map<String, Object> queryParamMap = CollectionsOpt.unionTwoMap(grantorQuery.getParams(), filter);
        queryParamMap.putAll(dynamicQuery.getParams());
        queryParamMap.putAll(staticQuery.getParams());

        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            "select * from (" + staticSql +" union all " + grantorSql +" union all " + dynamicSql + ") b order by CREATE_TIME desc",(String[])null,
            "select sum(t.rowcounts) as rowcounts from (" + staticCountSql +" union all " + grantorCountSql +" union all " + dynamicCountSql +") t",
            queryParamMap, pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

}
