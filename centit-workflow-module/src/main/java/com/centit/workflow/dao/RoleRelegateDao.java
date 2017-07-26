package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.workflow.po.RoleRelegate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RoleRelegateDao extends BaseDaoImpl<RoleRelegate,Long> {
    //public static final Logger logger = LoggerFactory.getLogger(WfRoleRelegateDao.class);

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();

            filterField.put("relegateNo", CodeBook.EQUAL_HQL_ID);

            filterField.put("grantor", CodeBook.EQUAL_HQL_ID);

            filterField.put("grantee", CodeBook.EQUAL_HQL_ID);

            filterField.put("isValid", CodeBook.LIKE_HQL_ID);

            filterField.put("relegateTime", "relegateTime like to_date(?,'yyyy-mm-dd')");

            filterField.put("expireTime", "expireTime like to_date(?,'yyyy-mm-dd')");

            filterField.put("unitCode", CodeBook.LIKE_HQL_ID);

            filterField.put("roleType", CodeBook.LIKE_HQL_ID);

            filterField.put("roleCode", CodeBook.LIKE_HQL_ID);

            filterField.put("grantDesc", CodeBook.LIKE_HQL_ID);

            filterField.put("recorder", CodeBook.LIKE_HQL_ID);

            filterField.put("recordDate", CodeBook.LIKE_HQL_ID);

            filterField.put(CodeBook.ORDER_BY_HQL_ID , "recordDate desc");
        }
        return filterField;
    }
    @Transactional(propagation= Propagation.MANDATORY)
    public long getNextReleGateId() {
        String sNo = DatabaseOptUtils.getNextValueOfSequence(this,"S_RELEGATENO");
        return Long.valueOf(sNo);
    }
    @Transactional(propagation= Propagation.MANDATORY)
    @Override
    public void saveObject(RoleRelegate roleRelegate) {
        if (roleRelegate.getRelegateno() == null
                || roleRelegate.getRelegateno() == 0) {
            roleRelegate.setRelegateno(getNextReleGateId());
        }
        super.saveNewObject(roleRelegate);
    }
}
