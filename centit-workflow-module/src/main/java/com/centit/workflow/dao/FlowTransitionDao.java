package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowTransition;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public class FlowTransitionDao extends BaseDaoImpl<FlowTransition,Long>
    {
     public Map<String, String> getFilterField() {
        if( filterField == null){
            filterField = new HashMap<String, String>();

            filterField.put("transId" , CodeBook.EQUAL_HQL_ID);


            filterField.put("flowId" , CodeBook.LIKE_HQL_ID);

            filterField.put("version" , CodeBook.LIKE_HQL_ID);

            filterField.put("transClass" , CodeBook.LIKE_HQL_ID);

            filterField.put("transName" , CodeBook.LIKE_HQL_ID);

            filterField.put("transDesc" , CodeBook.LIKE_HQL_ID);

            filterField.put("startNodeId" , CodeBook.LIKE_HQL_ID);

            filterField.put("endNodeId" , CodeBook.LIKE_HQL_ID);

            filterField.put("transCondition" , CodeBook.LIKE_HQL_ID);

            filterField.put("routerPos" , CodeBook.LIKE_HQL_ID);

        }
        return filterField;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowTransition> getNodeTrans(String nodeID){
        return this.listObjectsByFilter("where start_Node_Id = ?",new Object[]{nodeID});
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowTransition> getNodeInputTrans(String nodeID){
        return this.listObjectsByFilter("where end_Node_Id = ?",new Object[]{nodeID});
    }
}
