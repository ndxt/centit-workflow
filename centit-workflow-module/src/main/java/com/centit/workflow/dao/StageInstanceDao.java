package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.workflow.po.StageInstance;
import com.centit.workflow.po.StageInstanceId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StageInstanceDao extends BaseDaoImpl<StageInstance,StageInstanceId> {
    //public static final Logger logger = LoggerFactory.getLogger(WfStageInstanceDao.class);

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();

            filterField.put("flowInstId", "cid.flowInstId = ?");

            filterField.put("stageId", "cid.stageId = ?");

            filterField.put("promiseTime", CodeBook.LIKE_HQL_ID);

            filterField.put("timeLimit", CodeBook.LIKE_HQL_ID);

            
        }
        return filterField;
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public List<StageInstance> listStageInstByFlowInstId(long flowInstId) {
        
        return this.listObjects("from StageInstance where id.flowInstId=?", flowInstId);
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public StageInstance getObject(long flowInstId, long stageId) {
        return this.getObjectById(new StageInstanceId(flowInstId,stageId));
    }
}
