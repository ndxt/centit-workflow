package com.centit.workflow.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.workflow.po.ActionLog;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 流程日志操作类
 * 
 * @author ljy, codefan
 * @version $Rev$ <br>
 *          $Id$
 */
@Repository
public class ActionLogDao extends BaseDaoImpl<ActionLog,Long>
	{

    public Map<String, String> getFilterField() {
		if( filterField == null){
			filterField = new HashMap<String, String>();
			filterField.put("actionId" , CodeBook.EQUAL_HQL_ID);
			filterField.put("nodeInstId" , CodeBook.LIKE_HQL_ID);
			filterField.put("actionType" , CodeBook.LIKE_HQL_ID);
			filterField.put("actionTime" , CodeBook.LIKE_HQL_ID);
			filterField.put("userCode" , CodeBook.LIKE_HQL_ID);
			filterField.put("roleType" , CodeBook.LIKE_HQL_ID);
			filterField.put("roleCode" , CodeBook.LIKE_HQL_ID);

		}
		return filterField;
	}

	@Transactional(propagation= Propagation.MANDATORY)
	public List<ActionLog> listUserActionLogs(String userCode, PageDesc pageDesc, Date lastTime )
	{
        if(lastTime==null)
            return listObjects("From ActionLog where userCode=? order by actionTime desc" ,
                    new Object[]{userCode},pageDesc ); 
        else
            return listObjects("From ActionLog where userCode=? and actionTime >= ? order by  actionTime desc" ,
                    new Object[]{userCode,lastTime},pageDesc ); 

	}
	
	/**
	 * 查询受委托的工作记录
	 * @param userCode
	 * @param pageDesc
	 * @return
	 */
	@Transactional(propagation= Propagation.MANDATORY)
	public List<ActionLog> listGrantedActionLog(String userCode, PageDesc pageDesc){
	    return listObjects("from ActionLog where userCode=? and  grantor <> null",userCode, pageDesc);
	}
	
	/**
	 * 查询委托别人做的工作记录
	 * @param userCode
	 * @param pageDesc
	 * @return
	 */
	@Transactional(propagation= Propagation.MANDATORY)
    public List<ActionLog> listGrantorActionLog(String userCode,
                                                PageDesc pageDesc) {
        return listObjects("from ActionLog where grantor = ?", userCode,
                pageDesc);
    }
	/**
	 * 生成流程日志操作编号
	 * @return long
	 */
	@Transactional(propagation= Propagation.MANDATORY)
	public long getNextActionId(){
		String sNo = DatabaseOptUtils.getNextValueOfSequence(this,"S_ACTIONLOGNO");
		return Long.valueOf(sNo);
	}
}
