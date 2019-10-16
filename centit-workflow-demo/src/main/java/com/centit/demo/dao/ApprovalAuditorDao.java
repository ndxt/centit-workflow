package com.centit.demo.dao;


import com.centit.demo.po.ApprovalAuditor;
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
public class ApprovalAuditorDao extends BaseDaoImpl<ApprovalAuditor,Long> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public long getNextApprovalAuditorId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_APPROVALAUDITOR");
    }

    @Override
    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(ApprovalAuditor o) {
        if(o.getAuditorId() == null || o.getAuditorId() == 0){
            o.setAuditorId(getNextApprovalAuditorId());
        }
        super.saveNewObject(o);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<ApprovalAuditor> getAuditorsByPhaseNo(String phaseNo){
        return this.listObjectsByFilter("where phase_No = ?",new Object[]{phaseNo});
    }
}
