package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.workflow.po.ApprovalEvent;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Repository
public class ApprovalEventDao extends BaseDaoImpl<ApprovalEvent,Long> {
    public Map<String, String> getFilterField() {
        if( filterField == null){
            filterField = new HashMap<String, String>();
            filterField.put("approvalId" , CodeBook.EQUAL_HQL_ID);
            filterField.put("approvalState" , CodeBook.LIKE_HQL_ID);
            filterField.put("CURRENT_PHASE" , CodeBook.LIKE_HQL_ID);
        }
        return filterField;
    }
}
