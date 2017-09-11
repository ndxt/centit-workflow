package com.centit.workflow.service.impl;

import com.centit.framework.components.impl.AbstractUserUnitFilterCalcContext;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.IUserUnit;

import java.util.List;

/**
 * Created by codefan on 17-9-11.
 */
public class JdbcUserUnitFilterCalcContext extends AbstractUserUnitFilterCalcContext {

    @Override
    public List<? extends IUserInfo> listAllUserInfo() {
        return null;
    }

    @Override
    public List<? extends IUnitInfo> listAllUnitInfo() {
        return null;
    }

    @Override
    public IUnitInfo getUnitInfoByCode(String unitCode) {
        return null;
    }

    @Override
    public List<? extends IUserUnit> listAllUserUnits() {
        return null;
    }

    @Override
    public List<? extends IUserUnit> listUnitUsers(String unitCode) {
        return null;
    }

    @Override
    public IUserInfo getUserInfoByCode(String userCode) {
        return null;
    }

    /**
     * 从数据字典中获取 Rank 的等级
     *
     * @param rankCode 行政角色代码
     * @return 行政角色等级
     */
    @Override
    public int getXzRank(String rankCode) {
        return 0;
    }
}
