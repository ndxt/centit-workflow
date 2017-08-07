package com.centit.workflow.dao;

import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.workflow.po.ApprovalProcess;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Repository
public class ApprovalProcessDao extends BaseDaoImpl<ApprovalProcess,Long> {
    @Transactional(propagation= Propagation.MANDATORY)
    private long getNextApprovalProcessId(){
        String sNo = DatabaseOptUtils.getNextValueOfSequence(this,"S_APPROVALPROCESS");
        return Long.valueOf(sNo);
    }

    @Override
    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(ApprovalProcess o) {
        if(o.getProcessId() == null || o.getProcessId() == 0){
            o.setProcessId(getNextApprovalProcessId());
        }
        super.saveNewObject(o);
    }
}
