package com.centit.workflow.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.workflow.po.ManageActionLog;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
		String sNo = DatabaseOptUtils.getNextValueOfSequence(this,"S_MANAGERACTIONNO");
		return Long.valueOf(sNo);
	}
	
	/**
	 * 查询流程管理日志信息
	 * @param wfinstid
	 * @param pageDesc 
	 * @return
	 */
	@Transactional(propagation= Propagation.MANDATORY)
    public List<ManageActionLog> getFlowManageLogs(long wfinstid, PageDesc pageDesc) {
        String hql = "from ManageActionLog where flowInstId = ?"
                + " order by actionTime desc";
        return this.listObjects(hql,wfinstid,pageDesc);
    }
}
