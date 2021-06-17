package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.support.algorithm.UuidOpt;
import com.centit.workflow.po.RoleRelegate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RoleRelegateDao extends BaseDaoImpl<RoleRelegate, String> {
    //public static final Logger logger = LoggerFactory.getLogger(RoleRelegateDao.class);

    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("relegateNo", CodeBook.EQUAL_HQL_ID);
        filterField.put("grantor", CodeBook.EQUAL_HQL_ID);
        filterField.put("grantee", CodeBook.EQUAL_HQL_ID);
        filterField.put("isValid", CodeBook.EQUAL_HQL_ID);
        filterField.put("(date)relegateTime", "relegateTime < :relegateTime");
        filterField.put("(date)expireTime", "expireTime > :expireTime");
        filterField.put("unitCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("roleType", CodeBook.EQUAL_HQL_ID);
        filterField.put("roleCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("grantDesc", CodeBook.LIKE_HQL_ID);
        filterField.put("recorder", CodeBook.EQUAL_HQL_ID);
        filterField.put("recordDate", CodeBook.EQUAL_HQL_ID);
        filterField.put("optId", CodeBook.EQUAL_HQL_ID);

        filterField.put(CodeBook.ORDER_BY_HQL_ID, "recordDate desc");
        return filterField;
    }

    @Transactional
    public void saveObject(RoleRelegate roleRelegate) {
        if (StringUtils.isBlank(roleRelegate.getRelegateNo())) {
            roleRelegate.setRelegateNo(UuidOpt.getUuidAsString32());
        }
        super.mergeObject(roleRelegate);
    }

    public List<RoleRelegate> getRelegateListByGrantor(String grantor) {
        return this.listObjectsByFilter(" where grantor = ? ORDER BY GRANTEE, RELEGATE_TIME, EXPIRE_TIME, RECORD_DATE DESC ",
            new Object[]{grantor});
    }
}
