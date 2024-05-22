package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.workflow.po.StageInstance;
import com.centit.workflow.po.StageInstanceId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StageInstanceDao extends BaseDaoImpl<StageInstance,StageInstanceId> {
    //public static final Logger logger = LoggerFactory.getLogger(WfStageInstanceDao.class);

    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<String, String>();

        filterField.put("flowInstId", "flowInstId = :flowInstId");
        filterField.put("stageId", "stageId = :stageId");
        filterField.put("promiseTime", CodeBook.LIKE_HQL_ID);
        filterField.put("timeLimit", CodeBook.LIKE_HQL_ID);
        return filterField;
    }
    @Transactional
    public List<StageInstance> listStageInstByFlowInstId(String flowInstId) {
        return this.listObjectsByFilter("where flow_Inst_Id=?",new Object[]{flowInstId});
    }
    @Transactional
    public StageInstance getObject(String flowInstId, String stageId) {
        return this.getObjectById(new StageInstanceId(flowInstId,stageId));
    }

    @Transactional
    public StageInstance getStageInstanceByCode(String flowInstId, String stageCode) {
        List<StageInstance> stageInstances = listObjectsByFilter("where flow_Inst_Id=? and stage_code=?", new Object[]{flowInstId, stageCode});
        if (null != stageInstances && stageInstances.size() > 0) {
            return stageInstances.get(0);
        }
        return null;
    }

    @Transactional
    public List<StageInstance> listExpireStageInstance() {
        return this.listObjectsByFilter(" join WF_FLOW_INSTANCE b on (a.FLOW_INST_ID=b.FLOW_INST_ID)" +
                " where b.inst_State='N' and a.deadline_time < ? and (a.TIMER_STATUS='T' or a.TIMER_STATUS='W') ",
            new Object[]{DatetimeOpt.currentUtilDate()}, "a");
    }

    @Transactional
    public List<StageInstance> listWarningStageInstance() {
        return this.listObjectsByFilter(" join WF_FLOW_INSTANCE b on (a.FLOW_INST_ID=b.FLOW_INST_ID)" +
                " where b.inst_State='N' and a.warning_time < ? and a.TIMER_STATUS='T'",
            new Object[]{DatetimeOpt.currentUtilDate()}, "a");
    }

    @Transactional
    public void updtStageTimerStatus(String flowInstId, String stageId, String state) {
        String sql = "update WF_STAGE_INSTANCE set TIMER_STATUS = ? where FLOW_INST_ID = ? and STAGE_ID = ?";
        DatabaseOptUtils.doExecuteSql(this, sql, new Object[]{ state, flowInstId, stageId});
    }
}
