package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowWarning;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowWarningDao extends BaseDaoImpl<FlowWarning,Long>
{

	//public static final Logger logger = LoggerFactory.getLogger(WfRuntimeWarningDao.class);
		
	public Map<String, String> getFilterField() {
		if( filterField == null){
			filterField = new HashMap<String, String>();
			filterField.put("warningId" , CodeBook.EQUAL_HQL_ID);
			filterField.put("nodeInstId" , CodeBook.LIKE_HQL_ID);
			filterField.put("flowInstId" , CodeBook.LIKE_HQL_ID);
			filterField.put("flowStage" , CodeBook.LIKE_HQL_ID);
			filterField.put("warningType" , CodeBook.LIKE_HQL_ID);
			filterField.put("warningCode" , CodeBook.LIKE_HQL_ID);
			filterField.put("warningTime" , CodeBook.LIKE_HQL_ID);
			filterField.put("warningState" , CodeBook.LIKE_HQL_ID);
			filterField.put("warningidMsg" , CodeBook.LIKE_HQL_ID);
			filterField.put("sendMsgTime" , CodeBook.LIKE_HQL_ID);
			filterField.put("sendUsers" , CodeBook.LIKE_HQL_ID);
		}
		return filterField;
	}

	@Transactional(propagation= Propagation.MANDATORY)
	public List<FlowWarning> listFlowWarningByInst(Long flowInstId,
                                                   PageDesc pageDesc) {
        return this.listObjects("from FlowWarning where flowInstId = ?",
                 flowInstId, pageDesc);        
    }

	@Transactional(propagation= Propagation.MANDATORY)
	public List<FlowWarning> listFlowWarningByNodeInst(Long nodeInstId,
                                                       PageDesc pageDesc) {
        return this.listObjects("from FlowWarning where nodeInstId = ?",
                nodeInstId, pageDesc);  
    }

 
    public List<FlowWarning> listFlowWarningByWarningCode(String warningCode,
                                                          PageDesc pageDesc) {
        return this.listObjects("from FlowWarning where warningCode = ?",
                warningCode, pageDesc);  
    }
    
    public List<FlowWarning> listNeedNotifyWarning(){
        return this.listObjects("from FlowWarning where noticeState = '0'");
    }
    
}
