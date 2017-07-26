package com.centit.workflow.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowTransition;
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
	public List<FlowTransition> getNodeTrans(long nodeID){
		String baseHQL = "from FlowTransition where startNodeId = "
			+ nodeID ;
		return  this.listObjects(baseHQL);
	}

	@Transactional(propagation= Propagation.MANDATORY)
    public List<FlowTransition> getNodeInputTrans(long nodeID){
        String baseHQL = "from FlowTransition where endNodeId = "
            + nodeID ;
        return  this.listObjects(baseHQL);
    }
}
