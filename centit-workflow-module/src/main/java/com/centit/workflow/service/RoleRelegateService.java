package com.centit.workflow.service;

import com.centit.workflow.po.RoleRelegate;

/**
 * @author liu_cc
 * @create 2021-05-24 16:26
 */
public interface RoleRelegateService {
    RoleRelegate saveRelegate(RoleRelegate roleRelegate);

    RoleRelegate updateRelegate(RoleRelegate roleRelegate);
}
