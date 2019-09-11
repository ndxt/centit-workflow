package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowInstanceGroup;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 流程实例分组
 *
 */
@Repository
public class FlowInstanceGroupDao extends BaseDaoImpl<FlowInstanceGroup, String> {

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put("flowGroupId" , CodeBook.EQUAL_HQL_ID);
            filterField.put("flowGroupName" , CodeBook.LIKE_HQL_ID);
            filterField.put("flowGroupDesc" , CodeBook.LIKE_HQL_ID);
        }
        return filterField;
    }
}
