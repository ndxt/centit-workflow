package com.centit.workflow.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.QueryAndNamedParams;
import com.centit.workflow.po.UserTask;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.support.database.QueryAndParams;
import com.centit.support.database.QueryUtils;
import com.centit.workflow.po.LastVersionFlowDefine;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.po.FlowInfoId;

@Repository
public class FlowInfoDao extends BaseDaoImpl<FlowInfo,FlowInfoId>
	{
	public Map<String, String> getFilterField() {
		if( filterField == null){
			filterField = new HashMap<String, String>();

			filterField.put("flowCode" , "cid.flowCode= :flowCode");

			filterField.put("version" , "cid.version= :version");

			filterField.put("flowName" , CodeBook.LIKE_HQL_ID);

			filterField.put("flowState" , CodeBook.LIKE_HQL_ID);

			filterField.put("flowDesc" , CodeBook.LIKE_HQL_ID);
			filterField.put("optId" , CodeBook.EQUAL_HQL_ID);

			filterField.put(CodeBook.ORDER_BY_HQL_ID , "cid.version DESC,flowPublishDate DESC,cid.flowCode DESC ");

		}
		return filterField;
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public long getLastVersion(String flowCode){
		String  hql;
		//"SELECT max(cast(version as long)) FROM WfFlowDefine WHERE WFCODE = "
		hql =  "SELECT max(cast(cid.version as long)) FROM FlowInfo WHERE cid.flowCode = ?";
		return DatabaseOptUtils.getSingleIntByHql(this,hql,flowCode);
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public long getNextNodeId(){
	    return DatabaseOptUtils.getNextLongSequence(this,"S_FLOWDEFNO");
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public long getNextTransId(){
		return DatabaseOptUtils.getNextLongSequence(this,"S_FLOWDEFNO");
	}
	@Transactional(propagation= Propagation.MANDATORY)
    public long getNextStageId(){
        return DatabaseOptUtils.getNextLongSequence(this,"S_FLOWDEFNO");
    }
    
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional(propagation= Propagation.MANDATORY)
	public List<FlowInfo> getAllLastVertionFlows(Map<String,Object> filterMap){
		
		String sql =  "SELECT * FROM F_V_LASTVERSIONFLOW WHERE 1=1 " ;
		QueryAndNamedParams sqlAndParams = QueryUtils.translateQuery(sql,filterMap);
		return (List<FlowInfo>)DatabaseOptUtils.findObjectsByHql(this,sqlAndParams.getHql(),sqlAndParams.getParams());
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public List<FlowInfo> getAllVersionFlowsByCode(String wfCode, PageDesc pageDesc){
		String  hql =  "from FlowInfo where cid.flowCode = ? order by version desc";
		return this.listObjects(hql, wfCode ,pageDesc);
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public FlowInfo getLastVersionFlowByCode(String flowCode){
		long lVer = getLastVersion(flowCode);
		return this.getObjectById(new FlowInfoId(lVer, flowCode));
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public FlowInfo getFlowDefineByID(String flowCode, Long version)
	{
        return this.getObjectById(new FlowInfoId(version, flowCode));
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation= Propagation.MANDATORY)
	public List<FlowInfo> getFlowsByState(String wfstate)
	{
		String sql="SELECT * FROM F_V_LASTVERSIONFLOW WHERE FLOW_STATE = ? ORDER BY VERSION";
		
		return (List<FlowInfo>)DatabaseOptUtils.findObjectsBySql(this,sql,
				new Object[] {wfstate},FlowInfo.class);
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public String getNextPrimarykey() {
        return DatabaseOptUtils.getNextKeyBySequence(this,"S_FLOWDEFINE",6);
    }

    @SuppressWarnings("deprecation")
	@Transactional(propagation= Propagation.MANDATORY)
    public List<FlowInfo> getAllLastVertionFlows(
            Map<String, Object> filterMap, PageDesc pageDesc) {
        
        String sql =  "FROM LastVersionFlowDefine WHERE 1=1" ;
		QueryAndNamedParams sqlAndParams = QueryUtils.translateQuery(sql,filterMap);
		List<LastVersionFlowDefine> ls = (List<LastVersionFlowDefine>)DatabaseOptUtils.findObjectsByHql(this,sqlAndParams.getHql(),sqlAndParams.getParams(),pageDesc);

		@SuppressWarnings("unchecked")
        List<FlowInfo>all=new ArrayList<FlowInfo>();
        if(ls != null && ls.size() > 0) {
			for (LastVersionFlowDefine s : ls) {
				all.add(s.toWfFlowDefine());
			}
		}
        return all;
    }

}
