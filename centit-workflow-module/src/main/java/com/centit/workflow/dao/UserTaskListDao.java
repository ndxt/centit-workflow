package com.centit.workflow.dao;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.basedata.UserUnit;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.compiler.Lexer;
import com.centit.support.database.jsonmaptable.GeneralJsonObjectDao;
import com.centit.support.database.metadata.SimpleTableField;
import com.centit.support.database.orm.JpaMetadata;
import com.centit.support.database.orm.TableMapInfo;
import com.centit.support.database.utils.DBType;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.po.UserTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 流程任务数据操作类
 * @author codefan@sina.com
 * @version $Rev$ <br>
 * $Id$
 */
@Repository
public class UserTaskListDao extends BaseDaoImpl<NodeInstance, String> {

    private final static String flowInstStateSqlMySql = "select aa.FLOW_INST_ID, group_concat(DISTINCT bb.Node_Name) as node_name " +
        "from wf_node_instance cc join wf_flow_instance aa on (aa.FLOW_INST_ID = cc.FLOW_INST_ID) " +
        " join WF_NODE bb on (cc.NODE_ID = bb.NODE_ID)" +
        " group by aa.FLOW_INST_ID ";

    private final static String flowInstStateSqlPG = "select aa.FLOW_INST_ID, string_agg(DISTINCT bb.Node_Name) as node_name " +
        "from wf_node_instance cc join wf_flow_instance aa on (aa.FLOW_INST_ID = cc.FLOW_INST_ID) " +
        " join WF_NODE bb on (cc.NODE_ID = bb.NODE_ID)" +
        " group by aa.FLOW_INST_ID ";

    private final static String flowInstStateSqlOracle12c = "select aa.FLOW_INST_ID, " +
        "listagg(to_char(bb.Node_Name),',') within group (order by cc.last_update_time) as node_name " +
        "from wf_node_instance cc join wf_flow_instance aa on (aa.FLOW_INST_ID = cc.FLOW_INST_ID) " +
        " join WF_NODE bb on (cc.NODE_ID = bb.NODE_ID)" +
        " group by aa.FLOW_INST_ID";

    private final static String flowInstStateSqlChinese = "select aa.FLOW_INST_ID, wm_concat(DISTINCT bb.Node_Name) as node_name " +
        "from wf_node_instance cc join wf_flow_instance aa on (aa.FLOW_INST_ID = cc.FLOW_INST_ID) " +
        " join WF_NODE bb on (cc.NODE_ID = bb.NODE_ID)" +
        " group by aa.FLOW_INST_ID ";

    private final static String userCompleteTaskBaseSqlPart1= "select t.FLOW_INST_ID, t.FLOW_CODE, t.VERSION, t.FLOW_OPT_NAME, " +
        "t.FLOW_OPT_TAG, t.UNIT_CODE, t.USER_CODE, " +
        "t.CREATE_TIME, t.deadline_time as node_Expire_Time, " +
        "n.NODE_NAME, t.LAST_UPDATE_TIME, t.INST_STATE, " +
        "t.LAST_UPDATE_USER, t.USER_CODE as CREATOR_CODE, t.OS_ID, t.OPT_ID as MODEL_ID, f.OPT_ID " +
        " from wf_flow_instance t join wf_flow_define f on f.FLOW_CODE=t.FLOW_CODE and f.VERSION=t.VERSION" +
        " left join ";//  ("+flowInstStateSql+") n " +

    private final static String userCompleteTaskBaseSqlPart2 = "n on n.FLOW_INST_ID=t.FLOW_INST_ID " +
        " where t.flow_inst_id in  (select w.flow_inst_id from wf_node_instance w join wf_node n " +
        " on n.node_id=w.node_id where w.NODE_STATE in ('C', 'F', 'P') [ :userCode| and w.last_update_user=:userCode] " +
        " [ :nodeCode| and n.node_code = :nodeCode] [ :nodeCodes | and n.node_code in (:nodeCodes)]  )" +
        " [ :(like)flowOptName| and t.flow_Opt_Name like :flowOptName] " +
        " [ :(like)flowName| and f.flow_Name like :flowName] " +
        " [ :topUnit| and t.TOP_UNIT = :topUnit]" +
        " [ :osId| and t.os_id = :osId] " +
        " [ :modelId| and t.OPT_ID = :modelId] " +
        " [ :optId| and f.OPT_ID = :optId] " +
        " [ :osIds| and t.os_id in (:osIds)] " +
        " [ :(like)nodeName| and n.node_Name like :nodeName] " +
        " order by t.last_update_time desc ";

    private final static String allAboutNodeTask = "select a.FLOW_INST_ID, a.FLOW_CODE, a.VERSION, a.FLOW_OPT_NAME," +
        "a.FLOW_OPT_TAG, b.NODE_INST_ID, b.UNIT_CODE, b.USER_CODE, b.ROLE_TYPE," +
        "b.ROLE_CODE, b.TASK_ASSIGNED, '引擎分配' as AUTH_DESC, c.NODE_CODE, c.NODE_NAME, c.NODE_TYPE," +
        "c.OPT_TYPE as NODE_OPT_TYPE, c.OPT_PARAM, b.CREATE_TIME, b.deadline_time as node_Expire_Time, " +
        "c.OPT_CODE, c.EXPIRE_OPT, c.STAGE_CODE, b.GRANTOR, b.LAST_UPDATE_USER," +
        "b.LAST_UPDATE_TIME, b.NODE_STATE as INST_STATE, c.OPT_ID, a.OPT_ID as MODEL_ID, a.OS_ID, a.USER_CODE as CREATOR_CODE, " +
        "b.warning_time as node_warning_Time, a.warning_time as flow_warning_Time, " +
        "a.deadline_time as flow_Expire_Time, c.Time_Limit as promise_Time,a.create_time as flow_create_time " +
        "from wf_node_instance b join wf_flow_instance a on (a.FLOW_INST_ID = b.FLOW_INST_ID) " +
        "join WF_NODE c on (b.NODE_ID = c.NODE_ID) " +
        "where b.NODE_INST_ID = ?";

    private final static String userStaticTaskBaseSql = "select a.FLOW_INST_ID, a.FLOW_CODE, a.VERSION, a.FLOW_OPT_NAME," +
        "a.FLOW_OPT_TAG, b.NODE_INST_ID, b.UNIT_CODE, b.USER_CODE, b.ROLE_TYPE," +
        "b.ROLE_CODE, '引擎分配' as AUTH_DESC, c.NODE_CODE, c.NODE_NAME, c.NODE_TYPE," +
        "c.OPT_TYPE as NODE_OPT_TYPE,c.OPT_PARAM, b.CREATE_TIME, b.deadline_time as node_Expire_Time, " +
        "c.OPT_CODE, c.EXPIRE_OPT, c.STAGE_CODE, b.GRANTOR, b.LAST_UPDATE_USER," +
        "b.LAST_UPDATE_TIME,b.NODE_STATE as INST_STATE, c.OPT_ID, a.OPT_ID as MODEL_ID, a.OS_ID, a.USER_CODE as CREATOR_CODE, " +
        "b.warning_time as node_warning_Time, a.warning_time as flow_warning_Time, " +
        "a.deadline_time as flow_Expire_Time, c.Time_Limit as promise_Time,a.create_time as flow_create_time " +
        "from wf_node_instance b join wf_flow_instance a on (a.FLOW_INST_ID = b.FLOW_INST_ID) " +
            "join WF_NODE c on (b.NODE_ID = c.NODE_ID) " +
        // (b.task_assigned = 'S' or b.task_assigned = 'P' or b.task_assigned = 'T')
        "where b.node_state = 'N' and a.inst_state = 'N' and b.task_assigned = 'S' [ :flowInstId| and b.FLOW_INST_ID = :flowInstId]" +
        " [ :(splitforin)flowInstIds| and b.FLOW_INST_ID in ( :flowInstIds )]" +
        "[ :flowOptTag| and a.FLOW_OPT_TAG = :flowOptTag]" +
        "[ :stageArr | and c.STAGE_CODE in (:stageArr) ]" +
        "[ :(like)flowOptName| and a.FLOW_OPT_NAME like :flowOptName]" +
        "[ :userCode| and b.USER_CODE = :userCode]" +
        "[ :creatorCode| and a.USER_CODE = :creatorCode]" +
        "[ :topUnit| and a.TOP_UNIT = :topUnit]" +
        "[ :unitCode| and b.UNIT_CODE = :unitCode]" +
        "[ :(DATETIME)beginTime| and b.CREATE_TIME >= :beginTime]" +
        "[ :(DATETIME)endTime| and b.CREATE_TIME <= :endTime]" +
        "[ :osId| and a.OS_ID = :osId]" +
        "[ :optId| and c.OPT_ID = :optId]" +
        "[ :(splitforin)modelId| and a.OPT_ID in (:modelId)] " +
        "[ :optCode| and c.OPT_CODE = :optCode]" +
        "[ :osIds| and a.OS_ID  in (:osIds)]" +
        "[ :nodeInstId| and b.NODE_INST_ID = :nodeInstId]" +
        "[ :flowCode| and a.FLOW_CODE = :flowCode]" +
        "[ :stageCode| and c.STAGE_CODE = :stageCode]" +
        "[ :nodeName| and c.NODE_NAME = :nodeName]" +
        "[ :nodeNames| and c.NODE_NAME in (:nodeNames)]" +
        "[ :nodeCode| and c.NODE_CODE = :nodeCode]" +
        "[ :(startWith)nodeCodeStart | and c.NODE_CODE like :nodeCodeStart]" +
        "[ :nodeCodes| and c.NODE_CODE in (:nodeCodes)]" +
        "[ :notNodeCode| and c.NODE_CODE <> :notNodeCode]" +
        "[ :notNodeCodes| and c.NODE_CODE not in (:notNodeCodes)]";

    private final static String userGrantorTaskBaseSql = "select a.FLOW_INST_ID, a.FLOW_CODE, a.VERSION, a.FLOW_OPT_NAME," +
        "a.FLOW_OPT_TAG, b.NODE_INST_ID, b.UNIT_CODE,g.GRANTEE as USER_CODE, b.ROLE_TYPE," +
        "b.ROLE_CODE, '权限委托' as AUTH_DESC, c.NODE_CODE, c.NODE_NAME, c.NODE_TYPE," +
        "c.OPT_TYPE as NODE_OPT_TYPE,c.OPT_PARAM, b.CREATE_TIME, b.deadline_time as node_Expire_Time, " +
        "c.OPT_CODE,c.EXPIRE_OPT,c.STAGE_CODE, g.GRANTOR, b.LAST_UPDATE_USER," +
        "b.LAST_UPDATE_TIME,b.NODE_STATE as INST_STATE, c.OPT_ID, a.OPT_ID as MODEL_ID, a.OS_ID, a.USER_CODE as CREATOR_CODE, " +
        "b.warning_time as node_warning_Time, a.warning_time as flow_warning_Time, " +
        "a.deadline_time as flow_Expire_Time, c.Time_Limit as promise_Time,a.create_time as flow_create_time " +
        "from wf_node_instance b join wf_flow_instance a on (a.FLOW_INST_ID = b.FLOW_INST_ID) " +
        "join WF_NODE c on (b.NODE_ID = c.NODE_ID) " +
        "join WF_ROLE_RELEGATE g on ( g.GRANTOR = b.user_code and " +
            " ( (g.opt_id is not null and g.opt_id = a.opt_id) or " + // 按业务委托 或者 按角色委托
              " ((g.unit_code is null or b.unit_code = g.unit_code) and (g.ROLE_CODE is null or g.ROLE_CODE = b.ROLE_CODE)) " +
            ") and g.RELEGATE_TIME < :today and ( g.EXPIRE_TIME is null or g.EXPIRE_TIME> :today ) ) " + // 检验委托时间
        "where b.node_state = 'N' and a.inst_state = 'N' [ :flowInstId| and b.FLOW_INST_ID = :flowInstId]" +
        "[ :(splitforin)flowInstIds| and b.FLOW_INST_ID in (:flowInstIds)]" +
        "[ :flowOptTag| and a.FLOW_OPT_TAG = :flowOptTag]" +
        "[ :stageArr | and c.STAGE_CODE in (:stageArr) ]" +
        "[ :(like)flowOptName| and a.FLOW_OPT_NAME like :flowOptName]" +
        "[ :userCode| and g.GRANTEE = :userCode]" +
        "[ :grantor| and g.GRANTOR = :grantor]" +
        "[ :creatorCode| and a.USER_CODE = :creatorCode]" +
        "[ :topUnit| and a.TOP_UNIT = :topUnit]" +
        "[ :unitCode| and b.UNIT_CODE = :unitCode]" +
        "[ :(DATETIME)beginTime| and b.CREATE_TIME >= :beginTime]" +
        "[ :(DATETIME)endTime| and b.CREATE_TIME <= :endTime]" +
        "[ :osId| and a.OS_ID = :osId]" +
        "[ :optId| and c.OPT_ID = :optId]" +
        "[ :(splitforin)modelId| and a.OPT_ID in (:modelId)] " +
        "[ :optCode| and c.OPT_CODE = :optCode]" +
        "[ :osIds| and a.OS_ID  in (:osIds)]" +
        "[ :nodeInstId| and b.NODE_INST_ID = :nodeInstId]" +
        "[ :flowCode| and a.FLOW_CODE = :flowCode]" +
        "[ :stageCode| and c.STAGE_CODE = :stageCode]" +
        "[ :nodeName| and c.NODE_NAME = :nodeName]" +
        "[ :nodeNames| and c.NODE_NAME in (:nodeNames)]" +
        "[ :(startWith)nodeCodeStart | and c.NODE_CODE like :nodeCodeStart]" +
        "[ :nodeCode| and c.NODE_CODE = :nodeCode]" +
        "[ :nodeCodes| and c.NODE_CODE in (:nodeCodes)]" +
        "[ :notNodeCode| and c.NODE_CODE <> :notNodeCode]" +
        "[ :notNodeCodes| and c.NODE_CODE not in (:notNodeCodes)]";

    private final static String userDynamicTaskSqlPart1 =
        "select a.FLOW_INST_ID, a.FLOW_CODE, a.VERSION, a.FLOW_OPT_NAME," +
            "a.FLOW_OPT_TAG, b.NODE_INST_ID, b.UNIT_CODE,b.USER_CODE, b.ROLE_TYPE," +
            "b.ROLE_CODE, '引擎分配' as AUTH_DESC, c.NODE_CODE, c.NODE_NAME, c.NODE_TYPE," +
            "c.OPT_TYPE as NODE_OPT_TYPE, c.OPT_PARAM, b.CREATE_TIME, b.deadline_time as node_Expire_Time," +
            "c.OPT_CODE, c.EXPIRE_OPT,c.STAGE_CODE,b.GRANTOR,b.LAST_UPDATE_USER," +
            "b.LAST_UPDATE_TIME,b.NODE_STATE as INST_STATE, c.OPT_ID, a.OPT_ID as MODEL_ID, a.OS_ID, " +
            "a.USER_CODE as CREATOR_CODE, b.warning_time as node_warning_Time, a.warning_time as flow_warning_Time, " +
            "a.deadline_time as flow_Expire_Time, c.Time_Limit as promise_Time,a.create_time as flow_create_time " +
            "from wf_node_instance b join wf_flow_instance a on (a.FLOW_INST_ID = b.FLOW_INST_ID) " +
            "join WF_NODE c on (b.NODE_ID = c.NODE_ID) " +
            "where b.node_state = 'N' and a.inst_state = 'N' and b.task_assigned = 'D' " ;
            // " and c.role_type='GW' 目前只有这个

    private final static String userDynamicTaskSqlPart2 =
            "[ :flowInstId| and b.FLOW_INST_ID = :flowInstId]" +
            "[ :(splitforin)flowInstIds| and b.FLOW_INST_ID in ( :flowInstIds )]" +
            "[ :flowOptTag| and a.FLOW_OPT_TAG = :flowOptTag]" +
            "[ :stageArr | and c.STAGE_CODE in (:stageArr) ]" +
            "[ :(like)flowOptName| and a.FLOW_OPT_NAME like :flowOptName]" +
            "[ :unitCode| and ( b.unit_code = :unitCode or b.unit_code is null )] " +
            "[ :(DATETIME)beginTime| and b.CREATE_TIME >= :beginTime]" +
            "[ :(DATETIME)endTime| and b.CREATE_TIME <= :endTime]" +
            "[ :topUnit| and a.TOP_UNIT = :topUnit]" +
            "[ :osId| and a.OS_ID = :osId]" +
            "[ :optId| and c.OPT_ID = :optId]" +
            "[ :(splitforin)modelId| and a.OPT_ID in (:modelId)] " +
            "[ :optCode| and c.OPT_CODE = :optCode]" +
            "[ :osIds| and a.OS_ID  in (:osIds)]" +
            "[ :nodeInstId| and b.NODE_INST_ID = :nodeInstId]" +
            "[ :flowCode| and a.FLOW_CODE = :flowCode]" +
            "[ :stageCode| and c.STAGE_CODE = :stageCode]" +
            "[ :nodeName| and c.NODE_NAME = :nodeName]" +
            "[ :nodeNames| and c.NODE_NAME in (:nodeNames)]" +
            "[ :nodeCode| and c.NODE_CODE = :nodeCode]" +
            "[ :(startWith)nodeCodeStart | and c.NODE_CODE like :nodeCodeStart]" +
            "[ :nodeCodes| and c.NODE_CODE in (:nodeCodes)]" +
            "[ :notNodeCode| and c.NODE_CODE <> :notNodeCode]" +
            "[ :notNodeCodes| and c.NODE_CODE not in (:notNodeCodes)]";

    /**
     * 根据用户编码获取用户已办任务列表
     *
     * @param filter 过滤条件
     * @param pageDesc 分页信息
     * @return 用户待办
     */
    @Transactional
    public List<UserTask> listUserCompletedTask(Map<String, Object> filter, PageDesc pageDesc) {
        String sql;//= userCompleteTaskBaseSql;
        DBType dbType = DatabaseOptUtils.doGetDBType(this);
        switch (dbType){
            case Oracle:
                sql = userCompleteTaskBaseSqlPart1 + " (" + flowInstStateSqlOracle12c + ") " + userCompleteTaskBaseSqlPart2;
                break;
            case Oscar:
            case DM:
                sql = userCompleteTaskBaseSqlPart1 + " (" + flowInstStateSqlChinese + ") " + userCompleteTaskBaseSqlPart2;
                break;
            case MySql:
                sql = userCompleteTaskBaseSqlPart1 + " (" + flowInstStateSqlMySql + ") " + userCompleteTaskBaseSqlPart2;
                break;
            case PostgreSql:
                sql = userCompleteTaskBaseSqlPart1 + " (" + flowInstStateSqlPG + ") " + userCompleteTaskBaseSqlPart2;
                break;
            default:
                sql = userCompleteTaskBaseSqlPart1 + " V_FLOW_INST_STATE " + userCompleteTaskBaseSqlPart2;
                break;
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

    private Pair<String, SimpleTableField> findField(TableMapInfo tabA, TableMapInfo tabB, TableMapInfo tabC,
                                                     String fieldName){
        SimpleTableField field = tabB.findFieldByName(fieldName);
        if(field!=null)
            return new ImmutablePair<>("b.", field);
        field = tabA.findFieldByName(fieldName);
        if(field!=null)
            return new ImmutablePair<>("a.", field);
        field = tabC.findFieldByName(fieldName);
        if(field!=null)
            return new ImmutablePair<>("c.", field);
        return null;
    }

    private String buildSortSql(Map<String, Object> filterMap, boolean hasAlias){
        String selfOrderBy = StringBaseOpt.objectToString(filterMap.get(GeneralJsonObjectDao.SELF_ORDER_BY));
        if(StringUtils.isBlank(selfOrderBy)){
            StringBaseOpt.objectToString(filterMap.get("orderBy"));
        }
        // wf_node_instance b   wf_flow_instance a o join WF_NODE c
        TableMapInfo tabA = JpaMetadata.fetchTableMapInfo(FlowInstance.class);
        TableMapInfo tabB = JpaMetadata.fetchTableMapInfo(NodeInstance.class);
        TableMapInfo tabC = JpaMetadata.fetchTableMapInfo(NodeInfo.class);

        if(StringUtils.isNotBlank(selfOrderBy)){
            StringBuilder orderSql = new StringBuilder();
            Lexer lexer = new Lexer(selfOrderBy, Lexer.LANG_TYPE_SQL);
            String aword = lexer.getAWord();
            while(StringUtils.isNotBlank(aword)){
                Pair<String, SimpleTableField> fieldPair = findField(tabA, tabB, tabC, aword);
                if(fieldPair!=null){
                    if(orderSql.length()>0){
                        orderSql.append(", ");
                    }
                    if(hasAlias)
                        orderSql.append(fieldPair.getLeft());
                    orderSql.append(fieldPair.getRight().getColumnName());
                }
                aword = lexer.getAWord();
                if(StringUtils.equalsAnyIgnoreCase(aword, "desc", "asc", "nulls", "first", "last")){
                    if(fieldPair!=null){
                        orderSql.append(" ").append(aword);
                    }
                    aword = lexer.getAWord();
                }
                if(!",".equals(aword)){
                    break;
                }
            }

            if(orderSql.length()>0){
                return orderSql.toString();
            }
        }

        String sortField = StringBaseOpt.objectToString(filterMap.get(GeneralJsonObjectDao.TABLE_SORT_FIELD));
        Pair<String, SimpleTableField> fieldPair = findField(tabA, tabB, tabC, sortField);
        if(fieldPair!=null){
            String sf= hasAlias? fieldPair.getLeft() + fieldPair.getRight().getColumnName()
                : fieldPair.getRight().getColumnName();

            String orderField = StringBaseOpt.objectToString(filterMap.get(GeneralJsonObjectDao.TABLE_SORT_ORDER));
            if(StringUtils.equalsAnyIgnoreCase(orderField, "desc", "asc", "nulls", "first", "last")){
                return sf + " " +orderField;
            }
            return sf;
        }
        return hasAlias? "b.CREATE_TIME desc" : "CREATE_TIME desc";
    }

    @Transactional
    public List<UserTask> listUserStaticTask(Map<String, Object> filter, PageDesc pageDesc) {

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(
            userStaticTaskBaseSql + " order by " + buildSortSql(filter, true), filter);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(), queryAndNamedParams.getParams(), pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

    @Transactional
    public List<UserTask> listUserGrantorTask(Map<String, Object> filter, PageDesc pageDesc) {

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(
            userGrantorTaskBaseSql + " order by " + buildSortSql(filter, true), filter);

        queryAndNamedParams.getParams().put("today", DatetimeOpt.currentUtilDate());
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
        grantorQuery.getParams().put("today", DatetimeOpt.currentUtilDate());

        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            "select * from (" + staticSql +" union all " + grantorSql +") t order by " +
                buildSortSql(filter, false),  null,
            "select sum(t.rowcounts) as rowcounts from (" + staticCountSql +" union all " + grantorCountSql +") t",
            grantorQuery.getParams(), pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

    public static String buildDynamicTaskSql(List<UserUnit> userUnits){
        StringBuilder sqlBuilder = new StringBuilder(3072);
        int uuCount = userUnits.size();
        sqlBuilder.append(userDynamicTaskSqlPart1);
        if(uuCount == 1) {
            sqlBuilder.append(" and (b.unit_code is null or b.unit_code =:userUnitCode0) and b.role_code = :userStation0");
        } else if(uuCount > 1) {
            sqlBuilder.append(" and (");
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

    public static void appendDynamicQueryParams( Map<String, Object> queryMap, List<UserUnit> userUnits){
        int i=0;
        for(UserUnit uu : userUnits){
            queryMap.put("userUnitCode"+i, uu.getUnitCode());
            queryMap.put("userStation"+i, uu.getUserStation());
            i++;
        }
    }

    @Transactional
    public List<UserTask> listUserDynamicTask(List<UserUnit> userUnits, Map<String, Object> filter, PageDesc pageDesc) {
        String querySql = buildDynamicTaskSql(userUnits);
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(
            querySql + " order by " + buildSortSql(filter, true), filter);
        Map<String, Object> queryParamMap = CollectionsOpt.unionTwoMap(queryAndNamedParams.getParams(), filter);
        appendDynamicQueryParams(queryParamMap, userUnits);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            queryAndNamedParams.getQuery(), queryParamMap, pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

    @Transactional
    public List<UserTask> listUserAllTask(List<UserUnit> userUnits, Map<String, Object> filter, PageDesc pageDesc) {

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
        queryParamMap.put("today", DatetimeOpt.currentUtilDate());

        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
            "select * from (" + staticSql +" union all " + grantorSql +" union all " + dynamicSql + ") t order by "
             + buildSortSql(filter, false) , null,
            "select sum(t.rowcounts) as rowcounts from (" + staticCountSql +" union all " + grantorCountSql +" union all " + dynamicCountSql +") t",
            queryParamMap, pageDesc);

        return dataList == null ? null : dataList.toJavaList(UserTask.class);
    }

}
