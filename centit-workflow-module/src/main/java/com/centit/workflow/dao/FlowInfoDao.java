package com.centit.workflow.dao;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.po.FlowInfoId;
import com.centit.workflow.po.LastVersionFlowDefine;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowInfoDao extends BaseDaoImpl<FlowInfo,FlowInfoId> {
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
		String sql = "select max(t.VERSION) from WF_FLOW_DEFINE t where t.FLOW_CODE = ?";
		return this.getJdbcTemplate().queryForObject(sql,
				new Object[]{flowCode} ,Long.class);
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public long getNextNodeId(){
	    return DatabaseOptUtils.getSequenceNextValue(this,"S_FLOWDEFNO");
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public long getNextTransId(){
		return DatabaseOptUtils.getSequenceNextValue(this,"S_FLOWDEFNO");
	}
	@Transactional(propagation= Propagation.MANDATORY)
    public long getNextStageId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_FLOWDEFNO");
    }
    
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional(propagation= Propagation.MANDATORY)
	public List<FlowInfo> getAllLastVertionFlows(Map<String,Object> filterMap){
		
		String sql =  "SELECT * FROM F_V_LASTVERSIONFLOW WHERE 1=1 " ;
		QueryAndNamedParams sqlAndParams = QueryUtils.translateQuery(sql,filterMap);
		return this.listObjectsBySql(sqlAndParams.getQuery(),sqlAndParams.getParams());
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public List<FlowInfo> getAllVersionFlowsByCode(String wfCode, PageDesc pageDesc){
		return this.listObjectsByFilter("where FLOW_CODE = ? order by version desc",
				new Object[]{wfCode},pageDesc);
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
		return  this.getJdbcTemplate().queryForList(sql,
				new Object[]{wfstate} ,FlowInfo.class);
	}
	@Transactional(propagation= Propagation.MANDATORY)
	public String getNextPrimarykey() {
        return String.valueOf(DatabaseOptUtils.getSequenceNextValue(this,"S_FLOWDEFINE"));
    }

    @SuppressWarnings("deprecation")
	@Transactional(propagation= Propagation.MANDATORY)
    public List<FlowInfo> getAllLastVertionFlows(
            Map<String, Object> filterMap, PageDesc pageDesc) {
        
        String sql =  "select VERSION,FLOW_CODE,FLOW_NAME,FLOW_CLASS,FLOW_STATE,FLOW_DESC,FLOW_XML_DESC," +
				"FLOW_PUBLISH_DATE,OPT_ID,TIME_LIMIT " +
				" from F_V_LASTVERSIONFLOW" ;
		QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql,filterMap);
		JSONArray dataList = DatabaseOptUtils.listObjectsBySqlAsJson(this,
				queryAndNamedParams.getQuery(),queryAndNamedParams.getParams(),pageDesc);
		List<LastVersionFlowDefine> ls = new ArrayList<>();
		if(dataList != null) {
			ls = JSONArray.parseArray(dataList.toJSONString(),LastVersionFlowDefine.class);
		}
		//List<UserTask>  userTasks = (List<UserTask>)DatabaseOptUtils.findObjectsBySql(this,queryAndNamedParams.getSql(),queryAndNamedParams.getParams(),pageDesc,UserTask.class);

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
