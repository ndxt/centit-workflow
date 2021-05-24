package com.centit.workflow.service.impl;

import com.centit.workflow.dao.RoleRelegateDao;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.RoleRelegateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
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
    public RoleRelegate saveRelegate(RoleRelegate roleRelegate) {
        if (roleRelegate.getRelegateTime() == null) {
            roleRelegate.setRelegateTime(new Date());
        }
        roleRelegate.setRecordDate(new Date());
        // 判断是否是重复委托
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("grantor", roleRelegate.getGrantor());
        filterMap.put("grantee", roleRelegate.getGrantee());
        filterMap.put("roleType", roleRelegate.getRoleType());
        filterMap.put("role_code", roleRelegate.getRoleCode());
        RoleRelegate oldRoleRelegate = roleRelegateDao.getObjectByProperties(filterMap);
        if (oldRoleRelegate == null) {
            // 新增
            roleRelegateDao.saveObject(roleRelegate);
        }
        return roleRelegate;
    }

    @Override
    public RoleRelegate updateRelegate(RoleRelegate roleRelegate) {
        return null;
    }
}
