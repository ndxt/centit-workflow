package com.centit.demo.dao;


import com.centit.demo.po.ApprovalEvent;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Repository
public class ApprovalEventDao extends BaseDaoImpl<ApprovalEvent,Long> {

    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    @Transactional
    public long getNextApprovalEventId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_APPROVALEVENT");
    }

    @Transactional
    public void saveNewObject(ApprovalEvent o) {
        if(o.getApprovalId() == null || o.getApprovalId() == 0){
            o.setApprovalId(getNextApprovalEventId());
        }
        super.saveNewObject(o);
    }

    @Transactional
    public List<ApprovalEvent> getApprovalEventByFlowInstId(String flowInstId){
        return this.listObjectsByFilter("where flow_Inst_Id = ?",new Object[]{flowInstId});
    }
}
