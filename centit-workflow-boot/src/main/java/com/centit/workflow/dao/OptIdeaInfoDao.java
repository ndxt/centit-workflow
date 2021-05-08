package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.workflow.po.OptIdeaInfo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liu_cc
 * @create 2021-05-08 11:09
 */
@Repository
public class OptIdeaInfoDao extends BaseDaoImpl<OptIdeaInfo, String> {
    @Override
    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("procId", CodeBook.EQUAL_HQL_ID);
        filterField.put("nodeInstId", CodeBook.EQUAL_HQL_ID);
        filterField.put("flowInstId", CodeBook.EQUAL_HQL_ID);
        filterField.put("nodeCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("nodeName", CodeBook.EQUAL_HQL_ID);
        filterField.put("ideaCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("userCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("userName", CodeBook.EQUAL_HQL_ID);
        filterField.put("unitCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("unitName", CodeBook.EQUAL_HQL_ID);
        filterField.put("grantor", CodeBook.EQUAL_HQL_ID);
        return filterField;
    }
}
