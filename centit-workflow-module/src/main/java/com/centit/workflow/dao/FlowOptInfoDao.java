package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.workflow.po.FlowOptInfo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen_rj on 2018-5-8.
 */
@Repository
public class FlowOptInfoDao extends BaseDaoImpl<FlowOptInfo, String> {
    @Override
    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("optName" , CodeBook.LIKE_HQL_ID);
        filterField.put("optId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("applicationId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("ownerUnit" , CodeBook.EQUAL_HQL_ID);
        filterField.put(CodeBook.ORDER_BY_HQL_ID , " updateDate desc ");
        return filterField;
    }

    public String getOptInfoSequenceId() {
        Long optInfoSequenceId = DatabaseOptUtils.getSequenceNextValue(this, "S_WFOPTINFO");
        return String.valueOf(optInfoSequenceId);
    }
}
