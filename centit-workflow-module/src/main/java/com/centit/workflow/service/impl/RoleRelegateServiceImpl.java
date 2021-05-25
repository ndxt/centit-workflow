package com.centit.workflow.service.impl;

import com.centit.framework.core.dao.CodeBook;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.RoleRelegateDao;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.RoleRelegateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liu_cc
 * @create 2021-05-24 16:27
 */
@Service
@Slf4j
public class RoleRelegateServiceImpl implements RoleRelegateService {

    @Autowired
    private RoleRelegateDao roleRelegateDao;

    @Override
    @Transactional
    public void saveRelegate(RoleRelegate roleRelegate) {
        if (roleRelegate.getRelegateTime() == null) {
            roleRelegate.setRelegateTime(new Date());
        }
        if (roleRelegate.getRoleType() == null) {
            roleRelegate.setRoleType("XZ");
        }
        if (roleRelegate.getIsValid() == null) {
            roleRelegate.setIsValid("T");
        }
        roleRelegate.setRecordDate(new Date());

        // 判断是否是重复委托
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("grantor", roleRelegate.getGrantor());
        filterMap.put("grantee", roleRelegate.getGrantee());
        filterMap.put("roleType", roleRelegate.getRoleType());
        if (roleRelegate.getRoleCode() != null) {
            filterMap.put("roleCode", roleRelegate.getRoleCode());
        }
        if (roleRelegate.getOptId() != null) {
            filterMap.put("optId", roleRelegate.getOptId());
        }
        RoleRelegate oldRoleRelegate = roleRelegateDao.getObjectByProperties(filterMap);
        if (oldRoleRelegate == null) {
            roleRelegateDao.mergeObject(roleRelegate);
        }
    }

    @Override
    @Transactional
    public void updateRelegate(RoleRelegate roleRelegate) {
        saveRelegate(roleRelegate);
    }

    @Override
    public List<RoleRelegate> getRelegateListByGrantor(Map<String, Object> filterMap, PageDesc pageDesc) {
        filterMap.put(CodeBook.SELF_ORDER_BY, "GRANTEE, OPT_ID, RELEGATE_TIME, EXPIRE_TIME, RECORD_DATE DESC");
        return roleRelegateDao.listObjectsByProperties(filterMap, pageDesc);
    }

    //审批角色列表
    @Override
    public List<RoleRelegate> listRoleRelegates(Map<String, Object> filterMap, PageDesc pageDesc) {
        return roleRelegateDao.listObjectsByProperties(filterMap, pageDesc);
    }

    @Override
    public void deleteRoleRelegate(String relegateNo) {
        roleRelegateDao.deleteObjectById(relegateNo);
    }

    @Override
    public void changeRelegateValid(RoleRelegate roleRelegate) {
        String valid = roleRelegate.getIsValid();
        RoleRelegate relegate = roleRelegateDao.getObjectById(roleRelegate.getRelegateNo());
        String isValid = relegate.getIsValid();
        if (!isValid.equalsIgnoreCase(valid)) {
            relegate.setIsValid(valid);
            roleRelegateDao.updateObject(relegate);
        }
    }
}
