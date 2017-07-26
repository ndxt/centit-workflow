package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.FlowVariableId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowVariableDao extends BaseDaoImpl<FlowVariable,FlowVariableId>
{
		//public static final Logger logger = LoggerFactory.getLogger(WfFlowVariableDao.class);
		
	public Map<String, String> getFilterField() {
		if( filterField == null){
			filterField = new HashMap<String, String>();

			filterField.put("flowInstId" , CodeBook.EQUAL_HQL_ID);

			filterField.put("runToken" , CodeBook.EQUAL_HQL_ID);

			filterField.put("varName" , CodeBook.EQUAL_HQL_ID);

			filterField.put("varValue" , CodeBook.LIKE_HQL_ID);

			filterField.put("varType" , CodeBook.LIKE_HQL_ID);

		}
		return filterField;
	}

	@Transactional(propagation= Propagation.MANDATORY)
	public List<FlowVariable> listFlowVariables(long flowInstId)
	{
	    return this.listObjects("From FlowVariable where cid.flowInstId=? order by cid.runToken",flowInstId);
	}

	@Transactional(propagation= Propagation.MANDATORY)
	public List<FlowVariable> viewFlowVariablesByVarname(long flowInstId,
                                                         String varname) {
		return this
				.listObjects(
						"From FlowVariable where cid.flowInstId=? and cid.varName=? order by cid.runToken",
						new Object[] { flowInstId, varname });
	}
}
