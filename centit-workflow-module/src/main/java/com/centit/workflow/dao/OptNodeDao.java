package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.workflow.po.OptNode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017-10-9.
 */
@Repository
public class OptNodeDao extends BaseDaoImpl<OptNode,Long>{
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public String getNextOptNodeId(){
        return String.valueOf(DatabaseOptUtils.getSequenceNextValue(this,"S_OPTNODE"));
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(OptNode o) {
        if(o.getOptNodeId() == null || "".equals(o.getOptNodeId())){
            o.setOptNodeId(getNextOptNodeId());
        }
        super.saveNewObject(o);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<OptNode> getOptNodeByOptId(String optId){
        return this.listObjectsByFilter("where opt_id = ?",new Object[]{optId});
    }
}
