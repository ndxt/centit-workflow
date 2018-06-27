package com.centit.workflow.dao;

import com.centit.workflow.po.UserTask;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class PlatformFlowDao {
    @Resource
    private FlowInfoDao flowInfoDao;


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

    @Transactional(propagation = Propagation.MANDATORY)
    public List<UserTask> queryDynamicTask(String unitCode,String userStation,String userRank){
        String sql = "select *,c.opt_code as optUrl from " +
            "wf_node_instance a " +
            "left join wf_flow_instance w " +
            "on a.flow_inst_id = w.flow_inst_id " +
            "left join wf_node c " +
            "on a.node_Id=c.node_id " +
            "where a.node_state='N' and w.inst_state='N' and a.task_assigned='D' and " +
            "(a.unit_code is null or a.unit_code=?) and " +
            "((c.role_type='gw' and c.role_code=?) or (c.role_type='xz' and c.role_code=?))";//TODO 字段按需补全。动态任务暂时用不到，没测试过
        return flowInfoDao.getJdbcTemplate().query(sql,new Object[]{unitCode,userStation,userRank},new BeanPropertyRowMapper(UserTask.class));
    }
}
