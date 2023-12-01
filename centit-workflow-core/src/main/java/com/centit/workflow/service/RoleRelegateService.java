package com.centit.workflow.service;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleRelegate;

import java.util.List;
import java.util.Map;

/**
 * @author liu_cc
 * @create 2021-05-24 16:26
 */
public interface RoleRelegateService {
    void saveRelegate(RoleRelegate roleRelegate);

    void updateRelegate(RoleRelegate roleRelegate);

    List<RoleRelegate> getRelegateListByGrantor(Map<String, Object> filterMap, PageDesc pageDesc);

    List<RoleRelegate> listRoleRelegates(Map<String, Object> filterMap, PageDesc pageDesc);

    void deleteRoleRelegate(String relegateNo);

    void changeRelegateValid(RoleRelegate roleRelegate);

    void batchRelegateByOp(RoleRelegate roleRelegate, String relateType, List<String> optIds);
}
