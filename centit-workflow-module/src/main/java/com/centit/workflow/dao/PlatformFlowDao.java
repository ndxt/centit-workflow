package com.centit.workflow.dao;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.UserTask;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class PlatformFlowDao {
    @Resource
    private FlowInfoDao flowInfoDao;

    private final static String dynamicSql = "select w.flow_inst_id,w.flow_code,w.version,w.flow_opt_name,w.flow_opt_tag," +
        "  a.node_inst_id,a.unit_code,a.user_code, " +
        " c.node_name,c.node_type,c.opt_type as NODE_OPT_TYPE,c.opt_param,"+
        " w.create_time,w.promise_time,a.time_limit,c.opt_code, " +
        " c.expire_opt,c.stage_code,'' as GRANTOR,a.last_update_user," +
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
        "[:(like) flowOptName| and w.FLOW_OPT_NAME like :flowOptName] " +
        "[ :unitCode| and ( a.unit_code = :unitCode or a.unit_code is null )] " +
        "[ :userStation| and c.role_code = :userStation] " +
        "[ :stageCode| and c.STAGE_CODE = :stageCode] "+
        "[ :flowCode| and w.FLOW_CODE = :flowCode] " +
        "[ :nodeCode| and a.NODE_CODE = :nodeCode] " +
        "[ :nodeInstId| and a.node_inst_id = :nodeInstId] " +
        " ORDER by a.create_time desc";
    @Transactional(propagation = Propagation.MANDATORY)
    public List<UserTask> queryStaticTask(String userCode){
        String sql = "select t.flow_inst_id flowInstId," +
            "t.node_Inst_Id nodeInstId," +
            "t.flow_opt_name flowOptName," +
            "t.flow_opt_tag flowOptTag," +
            "t.user_Code userCode," +
            "t.unit_Code unitCode," +
            "t.opt_param opt_param " +
            "from v_user_task_list t where t.user_code = ?";//TODO 字段按需补全
        return flowInfoDao.getJdbcTemplate().query(sql,new Object[]{userCode},new BeanPropertyRowMapper(UserTask.class));
    }

    public List<UserTask> queryDynamicTask(Map<String, Object> searchColumn, PageDesc pageDesc){
       /* String sql = "select w.flow_inst_id,w.flow_code,w.version,w.flow_opt_name,w.flow_opt_tag," +
            "  a.node_inst_id,a.unit_code,a.user_code, " +
            " c.node_name,c.node_type,c.opt_type as NODE_OPT_TYPE,c.opt_param,"+
            " w.create_time,w.promise_time,a.time_limit,c.opt_code, " +
            " c.expire_opt,c.stage_code,'' as GRANTOR,a.last_update_user," +
            " a.last_update_time,w.inst_state,c.opt_code as opt_url "+
        "from wf_node_instance a " +
            "left join wf_flow_instance w " +
            " on a.flow_inst_id = w.flow_inst_id " +
            "left join wf_node c " +
            " on a.node_Id = c.node_id " +
            "where a.node_state = 'N' " +
            " and w.inst_state = 'N' " +
            " and a.task_assigned = 'D' " +
            " and (a.unit_code is null or a.unit_code=?)" +
            " and (c.role_type='gw' and c.role_code=?)" +
            " ORDER by a.create_time desc";*/
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(dynamicSql,searchColumn);

        JSONArray jsonArray = DatabaseOptUtils.listObjectsBySqlAsJson(flowInfoDao,queryAndNamedParams.getQuery(),
            queryAndNamedParams.getParams(),pageDesc);
        return jsonArray == null?null:jsonArray.toJavaList(UserTask.class);
       /* String sql = "select a.*,w.*,c.*,c.opt_code as optUrl from " +
            "wf_node_instance a " +
            "left join wf_flow_instance w " +
            "on a.flow_inst_id = w.flow_inst_id " +
            "left join wf_node c " +
            "on a.node_Id=c.node_id " +
            "where a.node_state='N' and w.inst_state='N' and a.task_assigned='D' and " +
            "(a.unit_code is null or a.unit_code=?) and " +
            "(c.role_type='gw' and c.role_code=?)" +
            " ORDER by a.create_time desc ";*/
        //return flowInfoDao.getJdbcTemplate().query(sql,new Object[]{unitCode,userStation},new BeanPropertyRowMapper(UserTask.class));
    }
}
