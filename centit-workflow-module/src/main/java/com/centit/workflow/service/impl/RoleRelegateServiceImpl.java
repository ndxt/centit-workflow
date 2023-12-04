package com.centit.workflow.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.RoleRelegateDao;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.RoleRelegateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    private void pretreatment(RoleRelegate roleRelegate) {
        if (roleRelegate.getRelegateTime() == null) {
            roleRelegate.setRelegateTime(new Date());
        }
        if (StringUtils.isBlank(roleRelegate.getRoleType() )) {
            if(StringUtils.isBlank(roleRelegate.getOptId())) {
                roleRelegate.setRoleType("XZ");
            } else {
                roleRelegate.setRoleType("OP");
                roleRelegate.setRoleCode(null);
                roleRelegate.setUnitCode(null);
            }
        } else {
            roleRelegate.setOptId(null);
        }
        if (roleRelegate.getIsValid() == null) {
            roleRelegate.setIsValid("T");
        }
    }
    @Override
    @Transactional
    public void saveRelegate(RoleRelegate roleRelegate) {
        pretreatment(roleRelegate);
        roleRelegate.setRecordDate(new Date());
        roleRelegateDao.saveNewObject(roleRelegate);
    }

    @Override
    @Transactional
    public void updateRelegate(RoleRelegate roleRelegate) {
        pretreatment(roleRelegate);
        roleRelegateDao.updateObject(new String[]{
            "grantor", "grantee", "isValid", "roleType", "roleCode",
            "unitCode", "optId", "expireTime", "relegateTime", "recordDate",
            "grantDesc"
        }, roleRelegate);
    }

    @Override
    public JSONArray listRelegateListByUser(Map<String, Object> filterMap, PageDesc pageDesc) {
        String topUnit = StringBaseOpt.castObjectToString(filterMap.get("topUnit"));
        JSONArray relegateList = roleRelegateDao.listRelegateListByUser(filterMap, pageDesc);
        if(relegateList!=null && !relegateList.isEmpty()){
            for(Object obj : relegateList){
                if(obj instanceof JSONObject){
                    JSONObject jsonObject = (JSONObject) obj;
                    String unitCode = jsonObject.getString("unitCode");
                    if(StringUtils.isNotBlank(unitCode)){
                        jsonObject.put("unitName",
                            CodeRepositoryUtil.getValue(CodeRepositoryUtil.UNIT_CODE, unitCode));
                    }

                    String roleCode = jsonObject.getString("roleCode");
                    if(StringUtils.isNotBlank(roleCode) && StringUtils.isNotBlank(topUnit)){
                        jsonObject.put("roleName",
                            CodeRepositoryUtil.getValue(
                                topUnit+"-RT", roleCode));
                    }
                }
            }
        }
        return relegateList;
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
            roleRelegateDao.updateObject(new String[]{"isValid"},relegate);
        }
    }

    @Override
    public void batchRelegateByOp(RoleRelegate roleRelegate, String relateType, List<String> optIds) {
        roleRelegateDao.deleteOptRelegate(roleRelegate.getGrantor(), roleRelegate.getGrantee());
        if("all".equalsIgnoreCase(relateType)){
            roleRelegate.setOptId(null);
            roleRelegate.setRelegateNo(UuidOpt.getUuidAsString22());
            roleRelegateDao.saveNewObject(roleRelegate);
        } else {
            for(String optId : optIds){
                roleRelegate.setOptId(optId);
                roleRelegate.setRelegateNo(UuidOpt.getUuidAsString22());
                roleRelegateDao.saveNewObject(roleRelegate);
            }
        }
    }
}
