package com.centit.demo.dao;


import com.centit.demo.po.ApprovalProcess;
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
public class ApprovalProcessDao extends BaseDaoImpl<ApprovalProcess,Long> {

    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public long getNextApprovalProcessId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_APPROVALPROCESS");
    }

    @Override
    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(ApprovalProcess o) {
        if(o.getProcessId() == null || o.getProcessId() == 0){
            o.setProcessId(getNextApprovalProcessId());
        }
        super.saveNewObject(o);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<ApprovalProcess> getApprovalProcessByNodeInstId(String nodeInstId){
        return this.listObjectsByFilter("where node_Inst_Id = ?",new Object[]{nodeInstId});
    }
}
