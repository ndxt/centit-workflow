package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.workflow.po.ManageActionLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ManageActionDao extends BaseDaoImpl<ManageActionLog,Long>
	{
	public Map<String, String> getFilterField() {
		if( filterField == null){
			filterField = new HashMap<String, String>();

			filterField.put("actionId" , CodeBook.EQUAL_HQL_ID);


			filterField.put("flowInstId" , CodeBook.LIKE_HQL_ID);

			filterField.put("actionType" , CodeBook.LIKE_HQL_ID);

			filterField.put("actionTime" , CodeBook.LIKE_HQL_ID);

			filterField.put("userCode" , CodeBook.LIKE_HQL_ID);

			filterField.put("roleType" , CodeBook.LIKE_HQL_ID);

			filterField.put("roleCode" , CodeBook.LIKE_HQL_ID);

			filterField.put("adminDesc" , CodeBook.LIKE_HQL_ID);

		}
		return filterField;
	} 
	
	/**
	 * 生成主键编号
	 * @return
	 */
	@Transactional(propagation= Propagation.MANDATORY)
	public long getNextManageId(){
		return DatabaseOptUtils.getSequenceNextValue(this,"S_MANAGERACTIONNO");
	}
	
	/**
	 * 查询流程管理日志信息
	 * @param wfinstid
	 * @param pageDesc 
	 * @return
	 */
	@Transactional(propagation= Propagation.MANDATORY)
    public List<ManageActionLog> getFlowManageLogs(long wfinstid, PageDesc pageDesc) {
        return this.listObjectsByFilter("where flow_Inst_Id = ? order by action_Time desc",
				new Object[]{wfinstid},pageDesc);
    }
}
