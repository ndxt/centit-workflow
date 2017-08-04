package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.workflow.po.ApprovalEvent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Repository
public class ApprovalEventDao extends BaseDaoImpl<ApprovalEvent,Long> {
    @Transactional(propagation= Propagation.MANDATORY)
    private long getNextApprovalEventId(){
        String sNo = DatabaseOptUtils.getNextValueOfSequence(this,"S_APPROVALEVENT");
        return Long.valueOf(sNo);
    }

    @Override
    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(ApprovalEvent o) {
        if(o.getApprovalId() == null || o.getApprovalId() == 0){
            o.setApprovalId(getNextApprovalEventId());
        }
        super.saveNewObject(o);
    }
}
