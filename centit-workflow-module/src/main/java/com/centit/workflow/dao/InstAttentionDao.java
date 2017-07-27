package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.workflow.po.InstAttention;
import com.centit.workflow.po.InstAttentionId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InstAttentionDao extends BaseDaoImpl<InstAttention,InstAttentionId>
	{
		//public static final Logger logger = LoggerFactory.getLogger(WfInstAttentionDao.class);
	@Override
	public Map<String, String> getFilterField() {
		if( filterField == null){
			filterField = new HashMap<String, String>();

			filterField.put("userCode" , "cid.userCode=:userCode");
			
			filterField.put("flowInstId" , "cid.flowInstId=:flowInstId");

			filterField.put("attSetTime" , CodeBook.LIKE_HQL_ID);

			filterField.put("attSetUser" , CodeBook.LIKE_HQL_ID);

			filterField.put("attSetMemo" , CodeBook.LIKE_HQL_ID);

		}
		return filterField;
	}
	/**
	 * 删除一个流程的所有关注
	 * @param flowInstId
	 */
	@Transactional(propagation= Propagation.MANDATORY)
	public void deleteFlowAttention(long flowInstId) {
	    DatabaseOptUtils.doExecuteHql(this,"delete from InstAttention where cid.flowInstId=?",flowInstId);
	}
	/**
     * 获得一个流程的所有关注
     * @param flowInstId
     */
	@Transactional(propagation= Propagation.MANDATORY)
    public List<InstAttention> listAttentionByFlowInstId(long flowInstId) {
        return this.listObjects( "From InstAttention where cid.flowInstId=?",flowInstId);
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public List<InstAttention> listAttentionByFlowInstId(long flowInstId, String optUser) {
        return this.listObjects( "From InstAttention where cid.flowInstId=? and attSetUser=? ",
                new Object[]{flowInstId,optUser});
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public void deleteFlowAttentionByOptUser(long flowInstId,String optUser)
    {
        DatabaseOptUtils.doExecuteHql(this, "delete From InstAttention where cid.flowInstId=? and attSetUser=? ",
                new Object[]{flowInstId,optUser});
    }
}
