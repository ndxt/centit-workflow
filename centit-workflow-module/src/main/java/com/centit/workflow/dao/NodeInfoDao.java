package com.centit.workflow.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.workflow.po.NodeInfo;
@Repository
public class NodeInfoDao extends BaseDaoImpl<NodeInfo,Long>
	{
	public Map<String, String> getFilterField() {
		if( filterField == null){
			filterField = new HashMap<String, String>();

			filterField.put("nodeId" , CodeBook.EQUAL_HQL_ID);

			filterField.put("flowCode" , CodeBook.EQUAL_HQL_ID);

			filterField.put("version" , CodeBook.EQUAL_HQL_ID);

			filterField.put("nodeType" , CodeBook.EQUAL_HQL_ID);

			filterField.put("nodeName" , CodeBook.LIKE_HQL_ID);

			filterField.put("optType" , CodeBook.EQUAL_HQL_ID);

			filterField.put("optCode" , CodeBook.EQUAL_HQL_ID);

			filterField.put("subFlowCode" , CodeBook.EQUAL_HQL_ID);
			filterField.put("nodeCode" , CodeBook.EQUAL_HQL_ID);
	        
			filterField.put("nodeDesc" , CodeBook.LIKE_HQL_ID);

			filterField.put("roleType" , CodeBook.EQUAL_HQL_ID);

			filterField.put("roleCode" , CodeBook.EQUAL_HQL_ID);

		}
		//this.getNextValueOfSequence(sequenceName)
		return filterField;
	}

	@Transactional(propagation= Propagation.MANDATORY)
	public Set<String> getUnitExp(String flowCode, Long version) {
	    String hql = " from NodeInfo where unitExp is not null";
	    Map<String, Object> paramMap = new HashMap<String, Object>();
	    paramMap.put("flowCode", flowCode);
	    paramMap.put("version", version);
	    
	    List<NodeInfo> nodeList = listObjects(hql, paramMap);
	    
	    Set<String> unitExpSet = new HashSet<String>();
	    
	    for (NodeInfo node : nodeList) {
	        if (StringUtils.isNotBlank(node.getUnitExp())) {
	            unitExpSet.add(node.getUnitExp());
	        }
	    }
	    
	    return unitExpSet;
	}

	@Transactional(propagation= Propagation.MANDATORY)
	public List<NodeInfo> listNodeByNodecode(String flowCode, Long version, String nodeCode) {
        String hql = "from NodeInfo where flowDefine.cid.flowCode=? and flowDefine.cid.version=? and nodeCode=?";
        List<NodeInfo> nodeList = listObjects(hql, new Object[]{flowCode,  version,  nodeCode});
        return nodeList;
    }
}
