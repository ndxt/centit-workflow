package com.centit.workflow.dao;

import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.workflow.po.ApprovalAuditor;
import com.centit.workflow.po.ApprovalEvent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Repository
public class ApprovalAuditorDao extends BaseDaoImpl<ApprovalAuditor,Long> {
    @Transactional(propagation= Propagation.MANDATORY)
    private long getNextApprovalAuditorId(){
        String sNo = DatabaseOptUtils.getNextValueOfSequence(this,"S_APPROVALAUDITOR");
        return Long.valueOf(sNo);
    }

    @Override
    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(ApprovalAuditor o) {
        if(o.getAuditorId() == null || o.getAuditorId() == 0){
            o.setAuditorId(getNextApprovalAuditorId());
        }
        super.saveNewObject(o);
    }
}
