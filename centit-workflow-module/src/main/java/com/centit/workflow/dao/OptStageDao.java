package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.workflow.po.OptStage;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017-10-9.
 */
@Repository
public class OptStageDao extends BaseDaoImpl<OptStage,Long> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public String getNextOptStageId(){
        return String.valueOf( DatabaseOptUtils.getSequenceNextValue(this,"S_OPTSTAGE"));
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(OptStage o) {
        if(o.getOptStageId() == null || "".equals(o.getOptStageId())){
            o.setOptStageId(getNextOptStageId());
        }
        super.saveNewObject(o);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<OptStage> getOptNodeByOptId(String optId){
        return this.listObjectsByFilter("where opt_id = ?",new Object[]{optId});
    }
}
